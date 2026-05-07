package org.manga_backend.notifms.domain.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class SubscriptionCacheService {
    private final StringRedisTemplate redisTemplate;

    public SubscriptionCacheService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String getMangaKey(Long mangaId) {
        return "manga:" + mangaId + ":subscribers";
    }

    public void addSubscriber(Long mangaId, Long userId) {
        redisTemplate.opsForSet().add(getMangaKey(mangaId), userId.toString());
    }

    public void removeSubscriber(Long mangaId, Long userId) {
        redisTemplate.opsForSet().remove(getMangaKey(mangaId), userId.toString());
    }

    public Set<String> getSubscribers(Long mangaId) {
        return redisTemplate.opsForSet().members(getMangaKey(mangaId));
    }
}
