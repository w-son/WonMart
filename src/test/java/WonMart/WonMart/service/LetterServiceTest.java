package WonMart.WonMart.service;

import WonMart.WonMart.domain.Letter;
import WonMart.WonMart.repository.LetterRepository;
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
public class LetterServiceTest {

    @Autowired LetterService letterService;
    @Autowired LetterRepository letterRepository;

    @Test
    public void 쪽지생성() throws Exception {
        // given
        Letter letter = new Letter();
        letter.setSender("A");
        letter.setReceiver("B");
        letter.setBody("Hello World");
        // when
        letterRepository.save(letter);
        // then
        Letter findLetter = (Letter) letterRepository.findBySender("A");
        assertEquals(letter, findLetter);
    }

}