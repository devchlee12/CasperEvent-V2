package softeer.team_pineapple_be.global.common.domain.enums;

import lombok.Getter;

/**
 * 캐시 타입
 */
@Getter
public enum CacheType {
  QUIZ_CONTENT("quizContent", 24 * 60 * 60, 10),
  REWARD_INFO("rewardInfo", 24 * 60 * 60, 1),
  QUIZ_INFO("quizInfo", 24 * 60 * 60, 10),
  DRAW_PROBABILITY("drawProbability", 24 * 60 * 60, 10);

  private String cacheName;
  private int expiredAfterWrite;
  private int maximumSize;

  CacheType(String cacheName, int expiredAfterWrite, int maximumSize) {
    this.cacheName = cacheName;
    this.expiredAfterWrite = expiredAfterWrite;
    this.maximumSize = maximumSize;
  }
}
