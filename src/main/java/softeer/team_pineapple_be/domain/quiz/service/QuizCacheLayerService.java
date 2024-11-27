package softeer.team_pineapple_be.domain.quiz.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import softeer.team_pineapple_be.domain.quiz.domain.QuizInfo;
import softeer.team_pineapple_be.domain.quiz.dto.response.QuizInfoCacheResponse;
import softeer.team_pineapple_be.domain.quiz.exception.QuizErrorCode;
import softeer.team_pineapple_be.domain.quiz.repository.QuizInfoRepository;
import softeer.team_pineapple_be.domain.quiz.dto.request.QuizInfoRequest;
import softeer.team_pineapple_be.domain.quiz.dto.response.QuizInfoResponse;
import softeer.team_pineapple_be.global.exception.RestApiException;

@Service
@RequiredArgsConstructor
public class QuizCacheLayerService {
    private final QuizInfoRepository quizInfoRepository;

    /**
     * 퀴즈 정보를 캐싱하는 메서드
     * @param quizId 퀴즈 아이디
     * @return 퀴즈 이미지와 정답
     */
    @Cacheable(value = "quizInfo",key = "#quizId",cacheManager = "redisCacheManager")
    public QuizInfoCacheResponse getQuizInfoCache(Integer quizId){
        QuizInfo quizInfo = quizInfoRepository.findById(quizId)
                .orElseThrow(() -> new RestApiException(QuizErrorCode.NO_QUIZ_INFO));
        return new QuizInfoCacheResponse(quizInfo.getQuizImage(), quizInfo.getAnswerNum());
    }
}
