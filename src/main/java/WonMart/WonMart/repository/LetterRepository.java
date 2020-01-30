package WonMart.WonMart.repository;

import WonMart.WonMart.domain.Letter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LetterRepository {

    private final EntityManager em;

    public void save(Letter letter) {
        em.persist(letter);
    }

    public Letter findOne(Long id) {
        return em.find(Letter.class, id);
    }

    public List<Letter> findAll() {
        return em.createQuery("select l from Letter l", Letter.class)
                .getResultList();
    }

    // 송신자 쪽지 조회
    public List<Letter> findBySender(String sender) {
        return em.createQuery("select l from Letter l where l.sender = :sender", Letter.class)
                .setParameter("sender", sender)
                .getResultList();
    }

    // 수신자 쪽지 조회
    public List<Letter> findbyReceiver(String receiver) {
        return em.createQuery("select l from Letter l where l.receiver = :receiver", Letter.class)
                .setParameter("receiver", receiver)
                .getResultList();
    }

}
