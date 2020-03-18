package WonMart.WonMart.service;

import WonMart.WonMart.domain.Address;
import WonMart.WonMart.domain.Member;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;

    @Test
    public void 회원등록() throws Exception {
        // given
        Member member = new Member();
        member.setNickName("김씨");
        member.setAddress(new Address("서울시", "와우산로"));
        // when
        Long id = memberService.join(member);
        // then
        Member findMember = memberService.findOne(id);
        assertEquals(member, findMember);
    }

}