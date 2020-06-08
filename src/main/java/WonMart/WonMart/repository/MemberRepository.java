package WonMart.WonMart.repository;

import WonMart.WonMart.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

// Repository : 영속성 컨텍스트내에서 Entity를 직접 다루는 로직을 구현
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public void remove(Member member) { em.remove(member); }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    /******************************************/
    public List<Member> findAllWithJoinFetch() {
        return em.createQuery("select distinct m from Member m" + " join fetch m.posts p", Member.class)
                .getResultList();
    }

    public List<Member> findAllWithJoinFetchAndPaging(int offset, int limit) {
        return em.createQuery("select m from Member m", Member.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
    /******************************************/

    public Member findBySocialKey(String socialKey) {
        Member findMember;
        try {
            findMember =  em.createQuery("select m from Member m where m.socialKey = :socialKey", Member.class)
                    .setParameter("socialKey", socialKey)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
        return findMember;
    }

    public Member findByNickName(String nickName) {
        Member findMember;
        try {
            findMember = em.createQuery("select m from Member m where m.nickName = :nickName", Member.class)
                    .setParameter("nickName", nickName)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
        return findMember;
    }

}
