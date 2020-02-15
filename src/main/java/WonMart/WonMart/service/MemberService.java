package WonMart.WonMart.service;

import WonMart.WonMart.domain.Address;
import WonMart.WonMart.domain.Member;
import WonMart.WonMart.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 Service : Repository를 단순히 위임만 하는 용도
           view로부터 전달된 파라미터가 온전한 객체로 Repository로 전달 후 처리될 수 있도록 필터를 하는 역할
           Transaction이 시작되고 종료되는 지점
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService { // 생성, 삭제, 수정, 조회

    // RequiredArgsConstructor 을 통해 초기화
    private final MemberRepository memberRepository;

    /*
     기본 Transaction은 read only로 정의되어있다
     write가 필요한 메서드에 annotation을 재정의한다
     Transaction이 시작됨을 의미 함수가 리턴되면서 영속성 컨텍스트가 반환되고 DB에 데이터가 저장된다
     */
    @Transactional
    public Long join(Member member) {
        // validateDuplicateNickName(member.getNickName());
        memberRepository.save(member);
        return member.getId();
    }

    // 중복 닉네임을 확인하는 로직 -> validation으로 대체
    private void validateDuplicateNickName(String nickName) {
        Member findMember = memberRepository.findByNickName(nickName);
        if(findMember != null) {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        }
    }

    @Transactional
    public void quit(Member member) {
        memberRepository.remove(member);
    }

    @Transactional
    public void updateMember(Long id, String nickName, Address address) {
        /*
         - 변경감지
         영속성 컨텍스트에 올라온 객체(findOne에 의해 호출됨)는 그 내용이 수정된 후에
         transaction이 끝나고 영속성 컨텍스트가 반환될때 변경사항이 데이터베이스에 저장된다
         */

        Member member = memberRepository.findOne(id);
        /* validation 검사 Controller에서 하게끔 수정
        if(!member.getNickName().equals(nickName)) {
            // validateDuplicateNickName(nickName);
        }
        */
        member.setNickName(nickName);
        member.setAddress(address);
    }

    public Member findOne(Long id) {
        return memberRepository.findOne(id);
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findBySocialKey(String socialKey) {
        return memberRepository.findBySocialKey(socialKey);
    }

    public Member findByNickName(String nickName) {
        return memberRepository.findByNickName(nickName);
    }

}
