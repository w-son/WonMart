package WonMart.WonMart.controller.login;

import WonMart.WonMart.controller.MemberForm;
import WonMart.WonMart.domain.Member;
import WonMart.WonMart.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class GoogleController {

    private final MemberService memberService;
    private final SessionController sessionController;


    /*
     Dispatch Servlet은 반환되는 형식이 String이냐 ModelAndView냐 Object이냐에
     해당 뷰페이지로 응답을 하게 되는 ViewResolver가 실행되냐
     문자열 그대로를 리턴하게 되는 MessageConverter가 실행되냐를 결정한다

     @ResponseBody 어노테이션을 사용하면 MessageConverter가 실행되어서
     메세지 그대로를 리턴하게 된다
     */
    @ResponseBody
    @RequestMapping(value = "/googleLogin", method = RequestMethod.POST)
    public String googleLogin(@RequestBody GoogleDTO form, HttpSession session, Model model) {

        System.out.println(form);

        String id = form.getId();
        String name = form.getName();
        String email = form.getEmail();

        sessionController.clearSession(session);

        session.setAttribute("socialKey", id);
        Member findMember = memberService.findBySocialKey(id);

        if(findMember == null) {
            return "False";

        } else {
            sessionController.setSession(session, findMember.getId(), findMember.getNickName(), findMember.getAddress());
            return "True";
        }
        /*
         ajax와 통신을 하는 경우 클라이언트의 화면에 리턴을 하는 것이 아니라
         ajax의 response에 리턴이된다
         = 여기서 view를 리턴한다고 해서 화면이 전환되지 않는다는 의미
         */
    }

}
