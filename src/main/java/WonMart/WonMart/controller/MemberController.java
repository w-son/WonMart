package WonMart.WonMart.controller;

import WonMart.WonMart.controller.login.SessionController;
import WonMart.WonMart.domain.Address;
import WonMart.WonMart.domain.Member;
import WonMart.WonMart.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController { // 회원가입, 회원조회, 회원수정, 회원탈퇴

    private final MemberService memberService;
    private final SessionController sessionController;

    @PostMapping("/member/new")
    public String create(@Valid MemberForm form, BindingResult result, HttpSession session) {
        // @Valid : form에 빈 값이 들어가지 않게 해주는 annotation
        if(result.hasErrors()) {
            return "member/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet());
        Member member = new Member();
        member.setSocialKey(session.getAttribute("socialKey").toString());
        member.setNickName(form.getNickName());
        member.setAddress(address);
        Long member_id = memberService.join(member);

        // 세션에 멤버의 정보를 저장
        sessionController.setSession(session, member_id, member.getNickName(), member.getAddress());

        return "redirect:/post";
    }

    @GetMapping("/member/info")
    public String memberInfo(Model model) {
        model.addAttribute("memberForm", new MemberForm());

        return "member/memberInfo";
    }

    @GetMapping("/member")
    public String members(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);

        return "member/memberList";
    }

    @PostMapping("/member/info")
    public String updateMemberInfo(@Valid MemberForm form, BindingResult result,  HttpSession session) {

        if(result.hasErrors()) {
            /*
             수정 정보 처리
             기존에 있던 닉네임 그대로 주소만 바꿨을 때 예
             기존의 닉네임을 중복 닉네임으로 인식하게되는 예외를 처리
             */
            Member findMember = memberService.findOne((Long) session.getAttribute("member_id"));
            if(!findMember.getNickName().equals(form.getNickName())) {
                return "member/memberInfo";
            }
        }

        String nickName = form.getNickName();
        Address address = new Address(form.getCity(), form.getStreet());

        Long id = (Long) session.getAttribute("member_id");
        memberService.updateMember(id, nickName, address);
        Member updatedMember = memberService.findOne(id);

        sessionController.setSession(session, id, updatedMember.getNickName(), updatedMember.getAddress());

        return "redirect:/post";
    }

    @RequestMapping("/member/quit")
    public String quitMember(HttpSession session) {
        Long id = (Long) session.getAttribute("member_id");
        Member member = memberService.findOne(id);
        memberService.quit(member);

        sessionController.clearSession(session);

        return "redirect:/";
    }

}
