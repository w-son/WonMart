package WonMart.WonMart.api;

import WonMart.WonMart.domain.Letter;
import WonMart.WonMart.repository.LetterRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/*
 순서 2
 내용 PostApi 참조
 */
@RestController
@RequiredArgsConstructor
public class LetterApi {

    private final LetterRepository letterRepository;

    @GetMapping("/api/v1/letters")
    public List<LetterDTO> lettersV1() {
        List<Letter> letters = letterRepository.findAll();
        List<LetterDTO> result = letters.stream()
                .map(l -> new LetterDTO(l))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v2/letters")
    public List<LetterDTO> lettersV2() {
        List<Letter> letters = letterRepository.findAllWithJoinFetch();
        List<LetterDTO> result = letters.stream()
                .map(l -> new LetterDTO(l))
                .collect(Collectors.toList());

        return result;
    }

    @Data
    static class LetterDTO {

        private Long id;
        private String sender;
        private String receiver;
        private String body;

        public LetterDTO(Letter letter) {
            this.id = letter.getId();
            this.sender = letter.getMember().getNickName();
            this.receiver = letter.getReceiver();
            this.body = letter.getBody();
        }

    }

}
