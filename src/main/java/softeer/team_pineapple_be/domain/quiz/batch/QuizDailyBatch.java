package softeer.team_pineapple_be.domain.quiz.batch;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import softeer.team_pineapple_be.domain.quiz.dto.response.QuizContentResponse;
import softeer.team_pineapple_be.domain.quiz.service.QuizCacheLayerService;
import softeer.team_pineapple_be.domain.quiz.service.QuizRedisService;
import softeer.team_pineapple_be.domain.quiz.service.QuizService;

/**
 * 일자별 퀴즈 참여 정보 초기화 처리하는 클래스
 */
@Component
@RequiredArgsConstructor
public class QuizDailyBatch {
  private final QuizRedisService quizRedisService;
  private final QuizService quizService;
  private final QuizCacheLayerService quizCacheLayerService;

  /**
   * 매일 12시에 퀴즈 관련 배치처리
   */
  @Scheduled(cron = "0 0 12 * * *")
  @CacheEvict(value = {"quizContent", "quizInfo"}, allEntries = true, cacheManager = "redisCacheManager")
  public void quizDailyBatch() {
    quizRedisService.deleteParticipateInfo();
    QuizContentResponse quizContent = quizService.quizContentCacheWarmUp();
    quizCacheLayerService.getQuizInfoCache(quizContent.getQuizId());
  }
}
