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
import java.io.*;
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

    public String uploadFile(MultipartFile file) throws IOException {
        /*
         Multipart로 받아온 파일을
         프로젝트 외부에 저장하는 방법
         */
        String fileName = RandomStringUtils.randomAlphanumeric(32);
        String fileUrl;
        if(!file.isEmpty()) {
            fileName = fileName + file.getOriginalFilename();
            fileUrl = "/img/post/" + fileName;
            /* 파일 프로젝트 내부, 프로젝트 빌드 경로에 저장하는 방법
            String workingDirectory = System.getProperty("user.dir");
            String buildPath = workingDirectory + "/build/resources/main/static/img/post";
            String projectPath = workingDirectory + "/src/main/resources/static/img/post";
            file.transferTo(new File(buildPath, fileName));
            file.transferTo(new File(projectPath, fileName));
            */
            File uploadDir = new File(System.getProperty("user.home") + "/Desktop/upload");
            if(!uploadDir.exists()) {
                // 디렉토리가 존재하지 않는 경우 생성
                uploadDir.mkdir();
            }
            file.transferTo(new File(uploadDir, fileName));
            // fileUrl = uploadDir + "/" + fileName;

        } else {
            fileUrl = "/img/not_ready.jpg";
        }

        return fileUrl;
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

        String title = form.getTitle();
        /*
         가격 문자열 : PostForm에서는 String
         Validation 검사 후 저장 시에는 integer형으로 변환 후 저장
         */
        int price = Integer.parseInt(form.getPrice());
        String body = form.getBody();
        String fileUrl = uploadFile(file);
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
    public String update(
            @RequestParam("file") MultipartFile file,
            @PathVariable("post_id") Long id,
            @Valid PostForm form, BindingResult result, Model model) throws IOException {

        if(result.hasErrors()) {
            Post post = postService.findOne(id);
            model.addAttribute("post", post);
            return "post/updatePost";
        }

        String title = form.getTitle();
        int price = Integer.parseInt(form.getPrice());
        String body = form.getBody();
        String fileUrl = uploadFile(file);
        postService.updatePost(id, title, price, body, fileUrl);

        return "redirect:/mypost";
    }

    @RequestMapping("/post/{post_id}/delete")
    public String delete(@PathVariable("post_id") Long id) {
        Post post = postService.findOne(id);
        postService.delete(post);

        return "redirect:/mypost";
    }

}
