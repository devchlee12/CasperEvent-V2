package softeer.team_pineapple_be.global.common.utils;

import java.security.SecureRandom;

import lombok.experimental.UtilityClass;

/**
 * 랜덤 유틸 클래스
 */
@UtilityClass
public class RandomUtils {
  private static final SecureRandom secureRandom = new SecureRandom();

  public Integer getAuthCode() {
    return 100000 + secureRandom.nextInt(900000);
  }

  public Integer getSecureRandomNumberLessThen(int max) {
    return secureRandom.nextInt(max);
  }

  public Integer getBase56Index(){return secureRandom.nextInt(0,56);}
}
