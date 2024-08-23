package softeer.team_pineapple_be.domain.draw.service;

import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.web.multipart.MultipartFile;
import softeer.team_pineapple_be.domain.admin.domain.EventDayInfo;
import softeer.team_pineapple_be.domain.admin.repisotory.EventDayInfoRepository;
import softeer.team_pineapple_be.domain.comment.domain.Comment;
import softeer.team_pineapple_be.domain.comment.repository.CommentRepository;
import softeer.team_pineapple_be.domain.draw.domain.*;
import softeer.team_pineapple_be.domain.draw.exception.DrawErrorCode;
import softeer.team_pineapple_be.domain.draw.repository.*;
import softeer.team_pineapple_be.domain.draw.request.DrawDailyMessageModifyRequest;
import softeer.team_pineapple_be.domain.draw.response.*;
import softeer.team_pineapple_be.domain.member.domain.Member;
import softeer.team_pineapple_be.domain.member.exception.MemberErrorCode;
import softeer.team_pineapple_be.domain.member.repository.MemberRepository;
import softeer.team_pineapple_be.global.auth.service.AuthMemberService;
import softeer.team_pineapple_be.global.cloud.service.S3DeleteService;
import softeer.team_pineapple_be.global.cloud.service.S3UploadService;
import softeer.team_pineapple_be.global.cloud.service.exception.S3ErrorCode;
import softeer.team_pineapple_be.global.exception.RestApiException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static kotlin.reflect.jvm.internal.impl.builtins.StandardNames.FqNames.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DrawServiceTest {

    @InjectMocks
    private DrawService drawService;

    @Mock
    private DrawDailyMessageInfoRepository drawDailyMessageInfoRepository;

    @Mock
    private DrawHistoryRepository drawHistoryRepository;

    @Mock
    private DrawPrizeRepository drawPrizeRepository;

    @Mock
    private DrawProbabilityRepository drawProbabilityRepository;

    @Mock
    private DrawRewardInfoRepository drawRewardInfoRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RandomDrawPrizeService randomDrawPrizeService;

    @Mock
    private AuthMemberService authMemberService;

    @Mock
    private EventDayInfoRepository eventDayInfoRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private S3UploadService s3UploadService;

    @Mock
    private S3DeleteService s3DeleteService;

    private String phoneNumber;
    private Byte prizeRank;
    private DrawDailyMessageInfo drawDailyMessageInfo;
    private DrawPrize drawPrize;
    private List<DrawPrize> drawPrizeList; // 값이 들어간 리스트
    private DrawRewardInfo rewardInfo ; // 값이 들어간 리스트로 초기화

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        phoneNumber = "010-1234-5678";
        prizeRank = 2;
        drawDailyMessageInfo = new DrawDailyMessageInfo("Win message", "Lose message", "Lose scenario", "Win image", "Lose image","Common Scenario", LocalDate.now());
        drawPrize = new DrawPrize(2L, "prize_image_url", true, phoneNumber, null);
        drawPrizeList = new ArrayList<>(List.of(drawPrize));
        rewardInfo = new DrawRewardInfo(prizeRank, "Prize", 1 ,"image",drawPrizeList);
    }

    @Test
    @DisplayName("사용자가 응모에 성공적으로 참여한 케이스 테스트 - SuccessCase")
    void enterDraw_DrawIsSuccessful_ReturnWinningResponse() {
        // Given
        Member member = new Member(phoneNumber);
        member.incrementToolBoxCnt(); // 툴박스 개수 증가
        member.generateCar(); // 차량 보유 설정

        when(authMemberService.getMemberPhoneNumber()).thenReturn(phoneNumber);
        when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(member));
        when(randomDrawPrizeService.drawPrize()).thenReturn(prizeRank);
        when(drawRewardInfoRepository.findById(prizeRank)).thenReturn(Optional.of(rewardInfo));
        when(drawDailyMessageInfoRepository.findByDrawDate(LocalDate.now())).thenReturn(Optional.of(drawDailyMessageInfo));
        when(drawPrizeRepository.findFirstByDrawRewardInfoAndValid(rewardInfo, true)).thenReturn(Optional.of(drawPrize));

        // When
        DrawResponse response = drawService.enterDraw();

        // Then
        verify(drawHistoryRepository).save(any(DrawHistory.class));
        assertThat(rewardInfo.getStock()).isEqualTo(0); // 재고 감소 확인
        assert response instanceof DrawWinningResponse;
    }

    @Test
    @DisplayName("사용자가 응모에 성공적으로 참여했으나 경품 재고가 없어 당첨에 실패한 케이스 - SuccessCase")
    void enterDraw_PrizeIsNotAvailable_ReturnLoseResponse() {
        // Given
        Member member = new Member(phoneNumber);
        member.incrementToolBoxCnt(); // 툴박스 개수 증가
        member.generateCar(); // 차량 보유 설정

        when(authMemberService.getMemberPhoneNumber()).thenReturn(phoneNumber);
        when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(member));
        when(randomDrawPrizeService.drawPrize()).thenReturn(prizeRank);
        when(drawRewardInfoRepository.findById(prizeRank)).thenReturn(Optional.of(new DrawRewardInfo(prizeRank, "Prize", 0, null))); // 재고 없음
        when(drawDailyMessageInfoRepository.findByDrawDate(LocalDate.now())).thenReturn(Optional.of(drawDailyMessageInfo));
        when(commentRepository.findByPhoneNumberAndPostTimeBetween(eq(phoneNumber), any(), any())).thenReturn(Optional.of(new Comment()));

        // When
        DrawResponse response = drawService.enterDraw();

        // Then
        verify(drawHistoryRepository).save(any(DrawHistory.class));
        assertThat(response).isInstanceOf(DrawLoseResponse.class);
    }

    @Test
    @DisplayName("사용자가 참여하려고 했으나 존재하지 않는 멤버인 케이스 - FailureCase")
    void enterDraw_MemberNotFound_ThrowRestApiException() {
        // Given
        when(authMemberService.getMemberPhoneNumber()).thenReturn(phoneNumber);
        when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> drawService.enterDraw())
                .isInstanceOf(RestApiException.class)
                .satisfies(exception -> {
                    RestApiException restApiException = (RestApiException) exception; // 캐스팅
                    assertThat(restApiException.getErrorCode()).isEqualTo(MemberErrorCode.NO_MEMBER);
                });
    }

    @Test
    @DisplayName("사용자가 참여 자격이 없는 케이스 - FailureCase")
    void enterDraw_CannotEnterDraw_ThrowRestApiException() {
        // Given
        Member member = new Member(phoneNumber);
        member.decrementToolBoxCnt(); // 툴박스 개수 감소
        // 차량 보유 설정 없음
        when(authMemberService.getMemberPhoneNumber()).thenReturn(phoneNumber);
        when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(member));

        // When & Then
        assertThatThrownBy(() -> drawService.enterDraw())
                .isInstanceOf(RestApiException.class)
                .satisfies(exception -> {
                    RestApiException restApiException = (RestApiException) exception; // 캐스팅
                    assertThat(restApiException.getErrorCode()).isEqualTo(DrawErrorCode.CANNOT_ENTER_DRAW);
                });
    }

    @Test
    @DisplayName("사용자가 응모에 참여하려고 했으나 응모 참여 가능한 날짜가 아닌 케이스 - FailureCase")
    void enterDraw_DailyMessageNotExists_ThrowRestApiException() {
        // Given
        Member member = new Member(phoneNumber);
        member.incrementToolBoxCnt(); // 툴박스 개수 증가
        member.generateCar(); // 차량 보유 설정

        when(authMemberService.getMemberPhoneNumber()).thenReturn(phoneNumber);
        when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(member));
        when(randomDrawPrizeService.drawPrize()).thenReturn(prizeRank);
        when(drawRewardInfoRepository.findById(prizeRank)).thenReturn(Optional.of(rewardInfo));
        when(drawDailyMessageInfoRepository.findByDrawDate(LocalDate.now())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> drawService.enterDraw())
                .isInstanceOf(RestApiException.class)
                .satisfies(exception -> {
                    RestApiException restApiException = (RestApiException) exception; // 캐스팅
                    assertThat(restApiException.getErrorCode()).isEqualTo(DrawErrorCode.NOT_VALID_DATE);
                });
    }

    @Test
    @DisplayName("순위에 존재하지 않는 상품이 뽑힌 케이스 - FailureCase")
    void enterDraw_NoPrizeFound_ThrowRestApiException() {
        // Given

        Member member = new Member(phoneNumber);
        member.incrementToolBoxCnt(); // 툴박스 개수 증가
        member.generateCar();
        when(authMemberService.getMemberPhoneNumber()).thenReturn(phoneNumber);
        when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(member)); // Member 객체 추가
        when(drawRewardInfoRepository.findById(prizeRank)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> drawService.enterDraw())
                .isInstanceOf(RestApiException.class)
                .satisfies(exception -> {
                    RestApiException restApiException = (RestApiException) exception; // 캐스팅
                    assertThat(restApiException.getErrorCode()).isEqualTo(DrawErrorCode.NO_PRIZE);
                });
    }

    @Test
    @DisplayName("상품이 유효하지 않은 케이스 - FailureCase")
    void enterDraw_NoValidPrizeFound_ThrowRestApiException() {
        // Given
        Member member = new Member(phoneNumber);
        member.incrementToolBoxCnt(); // 툴박스 개수 증가
        member.generateCar();
        when(authMemberService.getMemberPhoneNumber()).thenReturn(phoneNumber);
        when(memberRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(member)); // Member 객체 추가
        when(randomDrawPrizeService.drawPrize()).thenReturn(prizeRank);
        when(drawDailyMessageInfoRepository.findByDrawDate(LocalDate.now())).thenReturn(Optional.of(drawDailyMessageInfo));
        DrawRewardInfo rewardInfo = new DrawRewardInfo(prizeRank, "Prize", 1,"image", new ArrayList<>());
        when(drawRewardInfoRepository.findById(prizeRank)).thenReturn(Optional.of(rewardInfo));
        when(drawPrizeRepository.findFirstByDrawRewardInfoAndValid(rewardInfo, true)).thenReturn(Optional.empty()); // 빈 Optional 반환

        // When & Then
        assertThatThrownBy(() -> drawService.enterDraw())
                .isInstanceOf(RestApiException.class)
                .satisfies(exception -> {
                    RestApiException restApiException = (RestApiException) exception; // 캐스팅
                    assertThat(restApiException.getErrorCode()).isEqualTo(DrawErrorCode.NO_VALID_PRIZE);
                });
    }

    @Test
    void updateOrSaveDailyMessageInfo_TwoExist_DrawDailyInfoExist_SuccessCase(){
        String winMessage = "winMessage";
        String loseMessage = "loseMessage";
        String loseScenario = "loseScenario";
        MultipartFile winImage = mock(MultipartFile.class);
        MultipartFile loseImage = mock(MultipartFile.class);
        String commonScenario = "commonScenario";
        LocalDate drawDate = LocalDate.now();
        DrawDailyMessageModifyRequest drawDailyMessageModifyRequest = new DrawDailyMessageModifyRequest(winMessage, loseMessage, loseScenario, winImage, loseImage, commonScenario, drawDate);
        when(drawDailyMessageInfoRepository.findByDrawDate(drawDate)).thenReturn(Optional.empty());

        drawService.updateOrSaveDailyMessageInfo(drawDailyMessageModifyRequest);
    }

    @Test
    void updateOrSaveDailyMessageInfo_ImageUploadFail_FailureCase() throws Exception{
        String winMessage = "winMessage";
        String loseMessage = "loseMessage";
        String loseScenario = "loseScenario";
        MultipartFile winImage = mock(MultipartFile.class);
        MultipartFile loseImage = mock(MultipartFile.class);
        String commonScenario = "commonScenario";
        LocalDate drawDate = LocalDate.now();
        String fileName = "daily-win-image/" + drawDate +"/";
        DrawDailyMessageModifyRequest drawDailyMessageModifyRequest = new DrawDailyMessageModifyRequest(winMessage, loseMessage, loseScenario, winImage, null, commonScenario, drawDate);
        when(drawDailyMessageInfoRepository.findByDrawDate(drawDate)).thenReturn(Optional.of(drawDailyMessageInfo));

        doThrow(new IOException("S3 upload failed")).when(s3UploadService).saveFile(winImage, fileName);

        //  public String saveFile(MultipartFile multipartFile, String fileName) throws IOException {
        assertThatThrownBy(() -> drawService.updateOrSaveDailyMessageInfo(drawDailyMessageModifyRequest))
                .isInstanceOf(RestApiException.class)
                .satisfies(exception -> {
                    RestApiException restApiException = (RestApiException) exception; // 캐스팅
                    assertThat(restApiException.getErrorCode()).isEqualTo(S3ErrorCode.IMAGE_FAILURE);
                });
    }

    @Test
    void updateOrSaveDailyMessageInfo_TwoExist_DrawDailyInfoNotExist_SuccessCase(){
        String winMessage = "winMessage";
        String loseMessage = "loseMessage";
        String loseScenario = "loseScenario";
        MultipartFile winImage = mock(MultipartFile.class);
        MultipartFile loseImage = mock(MultipartFile.class);
        String commonScenario = "commonScenario";
        LocalDate drawDate = LocalDate.now();
        DrawDailyMessageModifyRequest drawDailyMessageModifyRequest = new DrawDailyMessageModifyRequest(winMessage, loseMessage, loseScenario, winImage, loseImage, commonScenario, drawDate);
        when(drawDailyMessageInfoRepository.findByDrawDate(drawDate)).thenReturn(Optional.of(drawDailyMessageInfo));

        drawService.updateOrSaveDailyMessageInfo(drawDailyMessageModifyRequest);
    }

    @Test
    void updateOrSaveDailyMessageInfo_TwoNotExist_SuccessCase(){
        String winMessage = "winMessage";
        String loseMessage = "loseMessage";
        String loseScenario = "loseScenario";
        MultipartFile winImage = mock(MultipartFile.class);
        MultipartFile loseImage = mock(MultipartFile.class);
        String commonScenario = "commonScenario";
        LocalDate drawDate = LocalDate.now();
        DrawDailyMessageModifyRequest drawDailyMessageModifyRequest = new DrawDailyMessageModifyRequest(winMessage, loseMessage, loseScenario, null, null, commonScenario, drawDate);
        when(drawDailyMessageInfoRepository.findByDrawDate(drawDate)).thenReturn(Optional.of(drawDailyMessageInfo));
//        when(drawDailyMessageModifyRequest.getWinImage()).thenReturn(null);
//        when(drawDailyMessageModifyRequest.getLoseImage()).thenReturn(null);

        drawService.updateOrSaveDailyMessageInfo(drawDailyMessageModifyRequest);
    }

    @Test
    void updateOrSaveDailyMessageInfo_TwoNotExist_DrawDailyInfoNotExist_FailureCase(){
        String winMessage = "winMessage";
        String loseMessage = "loseMessage";
        String loseScenario = "loseScenario";
        String commonScenario = "commonScenario";
        LocalDate drawDate = LocalDate.now();
        DrawDailyMessageModifyRequest drawDailyMessageModifyRequest = new DrawDailyMessageModifyRequest(winMessage, loseMessage, loseScenario, null, null, commonScenario, drawDate);
        when(drawDailyMessageInfoRepository.findByDrawDate(drawDate)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> drawService.updateOrSaveDailyMessageInfo(drawDailyMessageModifyRequest))
                .isInstanceOf(RestApiException.class)
                .satisfies(exception -> {
                    RestApiException restApiException = (RestApiException) exception; // 캐스팅
                    assertThat(restApiException.getErrorCode()).isEqualTo(DrawErrorCode.NO_DAILY_INFO);
                });
    }

    @Test
    void updateOrSaveDailyMessageInfo_WinImageNotExist_SuccessCase(){
        String winMessage = "winMessage";
        String loseMessage = "loseMessage";
        String loseScenario = "loseScenario";
        MultipartFile winImage = mock(MultipartFile.class);
        String commonScenario = "commonScenario";
        LocalDate drawDate = LocalDate.now();
        DrawDailyMessageModifyRequest drawDailyMessageModifyRequest = new DrawDailyMessageModifyRequest(winMessage, loseMessage, loseScenario, winImage, null, commonScenario, drawDate);
        when(drawDailyMessageInfoRepository.findByDrawDate(drawDate)).thenReturn(Optional.of(drawDailyMessageInfo));

        drawService.updateOrSaveDailyMessageInfo(drawDailyMessageModifyRequest);
    }

    @Test
    void updateOrSaveDailyMessageInfo_WinImageNotExist_DrawDailyInfoNotExist_FailureCase(){
        String winMessage = "winMessage";
        String loseMessage = "loseMessage";
        String loseScenario = "loseScenario";
        MultipartFile winImage = mock(MultipartFile.class);
        String commonScenario = "commonScenario";
        LocalDate drawDate = LocalDate.now();
        DrawDailyMessageModifyRequest drawDailyMessageModifyRequest = new DrawDailyMessageModifyRequest(winMessage, loseMessage, loseScenario, winImage, null, commonScenario, drawDate);
        when(drawDailyMessageInfoRepository.findByDrawDate(drawDate)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> drawService.updateOrSaveDailyMessageInfo(drawDailyMessageModifyRequest))
                .isInstanceOf(RestApiException.class)
                .satisfies(exception -> {
                    RestApiException restApiException = (RestApiException) exception; // 캐스팅
                    assertThat(restApiException.getErrorCode()).isEqualTo(DrawErrorCode.NO_DAILY_INFO);
                });
    }

    @Test
    void updateOrSaveDailyMessageInfo_loseImageNotExist_SuccessCase(){
        String winMessage = "winMessage";
        String loseMessage = "loseMessage";
        String loseScenario = "loseScenario";
        MultipartFile loseImage = mock(MultipartFile.class);
        String commonScenario = "commonScenario";
        LocalDate drawDate = LocalDate.now();
        DrawDailyMessageModifyRequest drawDailyMessageModifyRequest = new DrawDailyMessageModifyRequest(winMessage, loseMessage, loseScenario, null, loseImage, commonScenario, drawDate);
        when(drawDailyMessageInfoRepository.findByDrawDate(drawDate)).thenReturn(Optional.of(drawDailyMessageInfo));

        drawService.updateOrSaveDailyMessageInfo(drawDailyMessageModifyRequest);
    }
    @Test
    void updateOrSaveDailyMessageInfo_loseImageNotExist_DrawDailyInfoNotExist_FailureCase(){
        String winMessage = "winMessage";
        String loseMessage = "loseMessage";
        String loseScenario = "loseScenario";
        MultipartFile loseImage = mock(MultipartFile.class);
        String commonScenario = "commonScenario";
        LocalDate drawDate = LocalDate.now();
        DrawDailyMessageModifyRequest drawDailyMessageModifyRequest = new DrawDailyMessageModifyRequest(winMessage, loseMessage, loseScenario, null, loseImage, commonScenario, drawDate);
        when(drawDailyMessageInfoRepository.findByDrawDate(drawDate)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> drawService.updateOrSaveDailyMessageInfo(drawDailyMessageModifyRequest))
                .isInstanceOf(RestApiException.class)
                .satisfies(exception -> {
                    RestApiException restApiException = (RestApiException) exception; // 캐스팅
                    assertThat(restApiException.getErrorCode()).isEqualTo(DrawErrorCode.NO_DAILY_INFO);
                });
    }

    @Test
    void testGetDrawRemaining() {
        // given
        DrawRewardInfo rewardInfo1 = new DrawRewardInfo((byte) 1, "경품1", 10, "image1.jpg");
        DrawRewardInfo rewardInfo2 = new DrawRewardInfo((byte) 2, "경품2", 5, "image2.jpg");
        when(drawRewardInfoRepository.findAll()).thenReturn(Arrays.asList(rewardInfo1, rewardInfo2));

        DrawProbability drawProbability1 = new DrawProbability((byte) 1, 100);
        DrawProbability drawProbability2 = new DrawProbability((byte) 2, 50);
        when(drawProbabilityRepository.findAll()).thenReturn(Arrays.asList(drawProbability1, drawProbability2));

        // when
        DrawRemainingResponse response = drawService.getDrawRemaining();

        // then
        assertThat(response).isNotNull();
        List<DrawRemainingResponse.DrawRemaining> drawRemainings = response.getDrawRemaining();
        assertThat(drawRemainings).hasSize(2);

        assertThat(drawRemainings.get(0).getRanking()).isEqualTo((byte) 1);
        assertThat(drawRemainings.get(0).getNowStock()).isEqualTo(10);
        assertThat(drawRemainings.get(0).getTotalStock()).isEqualTo(100);

        assertThat(drawRemainings.get(1).getRanking()).isEqualTo((byte) 2);
        assertThat(drawRemainings.get(1).getNowStock()).isEqualTo(5);
        assertThat(drawRemainings.get(1).getTotalStock()).isEqualTo(50);
    }

    @Test
    void testGetDrawDailyScenario() {
        // given
        EventDayInfo eventDayInfo = new EventDayInfo(1,LocalDate.now());
        when(eventDayInfoRepository.findByEventDate(LocalDate.now()))
                .thenReturn(Optional.of(eventDayInfo));

        when(drawDailyMessageInfoRepository.findByDrawDate(eventDayInfo.getEventDate()))
                .thenReturn(Optional.of(drawDailyMessageInfo));


        // when
        DrawDailyMessageResponse.DrawDailyScenario scenario = drawService.getDrawDailyScenario();

        // then
        assertThat(scenario).isNotNull();
        assertThat(scenario.getDay()).isEqualTo(1);
        assertThat(scenario.getCommonScenario()).isEqualTo("Common Scenario");
    }

    @Test
    void testGetDrawDailyScenario_FailureCase() {
        // given
        EventDayInfo eventDayInfo = new EventDayInfo(1,LocalDate.now());
        when(eventDayInfoRepository.findByEventDate(LocalDate.now()))
                .thenReturn(Optional.empty());

        when(drawDailyMessageInfoRepository.findByDrawDate(eventDayInfo.getEventDate()))
                .thenReturn(Optional.of(drawDailyMessageInfo));


        // when
        assertThatThrownBy(() -> drawService.getDrawDailyScenario())
                .isInstanceOf(RestApiException.class)
                .satisfies(exception -> {
                    RestApiException restApiException = (RestApiException) exception; // 캐스팅
                    assertThat(restApiException.getErrorCode()).isEqualTo(DrawErrorCode.NOT_VALID_DATE);
                });
    }

    @Test
    void testGetDrawDailyScenario_FailureCas2e() {
        // given
        EventDayInfo eventDayInfo = new EventDayInfo(1,LocalDate.now());
        when(eventDayInfoRepository.findByEventDate(LocalDate.now()))
                .thenReturn(Optional.of(eventDayInfo));

        when(drawDailyMessageInfoRepository.findByDrawDate(eventDayInfo.getEventDate()))
                .thenReturn(Optional.empty());


        // when
        assertThatThrownBy(() -> drawService.getDrawDailyScenario())
                .isInstanceOf(RestApiException.class)
                .satisfies(exception -> {
                    RestApiException restApiException = (RestApiException) exception; // 캐스팅
                    assertThat(restApiException.getErrorCode()).isEqualTo(DrawErrorCode.NO_DAILY_INFO);
                });
    }

    @Test
    void testGetDailyMessageInfo() {
        // given
        LocalDate date = LocalDate.now();
        when(drawDailyMessageInfoRepository.findByDrawDate(date))
                .thenReturn(Optional.of(drawDailyMessageInfo));

        // when
        DrawDailyMessageResponse response = drawService.getDailyMessageInfo(date);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getWinMessage()).isEqualTo("Win message");
        assertThat(response.getLoseMessage()).isEqualTo("Lose message");
        assertThat(response.getLoseScenario()).isEqualTo("Lose scenario");
        assertThat(response.getWinImage()).isEqualTo("Win image");
        assertThat(response.getLoseImage()).isEqualTo("Lose image");
        assertThat(response.getCommonScenario()).isEqualTo("Common Scenario");
        assertThat(response.getDrawDate()).isEqualTo(date);
    }

    @Test
    void testGetDailyMessageInfo_NotFound() {
        // given
        LocalDate date = LocalDate.now();
        when(drawDailyMessageInfoRepository.findByDrawDate(date))
                .thenReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> drawService.getDailyMessageInfo(date))
                .isInstanceOf(RestApiException.class)
                .satisfies(exception -> {
                    RestApiException restApiException = (RestApiException) exception; // 캐스팅
                    assertThat(restApiException.getErrorCode()).isEqualTo(DrawErrorCode.NO_DAILY_INFO);
                });
    }
}
