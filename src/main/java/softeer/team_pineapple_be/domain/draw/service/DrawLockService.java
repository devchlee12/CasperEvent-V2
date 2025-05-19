package softeer.team_pineapple_be.domain.draw.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.RequiredArgsConstructor;
import softeer.team_pineapple_be.domain.comment.repository.CommentRepository;
import softeer.team_pineapple_be.domain.draw.domain.DrawDailyMessageInfo;
import softeer.team_pineapple_be.domain.draw.domain.DrawHistory;
import softeer.team_pineapple_be.domain.draw.domain.DrawPrize;
import softeer.team_pineapple_be.domain.draw.domain.DrawRewardInfo;
import softeer.team_pineapple_be.domain.draw.exception.DrawErrorCode;
import softeer.team_pineapple_be.domain.draw.repository.DrawDailyMessageInfoRepository;
import softeer.team_pineapple_be.domain.draw.repository.DrawHistoryRepository;
import softeer.team_pineapple_be.domain.draw.repository.DrawPrizeRepository;
import softeer.team_pineapple_be.domain.draw.repository.DrawRewardInfoRepository;
import softeer.team_pineapple_be.domain.draw.response.DrawLoseResponse;
import softeer.team_pineapple_be.domain.draw.response.DrawResponse;
import softeer.team_pineapple_be.domain.draw.response.DrawWinningResponse;
import softeer.team_pineapple_be.domain.member.domain.Member;
import softeer.team_pineapple_be.domain.member.exception.MemberErrorCode;
import softeer.team_pineapple_be.domain.member.repository.MemberRepository;
import softeer.team_pineapple_be.global.exception.RestApiException;
import softeer.team_pineapple_be.global.lock.annotation.DistributedLock;

/**
 * 응모 락 서비스
 */
@Service
@RequiredArgsConstructor
public class DrawLockService {
  public static final Byte DRAW_LOSE = 0;
  public static final Byte DRAW_FIRST_PRIZE = 1;
  private final DrawRewardInfoRepository drawRewardInfoRepository;
  private final DrawDailyMessageInfoRepository drawDailyMessageInfoRepository;
  private final DrawHistoryRepository drawHistoryRepository;
  private final CommentRepository commentRepository;
  private final DrawPrizeRepository drawPrizeRepository;
  private final MemberRepository memberRepository;

  @DistributedLock(key = "#prizeRank")
  public @NotNull DrawResponse disposeDrawPrize(String memberPhoneNumber, DrawDailyMessageInfo dailyMessageInfo,
      Byte prizeRank, Member member) {
    DrawRewardInfo rewardInfo =
        drawRewardInfoRepository.findById(prizeRank).orElseThrow(() -> new RestApiException(DrawErrorCode.NO_PRIZE));

    if (rewardInfo.getRanking().equals(DRAW_LOSE) || rewardInfo.getStock() == 0) {
      return disposeDrawLose(memberPhoneNumber, member, dailyMessageInfo);
    }
    Long prizeId;
    try {
      prizeId = setPrizeOwner(rewardInfo, memberPhoneNumber);
    } catch (RestApiException e) {
      return disposeDrawLose(memberPhoneNumber, member, dailyMessageInfo);
    }
    drawHistoryRepository.save(new DrawHistory(rewardInfo.getRanking(), memberPhoneNumber));
    rewardInfo.decreaseStock();
    return new DrawWinningResponse(dailyMessageInfo.getWinMessage(), rewardInfo.getName(),
        dailyMessageInfo.getWinImage(), prizeId, member.isCar(), member.getToolBoxCnt());
  }

  private @NotNull DrawLoseResponse disposeDrawLose(String memberPhoneNumber, Member member,
      DrawDailyMessageInfo dailyMessageInfo) {
    LocalDate today = LocalDate.now();
    LocalDateTime startOfDay = today.atStartOfDay();
    LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
    drawHistoryRepository.save(new DrawHistory((byte) 0, memberPhoneNumber));
    boolean commentedToday =
        commentRepository.findByPhoneNumberAndPostTimeBetween(memberPhoneNumber, startOfDay, endOfDay).isPresent();
    return new DrawLoseResponse(dailyMessageInfo.getLoseMessage(), dailyMessageInfo.getLoseScenario(),
        dailyMessageInfo.getLoseImage(), member.isCar(), commentedToday, member.getToolBoxCnt());
  }

  @DistributedLock(key = "#memberPhoneNumber")
  public @NotNull Member processEnteringDraw(String memberPhoneNumber) {
    Member member = memberRepository.findByPhoneNumber(memberPhoneNumber)
        .orElseThrow(() -> new RestApiException(MemberErrorCode.NO_MEMBER));
    canEnterDraw(member);
    member.decrementToolBoxCnt();
    return member;
  }

  /**
   * 경품 추첨 자격 있는지 확인
   *
   * @param member
   */
  private void canEnterDraw(Member member) {
    if ((!member.isCar()) || (member.getToolBoxCnt() == 0)) {
      throw new RestApiException(DrawErrorCode.CANNOT_ENTER_DRAW);
    }
  }

  /**
   * 당첨된 경품에 소유자 설정
   *
   * @param rewardInfo
   * @param memberPhoneNumber
   * @return 경품 ID
   */
  private Long setPrizeOwner(DrawRewardInfo rewardInfo, String memberPhoneNumber) {
    DrawPrize prize = drawPrizeRepository.findFirstByDrawRewardInfoAndValid(rewardInfo, true)
                                         .orElseThrow(() -> new RestApiException(DrawErrorCode.NO_VALID_PRIZE));
    prize.isNowOwnedBy(memberPhoneNumber);
    prize.invalidate();
    return prize.getId();
  }
}
