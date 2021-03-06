package WonMart.WonMart.validator;

import WonMart.WonMart.controller.form.MemberForm;
import WonMart.WonMart.domain.Member;
import WonMart.WonMart.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/*
 Member Form 의 필드에 유효성을 검사하기 위해 커스터미이징 한 클래스
 */
@RequiredArgsConstructor
public class MemberNickNameValidator implements ConstraintValidator<MemberForm.UniqueNickName, String> {

    private final MemberRepository memberRepository;

    @Override
    public void initialize(MemberForm.UniqueNickName constraintAnnotation) {

    }

    @Override
    public boolean isValid(String nickName, ConstraintValidatorContext context) {
        Member findMember = memberRepository.findByNickName(nickName);
        if(findMember != null) {
            return false;
        } else {
            return true;
        }
    }
}