package softeer.team_pineapple_be.domain.draw.batch;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import softeer.team_pineapple_be.domain.draw.service.DrawService;

@Component
@RequiredArgsConstructor
public class DrawDailyBatch {
  private final DrawService drawService;

  @Scheduled(cron = "0 0 0 * * *")
  public void dailyInfoBatch(){
    drawService.getDrawDailyScenario(LocalDate.now());
  }
}
