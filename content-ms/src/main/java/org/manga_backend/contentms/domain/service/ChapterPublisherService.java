package org.manga_backend.contentms.domain.service;

import org.manga_backend.contentms.api.dto.ChapterPublishedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Service
public class ChapterPublisherService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "manga-updates";

    public ChapterPublisherService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendChapterPublished(Long mangaId, Long chapterId, Double chapterNumber, Instant occurredAt) {
        var event = new ChapterPublishedEvent(mangaId, chapterId, chapterNumber, occurredAt);
        kafkaTemplate.send(TOPIC, mangaId.toString(), event);
    }
}
