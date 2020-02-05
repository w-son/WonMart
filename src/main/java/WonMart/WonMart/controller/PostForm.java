package WonMart.WonMart.controller;

import WonMart.WonMart.domain.Member;
import WonMart.WonMart.validator.MemberNickNameValidator;
import WonMart.WonMart.validator.PostPriceValidator;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Getter @Setter
public class PostForm {

    @NotEmpty(message = "제목을 입력해 주세요")
    private String title;

    @ValidPrice
    private String price;

    @NotEmpty(message = "내용을 입력해 주세요")
    private String body;

    /*
     PostValidator 기준에 의해 필드 유효성을 검사하는
     어노테이션 커스터마이징
    */
    @Constraint(validatedBy = PostPriceValidator.class)
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ValidPrice {

        String message() default "숫자로만 구성된 가격을 입력해주세요";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

}
