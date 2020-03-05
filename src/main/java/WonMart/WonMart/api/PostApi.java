package WonMart.WonMart.api;

import WonMart.WonMart.domain.Post;
import WonMart.WonMart.repository.PostRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
 순서 2
 X to One 조회에 관한 성능 최적화

 X to One 관계의 조회에 유의점
 1) Post 조회(Many) -> Post 내의 member(One) 조회 -> member 내의 posts 리스트 조회 -> 무한루프
    해결책 : One 이 되는(member)속성에 @JsonIgnore를 추가해준다

 2) Post 조회(Many) -> Post 내의 member(One) 조회 -> 매핑된 member 내의 모든 속성들에 쿼리문이 날라감 -> 1+1, 1+N 문제
    해결책 : 즉시로딩과 지연로딩
    전략 : 쿼리 수행 결과로 큰 비용이 들 수 있는 X to Many 관계에서는 지연로딩을, X to One에서는 즉시로딩을 사용한다

    - 즉시로딩(EAGER) 조인페치를 활용하여 매핑된 정보를 쿼리 한번에 모아서 실행
                    기존 : post 조회 1번 -> post의 member 조회 추가 1회
                    개선 : post 조회 1번 쿼리문에 member 의 정보까지 같이 쿼리

    - 지연로딩(LAZY) 매핑된 정보를 프록시 객체로 초기화 시켜 이후에 필요한 정보들에 한에서만 get 함수로 조회
                    기존 : post 조회 1번 -> post의 member 조회 추가 1회
                    개선 : post 조회 1번 쿼리문와 매핑된 member는 프록시로 초기화
                    * get 함수로 조회 시 해당 함수에 대해(= 연관 매핑된 프록시 객체에 대해) 쿼리가 추가로 발생한다 ... getMember
                      이미 조회 되어서 영속성 컨텍스트에 올라가 있는 정보라면 쿼리가 생략이 가능하다
                      따라서 지연로딩 후 get 함수 강제 초기화로 발생할 수 있는 최대 쿼리 수도 1+N 으로, 이 또한 개선이 필요한 부분이다

 3) (v2) 지연로딩 get 함수 호출 시 추가 쿼리 발생(매핑된 member같은 경우)의 개선을 위한 join fetch
    jpa 에서 제공하는 문법인 join fetch 를 통해서 프록시를 자동으로 채워 리턴하는 메서드 구현
    v1 에서 DTO 초기화시 getMember 때문에 발생하던 추가 쿼리를 생략할 수 있다

 4) join fetch 시 고려해볼만한 문제
    post -> member 를 join fetch -> member의 모든 정보가 끌려온다 = 비용이 커질 수 있다
    개선 : member 내의 원하는 정보만 가져오기 위해 join 과 DTO 재설계를 통하여 비용을 줄일 수 있음
    * https://github.com/w-son/SpringBoot/blob/master/src/main/java/jpabook/jpashop/api/OrderSimpleApiController.java 의 orderV4
      https://github.com/w-son/SpringBoot/blob/master/src/main/java/jpabook/jpashop/repository/OrderRepository.java 의 findOrderDtos
      https://github.com/w-son/SpringBoot/blob/master/src/main/java/jpabook/jpashop/repository/OrderSimpleQueryDto.java 참조
 */

@RestController
@RequiredArgsConstructor
public class PostApi {

    private final PostRepository postRepository;

    @GetMapping("/api/v1/posts")
    public List<PostDTO> postsV1() {
        List<Post> posts = postRepository.findAll();
        List<PostDTO> result = posts.stream()
                .map(p -> new PostDTO(p))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v2/posts")
    public List<PostDTO> postsV2() {
        List<Post> posts = postRepository.findAllWithJoinFetch();
        List<PostDTO> result = posts.stream()
                .map(p -> new PostDTO(p))
                .collect(Collectors.toList());

        return result;
    }

    @Data
    static class PostDTO {

        private Long id;
        private String writer;
        private String title;
        private int price;
        private String body;

        public PostDTO(Post post) {
            this.id = post.getId();
            this.writer = post.getMember().getNickName();
            this.title = post.getTitle();
            this.price = post.getPrice();
            this.body = post.getBody();
        }
    }

}
