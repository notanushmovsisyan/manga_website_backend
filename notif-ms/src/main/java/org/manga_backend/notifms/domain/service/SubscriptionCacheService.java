package org.manga_backend.notifms.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
public class SubscriptionCacheService {
    private final StringRedisTemplate redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(SubscriptionCacheService.class);

    public SubscriptionCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String getMangaKey(Long mangaId) {
        return "manga:" + mangaId + ":subscribers";
    }

    public void addSubscriber(Long mangaId, Long userId) {
        redisTemplate.opsForSet().add(getMangaKey(mangaId), userId.toString());
        log.debug("Subscriber added to Redis mangaId={} userId={}", mangaId, userId);
    }

    public void removeSubscriber(Long mangaId, Long userId) {
        redisTemplate.opsForSet().remove(getMangaKey(mangaId), userId.toString());
        log.debug("Subscriber removed from Redis mangaId={} userId={}", mangaId, userId);
    }

    public Set<String> getSubscribers(Long mangaId) {
        Set<String> result = redisTemplate.opsForSet().members(getMangaKey(mangaId));
        return result != null ? result : Collections.emptySet();
    }
}
