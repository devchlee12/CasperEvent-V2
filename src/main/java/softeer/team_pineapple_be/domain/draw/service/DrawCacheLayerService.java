package softeer.team_pineapple_be.domain.draw.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import softeer.team_pineapple_be.domain.draw.domain.DrawRewardInfo;
import softeer.team_pineapple_be.domain.draw.exception.DrawErrorCode;
import softeer.team_pineapple_be.domain.draw.repository.DrawProbabilityRepository;
import softeer.team_pineapple_be.domain.draw.repository.DrawRewardInfoRepository;
import softeer.team_pineapple_be.domain.draw.response.DrawRewardInfoResponse;
import softeer.team_pineapple_be.domain.draw.response.DrawRewardInfoResponsesWrapper;
import softeer.team_pineapple_be.domain.quiz.enums.CacheVersionKey;
import softeer.team_pineapple_be.global.cache.service.CacheVersionService;
import softeer.team_pineapple_be.global.exception.RestApiException;

@Service
@RequiredArgsConstructor
public class DrawCacheLayerService {
  private final DrawRewardInfoRepository drawRewardInfoRepository;
  private final CacheVersionService cacheVersionService;
  private final DrawProbabilityRepository drawProbabilityRepository;

  /**
   * 응모 경품 이미지를 반환하는 메서드
   */
  @Transactional(readOnly = true)
  @Cacheable(value = "rewardInfo", key = "#version")
  public DrawRewardInfoResponsesWrapper getDrawRewardInfoCache(Long version) {
    List<DrawRewardInfo> all = drawRewardInfoRepository.findAll();
    Long cacheVersion = cacheVersionService.getCacheVersion(
        CacheVersionKey.DRAW_PROBABILITY_INFO_VERSION.name());
    return new DrawRewardInfoResponsesWrapper(all.stream().map(info ->
      {
        Integer drawProbabilityByRanking = getDrawProbabilityCache(info.getRanking(), cacheVersion);
        return DrawRewardInfoResponse.of(info, drawProbabilityByRanking);
      }
    ).toList());
  }

  @Cacheable(value = "drawProbability", key = "#ranking + ':' + #version")
  @Transactional(readOnly = true)
  public Integer getDrawProbabilityCache(Byte ranking, Long version) {
    return drawProbabilityRepository.findById(ranking)
        .orElseThrow(() -> new RestApiException(DrawErrorCode.NO_PRIZE_PROBABILITY))
        .getProbability();
  }

  public void increaseDrawRewardInfoCacheVersion(){
    cacheVersionService.upCacheVersion(CacheVersionKey.DRAW_REWARD_INFO_VERSION.name());
  }

  public void increaseDrawProbabilityCacheVersion(){
    cacheVersionService.upCacheVersion(CacheVersionKey.DRAW_PROBABILITY_INFO_VERSION.name());
  }
}
