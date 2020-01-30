package WonMart.WonMart.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/*
 Form : 프런트에서 원하는 정보를 담아올 수 있게끔 폼 클래스를 구현
 */
@Getter @Setter
public class MemberForm {

    @NotEmpty(message = "닉네임 정보는 필수입니다")
    private String nickName;

    @NotEmpty(message = "지역구 정보는 필수입니다")
    private String city;

    @NotEmpty(message = "도로명 정보는 필수입니다")
    private String street;

}
