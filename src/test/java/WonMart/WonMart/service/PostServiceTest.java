package WonMart.WonMart.service;

import WonMart.WonMart.domain.Post;
import WonMart.WonMart.repository.PostRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class PostServiceTest {

    @Autowired PostService postService;
    @Autowired PostRepository postRepository;

    @Test
    public void 포스트생성() throws Exception {
        // given

        // when

        // then
    }

}