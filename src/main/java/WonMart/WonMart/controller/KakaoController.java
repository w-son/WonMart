package WonMart.WonMart.controller;

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
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/*
 REST api 키 : 46755ba94eb707cd876dbfd1b716ceab
 redirect uri : http://localhost:8080/kakaoLogin

 기본적인 REST api 사용 방식
 HTTP client을 통해서
 REST 메서드에 맞는 uri 변수를 생성
 -> execute한 변수를 response에 저장
 -> response를 ObjectMapping 한 후에 원하는 정보를 JsonNode에 담아서 리턴
 */
@Controller
@RequiredArgsConstructor
public class KakaoController {

    private final MemberService memberService;

    private final static String CLIENT_ID = "46755ba94eb707cd876dbfd1b716ceab";
    private final static String REDIRECT_URI = "http://localhost:8080/kakaoLogin";

    /*
     https://kauth.kakao.com/oauth/authorize
     ?client_id=46755ba94eb707cd876dbfd1b716ceab
     &redirect_uri=http://localhost:8080/kakaoLogin
     &response_type=code
     이 링크를 통해서 접근 토큰을 생성할 수 있는 code 를 GET한다
     */

    // code 를 통해 접근 토큰 생성
    public String getAccessToken(String authorize_code) {
        final String RequestUrl = "https://kauth.kakao.com/oauth/token";

        final List<NameValuePair> postParams = new ArrayList<>();
        postParams.add(new BasicNameValuePair("grant_type", "authorization_code"));
        postParams.add(new BasicNameValuePair("client_id", CLIENT_ID));
        postParams.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI));
        postParams.add(new BasicNameValuePair("code", authorize_code));

        final HttpClient client = HttpClientBuilder.create().build();
        final HttpPost post = new HttpPost(RequestUrl);
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

    // 접근 토큰을 통해서 사용자 정보 추출
    public JsonNode getKakaoUserInfo(String accessToken) {

        final String RequestUrl = "https://kapi.kakao.com/v1/user/me";
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

    @RequestMapping("/kakaoLogin")
    public String kakaoLogin(Model model, @RequestParam("code") String code, HttpSession session) throws Exception {

        // 코드를 통해 접근토큰을 생성하고 생성된 토큰을 이용해 사용자의 정보를 불러온다
        String accessToken = getAccessToken(code);
        JsonNode userInfo = getKakaoUserInfo(accessToken);

        // 카카오 아이디값을 통해 member들을 유일하게 식별할 것이다
        String kakaoKey = userInfo.get("id").toString();
        String email = userInfo.get("kaccount_email").toString();
        String nickname = userInfo.get("properties").get("nickname").toString();
        String image = userInfo.get("properties").get("profile_image").toString();

        System.out.println(kakaoKey);
        System.out.println(email);
        System.out.println(nickname);
        System.out.println(image);

        session.setAttribute("access_token", null);
        session.setAttribute("kakaoKey", null);
        session.setAttribute("member_id", null);
        session.setAttribute("nickName", null);
        session.setAttribute("address", null);

        /*
         현재 세션에 로그인 정보를 추가한다
         session : 전체 페이지에서 유효함
         model : 현재 페이지에서 유효함
         */
        session.setAttribute("access_token", accessToken);
        session.setAttribute("kakaoKey", kakaoKey);
        /* 테스트 코드
        session.setAttribute("email", email);
        session.setAttribute("nickName", nickname);
        session.setAttribute("image", image);
        */

        Member findMember = memberService.findByKakaoKey(kakaoKey);
        if(findMember == null) {
            // 회원가입을 한 적 없는 경우
            model.addAttribute("memberForm", new MemberForm());
            return "member/createMemberForm";

        } else {
            /*
             회원가입을 한 적 있는 경우
             세션에 member_id와 nickName을 추가한 후 첫 화면으로 redirect
             */
            session.setAttribute("member_id", findMember.getId());
            session.setAttribute("nickName", findMember.getNickName());
            session.setAttribute("address", findMember.getAddress());
            return "redirect:/";
        }
    }

    // 로그아웃 : 미완성
    @RequestMapping("/kakaoLogout")
    public String kakaoLogout(HttpSession session) {

        if(session.getAttribute("member_id") != null) {

            try {
                String accessToken = session.getAttribute("access_token").toString();
                String RequestUrl = "https://kapi.kakao.com/v1/user/logout";

                final HttpClient client = HttpClientBuilder.create().build();
                final HttpPost post = new HttpPost(RequestUrl);

                post.addHeader("Authorization", "Bearer " + accessToken);
                JsonNode returnNode = null;

                final HttpResponse response = client.execute(post);
                ObjectMapper mapper = new ObjectMapper();
                returnNode = mapper.readTree(response.getEntity().getContent());

                // 저장했던 세션 정보를 모두 제거
                session.removeAttribute("access_token");
                session.removeAttribute("kakaoKey");
                session.removeAttribute("member_id");
                session.removeAttribute("nickName");
                session.removeAttribute("address");

            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return "redirect:/";
    }

}