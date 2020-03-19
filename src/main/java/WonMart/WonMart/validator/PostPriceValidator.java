package WonMart.WonMart.validator;

import WonMart.WonMart.controller.form.PostForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/*
 Post Form 의 필드에 유효성을 검사하기 위해 커스터미이징 한 클래스
 */
public class PostPriceValidator implements ConstraintValidator<PostForm.ValidPrice, String> {

    @Override
    public void initialize(PostForm.ValidPrice constraintAnnotation) {

    }

    // 빈 문자열이거나 숫자로만 구성되지 않은 문자열의 유효성을 검사
    @Override
    public boolean isValid(String price, ConstraintValidatorContext context) {
        if(price.equals("")) {
            return false;
        }
        try {
            Integer.parseInt(price);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
