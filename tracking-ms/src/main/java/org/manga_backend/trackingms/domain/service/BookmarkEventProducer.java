package org.manga_backend.trackingms.domain.service;

import org.manga_backend.trackingms.api.dto.BookmarkAddedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BookmarkEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "bookmark-updates";

    public BookmarkEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBookmarkAdded(Long userId, Long mangaId, Instant occurredAt) {
        var event = new BookmarkAddedEvent(userId, mangaId, occurredAt);
        kafkaTemplate.send(TOPIC, userId.toString(), event);
    }
}
