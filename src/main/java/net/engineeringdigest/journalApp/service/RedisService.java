
package net.engineeringdigest.journalApp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {

    private final RedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RedisService(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Existing method: read JSON stored in Redis
    public <T> T get(String key, Class<T> entityClass) {
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        try {
            return objectMapper.readValue(value.toString(), entityClass);
        } catch (Exception e) {
            log.error("Failed to deserialize Redis value", e);
            throw new IllegalStateException(
                    "Failed to read data from Redis", e);
        }
    }

    // Existing method: store JSON with TTL
    public void set(String key, Object value, long ttl) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(
                    key, jsonValue, ttl, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to write Redis value", e);
            throw new IllegalStateException(
                    "Failed to write data to Redis", e);
        }
    }

    // Existing method
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // Atomically reserve a key if it does not already exist.
    public boolean setIfAbsent(String key, String value, long ttlSeconds) {
        Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(key, value, ttlSeconds, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(result);
    }

    /*
     * Result:
     *  1 = verified successfully; code consumed
     *  0 = incorrect code or no code exists
     * -1 = attempt limit reached; code invalidated
     *
     * Stored codes use JSON serialization, so the submitted code
     * is serialized the same way before comparison.
     */
    public long verifyAndConsumeCode(
            String codeKey,
            String attemptsKey,
            String submittedCode,
            int maxAttempts,
            long attemptsTtlSeconds) {

        String script =
                "local stored = redis.call('GET', KEYS[1]) " +
                        "if not stored then return 0 end " +
                        "if stored == ARGV[1] then " +
                        "  redis.call('DEL', KEYS[1]) " +
                        "  redis.call('DEL', KEYS[2]) " +
                        "  return 1 " +
                        "end " +
                        "local attempts = redis.call('INCR', KEYS[2]) " +
                        "if attempts == 1 then " +
                        "  redis.call('EXPIRE', KEYS[2], ARGV[3]) " +
                        "end " +
                        "if attempts >= tonumber(ARGV[2]) then " +
                        "  redis.call('DEL', KEYS[1]) " +
                        "  return -1 " +
                        "end " +
                        "return 0";

        DefaultRedisScript<Long> redisScript =
                new DefaultRedisScript<>(script, Long.class);

        try {
            String serializedCode =
                    objectMapper.writeValueAsString(submittedCode);

            Long result = (Long) redisTemplate.execute(
                    redisScript,
                    Collections.unmodifiableList(
                            java.util.Arrays.asList(codeKey, attemptsKey)),
                    serializedCode,
                    String.valueOf(maxAttempts),
                    String.valueOf(attemptsTtlSeconds));

            return result == null ? 0 : result;
        } catch (Exception e) {
            log.error("Atomic verification operation failed", e);
            throw new IllegalStateException(
                    "Could not verify the code", e);
        }
    }
}
