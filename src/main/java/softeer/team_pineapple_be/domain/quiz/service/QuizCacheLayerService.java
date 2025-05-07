package softeer.team_pineapple_be.domain.quiz.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import softeer.team_pineapple_be.domain.quiz.domain.QuizContent;
import softeer.team_pineapple_be.domain.quiz.domain.QuizInfo;
import softeer.team_pineapple_be.domain.quiz.dto.response.QuizContentResponse;
import softeer.team_pineapple_be.domain.quiz.dto.response.QuizInfoCacheResponse;
import softeer.team_pineapple_be.domain.quiz.enums.CacheVersionKey;
import softeer.team_pineapple_be.domain.quiz.exception.QuizErrorCode;
import softeer.team_pineapple_be.domain.quiz.repository.QuizContentRepository;
import softeer.team_pineapple_be.domain.quiz.repository.QuizInfoRepository;
import softeer.team_pineapple_be.global.cache.service.CacheVersionService;
import softeer.team_pineapple_be.global.exception.RestApiException;

@Service
@RequiredArgsConstructor
public class QuizCacheLayerService {
    private final QuizInfoRepository quizInfoRepository;
    private final QuizContentRepository quizContentRepository;
    private final CacheVersionService cacheVersionService;

    /**
     * 퀴즈 정보를 캐싱하는 메서드
     * @param quizId 퀴즈 아이디
     * @return 퀴즈 이미지와 정답
     */
    @Cacheable(value = "quizInfo",key = "#quizId + ':' + #version")
    public QuizInfoCacheResponse getQuizInfoCache(Integer quizId, Long version){
        QuizInfo quizInfo = quizInfoRepository.findById(quizId)
                .orElseThrow(() -> new RestApiException(QuizErrorCode.NO_QUIZ_INFO));
        return new QuizInfoCacheResponse(quizInfo.getQuizImage(), quizInfo.getAnswerNum());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "quizContent", key = "#now.toString() + ':' + #version")
    public QuizContentResponse getQuizContentCache(LocalDate now, Long version){
        QuizContent quizContent = quizContentRepository.findByQuizDate(now).orElseThrow(() -> new RestApiException(QuizErrorCode.NO_QUIZ_CONTENT));
        return QuizContentResponse.of(quizContent);
    }

    public void upVersionOfQuizContent(){
        cacheVersionService.upCacheVersion(CacheVersionKey.QUIZ_CONTENT_VERSION.name());
    }

    public void upVersionOfQuizInfo(){
        cacheVersionService.upCacheVersion(CacheVersionKey.QUIZ_INFO_VERSION.name());
    }
}
