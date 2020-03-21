package WonMart.WonMart.repository;

import WonMart.WonMart.domain.Post;
import WonMart.WonMart.domain.PostCategory;
import WonMart.WonMart.domain.QMember;
import WonMart.WonMart.domain.QPost;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final EntityManager em;

    public void save(Post post) {
        em.persist(post);
    }

    public void remove(Post post) { em.remove(post); }

    public Post findOne(Long id) {
        return em.find(Post.class, id);
    }

    public List<Post> findAll() {
        return em.createQuery("select p from Post p", Post.class)
                .getResultList();
    }

    public List<Post> findAllWithJoinFetch() {
        return em.createQuery("select p from Post p" + " join fetch p.member m", Post.class)
                .getResultList();
    }

    // 작성자 기준 Post
    public List<Post> findByMemberId(Long id) {
        return em.createQuery("select p from Post p where p.member.id = :id", Post.class)
                .setParameter("id", id)
                .getResultList();
    }

    /*
     Query DSL
     동적 쿼리 활용 시 코드를 깔끔하게 활용할 수 있는 라이브러리
     검색 로직에 활용하면 유용하다
     */

    // 카케고리 기준 필터
    public List<Post> filterPostsByCategory(PostCategory category) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QPost post = QPost.post;

        return query
                .select(post)
                .from(post)
                .where(checkCategory(category))
                .limit(1000)
                .fetch();
    }
    private BooleanExpression checkCategory(PostCategory category) {
        if(category == null) {
            return null;
        }
        return QPost.post.category.eq(category);
    }

    // 멤버 닉네임 기준 필터
    public List<Post> filterPostsByNickname(String nickName) {
        JPAQueryFactory query = new JPAQueryFactory(em);
        QPost post = QPost.post;
        QMember member = QMember.member;

        return query
                .select(post)
                .from(post)
                .join(post.member, member)
                .where(checkNickName(nickName))
                .limit(1000)
                .fetch();
    }
    private BooleanExpression checkNickName(String nickName) {
        if(!StringUtils.hasText(nickName)) {
            return null;
        }
        return QMember.member.nickName.like(nickName);
    }

}
