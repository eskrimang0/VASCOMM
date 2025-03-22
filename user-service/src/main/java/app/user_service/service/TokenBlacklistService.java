package app.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:";

    public void blacklistToken(String token, long expirationMillis) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(BLACKLIST_PREFIX + token, "true", Duration.ofMillis(expirationMillis));
    }

    public boolean isTokenBlacklisted(String token) {
        Boolean exists = redisTemplate.hasKey(BLACKLIST_PREFIX + token);
        return Boolean.TRUE.equals(exists);
    }
}
