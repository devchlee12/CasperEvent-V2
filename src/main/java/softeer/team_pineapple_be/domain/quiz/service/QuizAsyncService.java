package softeer.team_pineapple_be.domain.quiz.service;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import softeer.team_pineapple_be.domain.fcfs.domain.FcfsInfoEntity;
import softeer.team_pineapple_be.domain.fcfs.repository.FcfsInfoRepository;

@Service
@RequiredArgsConstructor
public class QuizAsyncService {
    private final FcfsInfoRepository fcfsInfoRepository;

    @Async
    public void saveFcfsInfo(String uuid, Long order) {
        fcfsInfoRepository.save(new FcfsInfoEntity(uuid, order.intValue()));
    }
}
