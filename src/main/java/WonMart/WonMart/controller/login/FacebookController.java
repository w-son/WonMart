package WonMart.WonMart.controller.login;

import WonMart.WonMart.controller.form.MemberForm;
import WonMart.WonMart.domain.Member;
import WonMart.WonMart.service.MemberService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

@Controller
@RequiredArgsConstructor
public class FacebookController {

    private final MemberService memberService;
    private final SessionController sessionController;

    private final static String CLIENT_ID = "888171918306283";
    private final static String REDIRECT_URI = "http://localhost:8080/facebookLogin";
    private final static String SECRET_ID = "d22043ce3664ffc98c9f37353923f61e";

    public String getAccessToken(String code) throws URISyntaxException {

        final String requestURL = "https://graph.facebook.com/v6.0/oauth/access_token";

        /*
         BasicNameValuePair 대신 URIBuilder를 활용해서
         queryString을 처리해보는 방식을 구현해 보았음
         */

        URIBuilder builder = new URIBuilder(requestURL);
        builder.setParameter("client_id", CLIENT_ID)
                .setParameter("redirect_uri", REDIRECT_URI)
                .setParameter("client_secret", SECRET_ID)
                .setParameter("code", code);

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(builder.build());
        JsonNode retNode = null;

        try {
            final HttpResponse response = client.execute(get);
            ObjectMapper mapper = new ObjectMapper();
            retNode = mapper.readTree(response.getEntity().getContent());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String accessToken = retNode.get("access_token").toString();
        return accessToken.substring(1, accessToken.length() - 1);
    }

    public JsonNode getFaceBookUserInfo(String accessToken) throws URISyntaxException {

        URIBuilder builder = new URIBuilder("https://graph.facebook.com/v6.0/me");

        builder.setParameter("fields", "id,name")
                .setParameter("access_token", accessToken);

        final HttpClient client = HttpClientBuilder.create().build();
        final HttpGet post = new HttpGet(builder.build());
        JsonNode retNode = null;

        try {
            final HttpResponse response = client.execute(post);
            ObjectMapper mapper = new ObjectMapper();
            retNode = mapper.readTree(response.getEntity().getContent());

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return retNode;
    }

    @RequestMapping("/facebookLogin")
    public String facebookLogin(@RequestParam("code") String code, HttpSession session, Model model)
            throws URISyntaxException, UnsupportedEncodingException {

        String accessToken = getAccessToken(code);
        JsonNode userInfo = getFaceBookUserInfo(accessToken);

        String socialKey = userInfo.get("id").toString();

        sessionController.clearSession(session);

        session.setAttribute("access_token", accessToken);
        session.setAttribute("socialKey", socialKey);

        Member findMember = memberService.findBySocialKey(socialKey);
        if(findMember == null) {
            // 회원가입을 한 적 없는 경우
            model.addAttribute("memberForm", new MemberForm());
            return "member/createMemberForm";

        } else {
            // 회원가입을 한 적 있는 경우
            sessionController.setSession(session, findMember.getId(), findMember.getNickName(), findMember.getAddress());
            return "redirect:/post";
        }

    }

}
