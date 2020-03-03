package WonMart.WonMart.api;

import WonMart.WonMart.controller.MemberForm;
import WonMart.WonMart.domain.Address;
import WonMart.WonMart.domain.Member;
import WonMart.WonMart.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.transform.Result;
import java.util.List;
import java.util.stream.Collectors;

/*
 순서 1

 Controller와 RestController의 차이
 Controller의 경우 메서드 리턴시 ViewResolver를 통해서 해당하는 view를 리턴하게 된다
 RestControlller의 경우에는 view가 아닌 해당하는 메세지를 그대로 리턴하게 된다
 메서드 앞에 @ResponseBody annotation을 붙히는 것과 같은 의미라고 보면 된다

 - Entity 가 아닌 DTO를 통한 CRUD시 이점
     1) 엔티티를 외부에 노출하지 않음
     2) API 스펙을 바꾸는 경우를 방지
     3) 유지보수에 뛰어난 설계
     4) 응답을 JSON array 형태가 아닌 dictionary의 형태로 스펙의 확장성과 자유도를 높임
        ... Result<T>

 -  요청을 보낼때 RequestBody의 형태가 JSON 형태가 되게끔 요청해야 content-type에 맞게 수신할 수 있음
 */

@RestController
@RequiredArgsConstructor
public class MemberApi { // 조회, 생성, 수정

    private final MemberService memberService;

    // 생성
    @PostMapping("/api/members/new")
    public CreateMemberResponse createMember(@RequestBody @Valid MemberForm form) {
        Member member = new Member();
        member.setNickName(form.getNickName());
        member.setAddress(new Address(form.getCity(), form.getStreet()));

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }
    @Data
    @AllArgsConstructor
    static class CreateMemberResponse {
        private Long id;
    }

    // 수정
    @PostMapping("/api/members/{id}")
    public UpdateMemberResponse updateMember(@PathVariable("id") Long id, @RequestBody @Valid MemberForm form) {
        memberService.updateMember(id, form.getNickName(), new Address(form.getCity(), form.getStreet()));
        Member updatedMember = memberService.findOne(id);
        return new UpdateMemberResponse(id, updatedMember.getNickName());
    }
    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String nickName;
    }

    // 조회
    @GetMapping("/api/members")
    public Result members() {
        List<Member> members = memberService.findMembers();
        List<MemberDTO> collect = members.stream()
                .map(m -> new MemberDTO(m.getNickName(), m.getAddress()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }
    @Data
    @AllArgsConstructor
    static class MemberDTO {
        private String nickName;
        private Address address;
    }
    @Data
    @AllArgsConstructor
    static class Result<T> {
        // 이런식으로 밑의 data 리스트 이외에 count 같은 api 스펙을 추가할 수 있다
        private int count;
        private T data;
    }

}
