package WonMart.WonMart.controller;

import WonMart.WonMart.domain.Post;
import WonMart.WonMart.service.PostService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController { // 게시글 생성, 게시글 조회, 게시글 수정, 게시글 삭제

    private final PostService postService;

    @GetMapping("/post/new")
    public String createPost(Model model) {
        model.addAttribute("postForm", new PostForm());
        return "post/createPostForm";
    }

    /*
     Requestpart 는 이미지 파일 업로드 관련 어노테이션
     */
    @PostMapping("/post/new")
    public String create(
            @RequestParam("file") MultipartFile file,
            @Valid PostForm form,
            BindingResult result,
            HttpSession session) throws IOException { // 파라미터가 많아서 나눠서 작성 했음

        if(result.hasErrors()) {
            return "post/createPostForm";
        }

        /*
         Multipart로 받아온 파일을
         static 하위에 저장하는 방법
         */
        String fileName = RandomStringUtils.randomAlphanumeric(32) + file.getOriginalFilename();
        String workingDirectory = System.getProperty("user.dir");
        String downloadPath = workingDirectory + "/src/main/resources/static/img/sample";
        file.transferTo(new File(downloadPath, fileName));

        String title = form.getTitle();
        /*
         가격 문자열 : PostForm에서는 String
         Validation 검사 후 저장 시에는 integer형으로 변환 후 저장
         */
        int price = Integer.parseInt(form.getPrice());
        String body = form.getBody();
        /* Post에 저장할 경로 */
        String fileUrl = "/img/sample/" + fileName;
        postService.post((Long)session.getAttribute("member_id"), title, price, body, fileUrl);

        return "redirect:/";
    }

    @GetMapping("/post/{post_id}/info")
    public String postInfo(@PathVariable("post_id") Long id, Model model) {
        Post post = postService.findOne(id);
        model.addAttribute("post", post);

        return "post/postInfo";
    }

    @GetMapping("/mypost")
    public String myPosts(Model model, HttpSession session) {
        Long id = (Long) session.getAttribute("member_id");
        List<Post> posts = postService.findByMemberId(id);
        model.addAttribute("posts", posts);

        return "post/myPostList";
    }

    @GetMapping("/post")
    public String posts(Model model, HttpSession session) {
        List<Post> posts = postService.findPosts();
        model.addAttribute("posts", posts);

        return "post/postList";
    }

    @GetMapping("/post/{post_id}/update")
    public String updatePost(@PathVariable("post_id") Long id, Model model) {

        Post post = postService.findOne(id);
        model.addAttribute("post", post);
        model.addAttribute("postForm", new PostForm());

        return "post/updatePost";
    }

    @PostMapping("/post/{post_id}/update")
    public String update(@PathVariable("post_id") Long id, @Valid PostForm form, BindingResult result, Model model) {
        if(result.hasErrors()) {
            Post post = postService.findOne(id);
            model.addAttribute("post", post);
            return "post/updatePost";
        }
        // 업로드 이미지 변경 처리 미완료
        String title = form.getTitle();
        int price = Integer.parseInt(form.getPrice());
        String body = form.getBody();

        postService.updatePost(id, title, price, body);

        return "redirect:/mypost";
    }

    @RequestMapping("/post/{post_id}/delete")
    public String delete(@PathVariable("post_id") Long id) {
        Post post = postService.findOne(id);
        postService.delete(post);

        return "redirect:/mypost";
    }

}
