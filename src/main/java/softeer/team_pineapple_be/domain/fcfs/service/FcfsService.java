package softeer.team_pineapple_be.domain.fcfs.service;

import java.util.Collections;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import softeer.team_pineapple_be.domain.fcfs.domain.FcfsInfoEntity;
import softeer.team_pineapple_be.domain.fcfs.dto.FcfsInfo;
import softeer.team_pineapple_be.domain.fcfs.exception.FcfsErrorCode;
import softeer.team_pineapple_be.domain.fcfs.repository.FcfsInfoRepository;
import softeer.team_pineapple_be.domain.quiz.service.QuizAsyncService;
import softeer.team_pineapple_be.global.exception.RestApiException;

/**
 * 선착순 서비스
 */
@Service
@RequiredArgsConstructor
public class FcfsService {
  private final static String FCFS_KEY = "fcfs_queue";
  private final static String FCFS_INFO_KEY = "fcfs_info";
  private final static String FCFS_LIMIT = "500";
  private final RedisTemplate<String, String> redisTemplate;
  private final FcfsInfoRepository fcfsInfoRepository;
  private final QuizAsyncService quizAsyncService;
  private final RedisScript<Long> firstComeFirstServeScript;

  /**
   * 선착순 큐 초기화
   */
  public void clearFcfsInfo() {
    redisTemplate.delete(FCFS_KEY);
    redisTemplate.delete(FCFS_INFO_KEY);
  }

  /**
   * 선착순 등록하고 순서 알려주는 메서드 응답
   *
   * @return 0 -> 선착순 등수 안에 들지 못함// 나머지 양수 -> 경품 받을 수 있는 선착순에 들었고 등수가 몇등인지
   */
  public FcfsInfo getFirstComeFirstServe() {
    String uuid = UUID.randomUUID().toString();
    //선착순 안에 들었는지 확인하고 선착순에 들었으면 redis에 uuid 저장하고 순번 반환, 못들었으면 그냥 0 반환
    Long order = redisTemplate.execute(firstComeFirstServeScript, Collections.singletonList(FCFS_KEY), FCFS_LIMIT ,FCFS_INFO_KEY, uuid);
    if (order > 0) {
      quizAsyncService.saveFcfsInfo(uuid, order);
    }
    return new FcfsInfo(uuid, order);
  }

  /**
   * 참가자의 ID로 선착순 등수를 알려주는 메서드
   *
   * @param participantId
   * @return 참가자의 등수
   */
  public Integer getParticipantOrder(String participantId) {
    Object order = redisTemplate.opsForHash().get(FCFS_INFO_KEY, participantId);
    if (order == null) {
      FcfsInfoEntity fcfsInfoEntity = fcfsInfoRepository.findByParticipantId(participantId)
              .orElseThrow(
                      () -> new RestApiException(FcfsErrorCode.NOT_FOR_REWARD));
      return fcfsInfoEntity.getSuccessOrder();
    }
    return Integer.valueOf(order.toString());
  }
}
