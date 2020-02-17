package WonMart.WonMart.controller;

import WonMart.WonMart.domain.Letter;
import WonMart.WonMart.domain.Member;
import WonMart.WonMart.service.LetterService;
import WonMart.WonMart.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

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

        return "redirect:/post";
    }

    @GetMapping("/letter")
    public String letters(Model model) {
        List<Letter> letters = letterService.findLetters();
        model.addAttribute("letters", letters);

        return "letter/letterList";
    }

}
