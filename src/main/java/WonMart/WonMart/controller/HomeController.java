package WonMart.WonMart.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    /*
     RequestMapping : curl mapping 요청들 매핑, get, post, update, delete 등
     GetMapping : 매핑되는 uri로 접근하는 경우 -> 다른 uri에서 model을 갖고 action을 취해 현재 uri로의 요청을 받아 전환
     PostMapping : 매핑되는 uri에서 떠나는 경우 -> 현재 uri에서 model을 갖고 action을 취해서 다른 uri로 전환
     */

    @RequestMapping("/")
    public String home() {
        return "home";
    }

}
