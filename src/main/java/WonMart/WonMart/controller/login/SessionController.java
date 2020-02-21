package WonMart.WonMart.controller.login;

import WonMart.WonMart.domain.Address;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SessionController { // 코드 중복 최소화 하기 위해 만든 컨트롤러

    public void clearSession(HttpSession session) {
        session.setAttribute("access_token", null);
        session.setAttribute("socialKey", null);
        session.setAttribute("member_id", null);
        session.setAttribute("nickName", null);
        session.setAttribute("address", null);
    }

    public void setSession(HttpSession session, Long id, String nickName, Address address) {
        session.setAttribute("member_id", id);
        session.setAttribute("nickName", nickName);
        session.setAttribute("address", address);
    }

    // 로그아웃 : 미완성
    @RequestMapping("/logout")
    public String logout(HttpSession session) {

        /*
        kakaoLogout(session);
        naverLogout(session);
        facebookLogout(session);
        googleLogout(session);
        */

        // 저장했던 세션 정보를 모두 제거
        clearSession(session);

        return "redirect:/";
    }

    public void kakaoLogout(HttpSession session) {

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

            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void naverLogout(HttpSession session) {

        String accessToken = session.getAttribute("access_token").toString();

        final String requestUrl = "https://nid.naver.com/oauth2.0/token";

        final List<NameValuePair> postParams = new ArrayList<>();
        postParams.add(new BasicNameValuePair("grant_type", "delete"));
        postParams.add(new BasicNameValuePair("client_id", "2cUCbzWwkKNT41u9QWhB"));
        postParams.add(new BasicNameValuePair("client_secret", "YS5PYrMDH3"));
        postParams.add(new BasicNameValuePair("access_token", accessToken));

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
    }

    public void facebookLogout(HttpSession session) {
        // 미완성
    }

    public void googleLogout(HttpSession session) {
        // 미완성
    }

}
