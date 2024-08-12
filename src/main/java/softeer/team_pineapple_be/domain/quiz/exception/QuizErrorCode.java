package softeer.team_pineapple_be.domain.quiz.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import softeer.team_pineapple_be.global.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum QuizErrorCode implements ErrorCode {

  NO_QUIZ_INFO(HttpStatus.BAD_REQUEST, "퀴즈 정답이 등록되어 있지 않습니다."),
  NO_QUIZ_CONTENT(HttpStatus.BAD_REQUEST, "퀴즈 내용이 존재하지 않습니다."),
  PARTICIPATION_EXISTS(HttpStatus.FORBIDDEN, "이미 참여한 유저입니다."),
  ALREADY_WIN_REWARD_TODAY(HttpStatus.BAD_REQUEST, "이미 오늘 선착순 경품을 수령한 유저입니다."),
  NO_QUIZ_REWARD(HttpStatus.BAD_REQUEST, "선착순 경품이 존재하지 않습니다.");

  private final HttpStatus httpStatus;
  private final String message;
}
