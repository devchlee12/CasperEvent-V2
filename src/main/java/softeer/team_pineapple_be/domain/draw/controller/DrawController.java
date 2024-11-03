package softeer.team_pineapple_be.domain.draw.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import softeer.team_pineapple_be.domain.draw.request.SendPrizeRequest;
import softeer.team_pineapple_be.domain.draw.response.DrawDailyMessageResponse;
import softeer.team_pineapple_be.domain.draw.response.DrawResponse;
import softeer.team_pineapple_be.domain.draw.response.DrawRewardInfoResponse;
import softeer.team_pineapple_be.domain.draw.response.SendPrizeResponse;
import softeer.team_pineapple_be.domain.draw.service.DrawPrizeService;
import softeer.team_pineapple_be.domain.draw.service.DrawService;
import softeer.team_pineapple_be.global.auth.annotation.Auth;
import softeer.team_pineapple_be.global.auth.service.AuthMemberService;
import softeer.team_pineapple_be.global.lock.annotation.SemaphoreGuarded;

/**
 * 경품 추첨 컨트롤러
 */
@Tag(name = "경품 추첨 API", description = "경품 추첨 기능을 제공하는 API 입니다")
@RestController
@RequiredArgsConstructor
@RequestMapping("/draw")
public class DrawController {
  private final DrawService drawService;
  private final DrawPrizeService drawPrizeService;
  private final AuthMemberService authMemberService;

  @Auth
  @PostMapping
  @Operation(summary = "경품 추첨에 참여하기")
  @SemaphoreGuarded
  public ResponseEntity<DrawResponse> enterDraw() {
    return ResponseEntity.ok(drawService.enterDraw(authMemberService.getMemberPhoneNumber()));
  }

  @Operation(summary = "해당 날짜와 응모 시나리오 조회")
  @GetMapping("/scenario")
  public ResponseEntity<DrawDailyMessageResponse.DrawDailyScenario> getDrawDailyScenario() {
    return ResponseEntity.ok(drawService.getDrawDailyScenario());
  }

  @Operation(summary = "메인 페이지에서 사용할 경품 정보 받기")
  @GetMapping("/prize-infos")
  public ResponseEntity<List<DrawRewardInfoResponse>> getDrawPrizeImages() {
    List<DrawRewardInfoResponse> drawRewardImages = drawPrizeService.getDrawRewardImages().getDrawRewardInfoResponses();
    return ResponseEntity.ok(drawRewardImages);
  }

  @Auth
  @PostMapping("/rewards/send-prize")
  @Operation(summary = "당첨된 경품을 문자로 전송받기")
  public ResponseEntity<SendPrizeResponse> sendPrize(@Valid @RequestBody SendPrizeRequest request) {
    SendPrizeResponse sendPrizeResponse = drawPrizeService.sendPrizeMessage(request.getPrizeId());
    return ResponseEntity.ok(sendPrizeResponse);
  }
}
