package WonMart.WonMart.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter @Setter
public class Letter {

    @Id @GeneratedValue
    @Column(name = "letter_id")
    private Long id;

    private String sender;

    private String receiver;

    private LocalDateTime letterTime;

    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    /*
     생성메서드
     NoArgsConstructor annotation을 통해 생성 메서드로만 객체 생성
     생성된 쪽지는 송신자 멤버와 다대일 매핑되게 설계하였음
     */
    public static Letter createLetter(Member member, String sender, String receiver, String body) {
        Letter letter = new Letter();
        member.addLetter(letter);
        letter.setSender(sender);
        letter.setReceiver(receiver);
        letter.setLetterTime(LocalDateTime.now());
        letter.setBody(body);

        return letter;
    }

}
