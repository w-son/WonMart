package WonMart.WonMart.controller.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class LetterForm {

    @NotEmpty(message = "내용을 입력해주세요")
    private String body;

}
