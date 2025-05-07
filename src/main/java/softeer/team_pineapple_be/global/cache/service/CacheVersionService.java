package softeer.team_pineapple_be.global.cache.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CacheVersionService {
  private final RedisTemplate<String, String> redisTemplate;

  public Long getCacheVersion(String key){
    String version = redisTemplate.opsForValue().get(key);
    if (version == null){
      redisTemplate.opsForValue().set(key, "1");
      return 1L;
    }
    return Long.valueOf(version);
  }

  public void upCacheVersion(String key){
    redisTemplate.opsForValue().increment(key, 1);
  }
}
