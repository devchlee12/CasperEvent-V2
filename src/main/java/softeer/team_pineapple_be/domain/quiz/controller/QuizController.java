package softeer.team_pineapple_be.domain.quiz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import softeer.team_pineapple_be.domain.member.response.MemberInfoResponse;
import softeer.team_pineapple_be.domain.quiz.request.QuizInfoRequest;
import softeer.team_pineapple_be.domain.quiz.response.QuizContentResponse;
import softeer.team_pineapple_be.domain.quiz.response.QuizInfoResponse;
import softeer.team_pineapple_be.domain.quiz.service.QuizService;

@Tag(name = "Quiz 관련 정보 제공", description = "퀴즈에 대한 처리(내용, 정답)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;

    @Operation(summary = "퀴즈 내용 가져오기")
    @GetMapping
    public ResponseEntity<QuizContentResponse> getQuizContent() {
        return ResponseEntity.ok().body(quizService.getQuizContent());
    }

    @Operation(summary = "퀴즈 정답 맞추기")
    @PostMapping("/answer")
    public ResponseEntity<QuizInfoResponse> isCorrect(@RequestBody QuizInfoRequest quizInfoRequest) {
        return ResponseEntity.ok().body(quizService.quizIsCorrect(quizInfoRequest));
    }

    @Operation(summary = "퀴즈 참여 여부 등록")
    @GetMapping("/participants")
    public ResponseEntity<MemberInfoResponse> setQuizHistory(HttpServletRequest request){
        String phoneNumber = (String) request.getSession().getAttribute("phoneNumber");
        return ResponseEntity.ok().body(quizService.quizHistory(phoneNumber));
    }

}