package WonMart.WonMart.controller;

import WonMart.WonMart.controller.form.LetterForm;
import WonMart.WonMart.domain.Letter;
import WonMart.WonMart.domain.Member;
import WonMart.WonMart.service.LetterService;
import WonMart.WonMart.service.MemberService;
import WonMart.WonMart.utility.AscendingTimeSort;
import WonMart.WonMart.utility.DescendingTimeSort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class LetterController { // 쪽지 생성, 쪽지 조회

    private final LetterService letterService;
    private final MemberService memberService;

    @GetMapping("/letter/{receiver_id}/new")
    public String createLetter(@PathVariable("receiver_id") Long id, Model model) {
        Member receiver = memberService.findOne(id);
        String receiver_nickName = receiver.getNickName();

        model.addAttribute("receiver_id", id);
        model.addAttribute("receiver_nickName", receiver_nickName);
        model.addAttribute("letterForm", new LetterForm());

        return "letter/createLetterForm";
    }

    @PostMapping("/letter/{receiver_id}/new")
    public String send(
            @PathVariable("receiver_id") Long receiver_id,
            @Valid LetterForm form, BindingResult result,
            Model model, HttpSession session) {

        if(result.hasErrors()) {
            Member receiver = memberService.findOne(receiver_id);
            String receiver_nickName = receiver.getNickName();
            model.addAttribute("receiver_nickName", receiver_nickName);
            return "letter/createLetterForm";
        }

        Long sender_id = (Long) session.getAttribute("member_id");
        letterService.send(sender_id, receiver_id, form.getBody());

        return "redirect:/letter";
    }

    @GetMapping("/letter")
    public String letters(Model model, HttpSession session) {
        /*
         1. 현재 사용자에 의해서 보내지거나 현재 사용자에게 온 쪽지를 모두 모은다
         2. 시간순(오름차순)으로 정렬하여 리스트화 한다
         3. 빠른 시간대부터 확인하면서 나에게 보내거나 내가 보낸 대화 상대자를 구해 해시맵에 적용시킨다
         4. 해시맵에 적용된 최신 쪽지를 내림차순으로 정렬하여 리스트화 시킨다
         */
        String me = (String) session.getAttribute("nickName");
        List<Letter> sentByMe = letterService.findBySender(me);
        List<Letter> sentByOthers = letterService.findByReceiver(me);

        List<Letter> temp = new ArrayList<>();
        temp.addAll(sentByMe);
        temp.addAll(sentByOthers);
        temp.sort(new AscendingTimeSort());

        HashMap<String, Letter> map = new HashMap<>();
        for(Letter letter : temp) {
            String other;
            if(letter.getSender().equals(me)) {
                other = letter.getReceiver();
            } else {
                other = letter.getSender();
            }
            map.put(other, letter);
        }
        List<Letter> letters = new ArrayList<>(map.values()); // map.keySet() 으로 키값 조회 가능
        letters.sort(new DescendingTimeSort());

        model.addAttribute("letters", letters);

        return "letter/letterList";
    }

    @GetMapping("/letter/{receiver_nickname}")
    public String room(
            @PathVariable("receiver_nickname") String receiver_nickname,
            Model model, HttpSession session) {

        Member me = memberService.findByNickName((String) session.getAttribute("nickName"));
        Member receiver = memberService.findByNickName(receiver_nickname);

        List<Letter> sent = me.getLetters();
        List<Letter> received = receiver.getLetters();

        sent.removeIf(letter -> !letter.getReceiver().equals(receiver.getNickName()));
        received.removeIf(letter -> !letter.getReceiver().equals(me.getNickName()));

        List<Letter> letters = new ArrayList<>();
        letters.addAll(sent);
        letters.addAll(received);
        letters.sort(new AscendingTimeSort());

        model.addAttribute("receiver_id", receiver.getId());
        model.addAttribute("letters", letters);

        return "letter/letterRoom";
    }

}
