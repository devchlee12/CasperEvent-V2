package softeer.team_pineapple_be.domain.draw.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DrawRewardInfoResponsesWrapper {
    List<DrawRewardInfoResponse> drawRewardInfoResponses;
}
