package WonMart.WonMart.service;

import WonMart.WonMart.domain.Letter;
import WonMart.WonMart.domain.Member;
import WonMart.WonMart.repository.LetterRepository;
import WonMart.WonMart.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LetterService { // 생성, 조회

    private final LetterRepository letterRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long send(Long sender_id, Long receiver_id, String body) {
        Member sender = memberRepository.findOne(sender_id);
        Member receiver = userDoesNotExist(receiver_id);
        Letter letter = Letter.createLetter(sender, sender.getNickName(), receiver.getNickName(), body);
        letterRepository.save(letter);

        return letter.getId();
    }

    // 존재하는 수신자에게 쪽지를 작성하는 것인지 확인하는 로직
    private Member userDoesNotExist(Long id) {
        Member findMember = memberRepository.findOne(id);
        if(findMember == null) {
            throw new IllegalStateException("존재하지 않는 사용자입니다.");
        } else {
            return findMember;
        }
    }

    public Letter findOne(Long id) {
        return letterRepository.findOne(id);
    }

    public List<Letter> findLetters() {
        return letterRepository.findAll();
    }

    // 송신자 쪽지 조회
    public List<Letter> findBySender(String sender) {
        return letterRepository.findBySender(sender);
    }

    // 수신자 쪽지 조회
    public List<Letter> findByReceiver(String receiver) {
        return letterRepository.findbyReceiver(receiver);
    }

}