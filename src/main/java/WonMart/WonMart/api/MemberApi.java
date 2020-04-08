package WonMart.WonMart.api;

import WonMart.WonMart.controller.form.MemberForm;
import WonMart.WonMart.domain.Address;
import WonMart.WonMart.domain.Member;
import WonMart.WonMart.domain.Post;
import WonMart.WonMart.repository.MemberRepository;
import WonMart.WonMart.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    private final MemberRepository memberRepository;

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
    @GetMapping("/api/v1/members")
    public Result membersV1() {
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

    /*
     순서 3
     X to Many
     순서 2에서는 X to One에 대한 관계를 다뤘다면
     순서 3에서는 X to Many를 포함하는 연관관계에 대해 설계한다

     * X to Many를 여러 개 한방쿼리로 (member의 post 와 letter를 동시에 join fetch하는 경우) 쿼리할 수 없다
     -> MultipleBagException : 순서없고 중복 있는 Bag형태(List)를 두개 이상 조인 쿼리하면 매핑할 수 없기 때문이다
     reference : https://perfectacle.github.io/2019/05/01/hibernate-multiple-bag-fetch-exception/

     1) 엔티티 노출 방지 (v2)
        X to Many를 포함하는 엔티티는 DTO 설계 시 DTO 내 추가로 DTO를 생성하여 엔티티 노출을 막는다

     2) 쿼리 수 최적화 (v3)
        join fetch를 통해서 연관매핑된 속성들은 쿼리 한번에 끌어올 수 있게 한다
        X to One 과는 달리 X to Many는 join fetch시 중복 데이터가 발생하므로 distinct 명령어를 붙혀준다
        member와 post를 cross product 시킨 결과인 findAllWithJoinFetch의 결과로 그 이유를 알 수 있다

     3) 페이징(where in, IN 쿼리) 최적화 (v4)
        Member 객체 가, 나
        가의 Post 객체 1, 2, 3
        나의 Post 객체 4, 5, 6
        v3 를 활용시 -> cartesian product 후 row 6개 리턴 -> 가와 나 의 정보가 중복되어서 리턴된다
        X to One 관계라면 페이징에 문제가 없지만
        X to Many 관계인 컬렉션 같은 경우는 row 가 (가1, 가2, 가3, 나4, 나5, 나6) 이므로 순서대로 페이징 시 원하는 데이터를 가져올 수 없다

        * LAZY 로딩 + hibernate.default_batch_fetch_size(전역) + @BatchSize(개별)을 활용한다
        X를 기점으로 페이징을 한 후에 (1번 쿼리)
        BatchSize만큼 컬렉션에서 떼어 와서 조회한다 (컬렉션 사이즈/BatchSize 회)
        = 컬렉션을 IN 쿼리를 통해서 BatchSize만큼 가져와서 조회(LAZY의 경우 프록시 초기화)할 수 있는 것이다
        = 결과적으로 Cross Product 없이 컬렉션을 조회 할 수 있어 데이터의 중복을 방지할 수 있음
        select m from member m where p in (1, 2, 3, 4, 5, 6) <- 이렇게 in절 내에 최대 BatchSize만큼 조회

        -> findAllWithJoinFetchAndPaging을 실행하면 위의 쿼리가 진행되고 in 절 괄호 내에 Post가 조회된다
           결과적으로는 member의 데이터가 뻥튀기(?) 되지 않은 상태로 조회가 가능하고
           + in 절로 조회된 Post들은 DTO 초기화 시에 이미 영속성 캐시에 올라온 상태이므로 N + 1 문제가 해결된다
     */

    @GetMapping("/api/v2/members")
    public List<ExtendedMemberDTO> membersV2() {
        List<Member> members = memberRepository.findAll();
        List<ExtendedMemberDTO> result = members.stream()
                .map(m -> new ExtendedMemberDTO(m))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v3/members")
    public List<ExtendedMemberDTO> membersV3() {
        List<Member> members = memberRepository.findAllWithJoinFetch();
        List<ExtendedMemberDTO> result = members.stream()
                .map(m -> new ExtendedMemberDTO(m))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v4/members")
    public List<ExtendedMemberDTO> memberV4(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "1000") int limit) {
        List<Member> members = memberRepository.findAllWithJoinFetchAndPaging(offset, limit);
        List<ExtendedMemberDTO> result = members.stream()
                .map(m -> new ExtendedMemberDTO(m))
                .collect(Collectors.toList());

        return result;
    }

    @Data
    static class ExtendedMemberDTO {
        private String nickName;
        private Address address;
        private List<ExtendedPostDTO> posts;

        public ExtendedMemberDTO(Member member) {
            this.nickName = member.getNickName();
            this.address = member.getAddress();
            this.posts = member.getPosts().stream()
                    .map(post -> new ExtendedPostDTO(post))
                    .collect(Collectors.toList());
        }
    }
    @Data
    static class ExtendedPostDTO {
        private String writer;
        private String title;
        private int price;
        private String body;

        public ExtendedPostDTO(Post post) {
            this.writer = post.getMember().getNickName();
            this.title = post.getTitle();
            this.price = post.getPrice();
            this.body = post.getBody();
        }
    }

}
