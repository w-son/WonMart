package WonMart.WonMart.controller;

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

    @PostMapping("/member/new")
    public String create(@Valid MemberForm form, BindingResult result, HttpSession session) {
        // @Valid : form에 빈 값이 들어가지 않게 해주는 annotation
        if(result.hasErrors()) {
            return "member/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet());
        Member member = new Member();
        member.setKakaoKey(session.getAttribute("kakaoKey").toString());
        member.setNickName(form.getNickName());
        member.setAddress(address);
        Long member_id = memberService.join(member);

        // 세션에 멤버의 정보를 저장
        session.setAttribute("member_id", member_id);
        session.setAttribute("nickName", member.getNickName());
        session.setAttribute("address", member.getAddress());

        return "redirect:/";
    }

    @GetMapping("/member/info")
    public String memberInfo(Model model) {
        model.addAttribute("memberForm", new MemberForm());

        return "member/memberInfo";
    }

    @PostMapping("/member/info")
    public String updateMemberInfo(@Valid MemberForm form, BindingResult result,  HttpSession session) {

        if(result.hasErrors()) {
            return "member/memberInfo";
        }

        String nickName = form.getNickName();
        Address address = new Address(form.getCity(), form.getStreet());

        Long id = (Long) session.getAttribute("member_id");
        memberService.updateMember(id, nickName, address);

        Member updatedMember = memberService.findOne(id);

        session.setAttribute("nickName", updatedMember.getNickName());
        session.setAttribute("address", updatedMember.getAddress());

        return "redirect:/";
    }

    @RequestMapping("/member/quit")
    public String quitMember(HttpSession session) {
        Long id = (Long) session.getAttribute("member_id");
        Member member = memberService.findOne(id);
        memberService.quit(member);

        session.setAttribute("access_token", null);
        session.setAttribute("kakaoKey", null);
        session.setAttribute("member_id", null);
        session.setAttribute("nickName", null);
        session.setAttribute("address", null);

        return "redirect:/";
    }

    @GetMapping("/member")
    public String members(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);

        return "member/memberList";
    }

}
