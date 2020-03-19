package WonMart.WonMart.controller.form;

import WonMart.WonMart.validator.MemberNickNameValidator;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotEmpty;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 Form : 프런트에서 원하는 정보를 담아올 수 있게끔 폼 클래스를 구현
 */
@Getter @Setter
public class MemberForm {

    @NotEmpty(message = "닉네임 정보는 필수입니다")
    @UniqueNickName
    private String nickName;

    @NotEmpty(message = "지역구 정보는 필수입니다")
    private String city;

    @NotEmpty(message = "도로명 정보는 필수입니다")
    private String street;

    /*
     MemberValidator의 기준에 의해 필드 유효성을 검사하는
     어노테이션 커스터마이징
     */
    @Constraint(validatedBy = MemberNickNameValidator.class)
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UniqueNickName {

        String message() default "이미 존재하는 닉네임입니다";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

}
