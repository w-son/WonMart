package WonMart.WonMart.controller.login;

import WonMart.WonMart.controller.MemberForm;
import WonMart.WonMart.domain.Member;
import WonMart.WonMart.service.MemberService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class NaverController { // 동일한 과정에 대한 설명 주석 KakaoController 에 명시

    private final MemberService memberService;
    private final SessionController sessionController;

    private static final String CLIENT_ID = "2cUCbzWwkKNT41u9QWhB";
    private static final String CLIENT_SECRET = "YS5PYrMDH3";
    private static final String REDIRECT_URI = "http://localhost:8080/naverLogin";

    public String getAccessToken(String code) {

        final String requestUrl = "https://nid.naver.com/oauth2.0/token";

        final List<NameValuePair> postParams = new ArrayList<>();
        postParams.add(new BasicNameValuePair("grant_type", "authorization_code"));
        postParams.add(new BasicNameValuePair("client_id", CLIENT_ID));
        postParams.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
        postParams.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI));
        postParams.add(new BasicNameValuePair("code", code));

        final HttpClient client = HttpClientBuilder.create().build();
        final HttpPost post = new HttpPost(requestUrl);
        JsonNode returnNode = null;

        try {
            post.setEntity(new UrlEncodedFormEntity(postParams));
            final HttpResponse response = client.execute(post);
            final int responseCode = response.getStatusLine().getStatusCode();

            ObjectMapper mapper = new ObjectMapper();
            returnNode = mapper.readTree(response.getEntity().getContent());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnNode.get("access_token").toString();
    }

    public JsonNode getNaverUserInfo(String accessToken) {

        final String RequestUrl = "https://openapi.naver.com/v1/nid/me";
        final HttpClient client = HttpClientBuilder.create().build();
        final HttpPost post = new HttpPost(RequestUrl);

        post.addHeader("Authorization", "Bearer " + accessToken);
        JsonNode returnNode = null;

        try {
            final HttpResponse response = client.execute(post);
            final int responseCode = response.getStatusLine().getStatusCode();

            ObjectMapper mapper = new ObjectMapper();
            returnNode = mapper.readTree(response.getEntity().getContent());

        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnNode;

    }

    @RequestMapping("/naverLogin")
    public String naverLogin(@RequestParam("code") String code, HttpSession session, Model model) {

        String accessToken = getAccessToken(code);
        JsonNode userInfo = getNaverUserInfo(accessToken);

        String socialKey = userInfo.get("response").get("id").toString();
        String emil = userInfo.get("response").get("email").toString();
        String nickName = userInfo.get("response").get("nickname").toString();
        String image = userInfo.get("response").get("profile_image").toString();

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
            return "redirect:/";
        }
    }

}
