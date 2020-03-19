package WonMart.WonMart.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String title;

    private LocalDateTime postTime;

    private int price;

    // enum을 쓸 때 반드시 들어가야 하는 annotation
    @Enumerated(EnumType.STRING)
    private PostCategory category;

    // https://freehoon.tistory.com/123 텍스트 편집기 reference
    private String body;

    private String image;

    /*
     1) Many관계에 있는 엔티티에 Foreign Key가 존재하고 이 엔티티가 연관관계의 주인이다
     2) LAZY 형태로 fetch하여 N+1문제 해결, 나중에 get함수를 통해서 프록시 객체를 초기화한다
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private Member member;

    /*
     생성 메서드
     NoArgsConstructor annotation을 통해 생성 메서드로만 객체 생성
     */
    public static Post createPost(Member member, String title, int price, PostCategory category, String body, String image) {
        Post post = new Post();
        member.addPost(post);
        post.setTitle(title);
        post.setPostTime(LocalDateTime.now());
        post.setPrice(price);
        post.setCategory(category);
        post.setBody(body);
        post.setImage(image);

        return post;
    }

}
