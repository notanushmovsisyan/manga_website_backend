package org.manga_backend.trackingms.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.manga_backend.trackingms.api.dto.BookmarkAddedEvent;
import org.manga_backend.trackingms.api.dto.BookmarkRemovedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Service
public class BookmarkEventProducer {

    // 1. Changed Object to String
    private final KafkaTemplate<String, String> kafkaTemplate;

    // 2. Inject ObjectMapper
    private final ObjectMapper objectMapper;

    private static final String TOPIC = "bookmark-updates";

    private static final Logger log = LoggerFactory.getLogger(BookmarkEventProducer.class);

    // Update constructor
    public BookmarkEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendBookmarkAdded(Long userId, Long mangaId, Instant timestamp) {
        try {
            var event = new BookmarkAddedEvent(userId, mangaId, timestamp);

            // 3. Manually convert the object to a JSON string
            String payload = objectMapper.writeValueAsString(event);

            // 4. Create the ProducerRecord with String, String
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, mangaId.toString(), payload);

            // Attach the custom header
            record.headers().add("eventType", "BookmarkAdded".getBytes(StandardCharsets.UTF_8));

            // Send the record
            kafkaTemplate.send(record)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish event for mangaId={}", mangaId, ex);
                        } else {
                            log.info("Published bookmark event for mangaId={}", mangaId);
                        }
                    });

        } catch (Exception e) {
            // writeValueAsString throws an exception if it fails to serialize, so we catch it
            log.error("Failed to publish bookmark-added event. userId={}, mangaId={}", userId, mangaId, e);
        }
    }

    public void sendBookmarkRemoved(Long userId, Long mangaId, Instant timestamp) {
        try {
            var event = new BookmarkRemovedEvent(userId, mangaId, timestamp);

            // Convert to JSON String
            String payload = objectMapper.writeValueAsString(event);

            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, mangaId.toString(), payload);
            record.headers().add("eventType", "BookmarkRemoved".getBytes(StandardCharsets.UTF_8));

            kafkaTemplate.send(record)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish event for mangaId={}", mangaId, ex);
                        } else {
                            log.info("Published bookmark event for mangaId={}", mangaId);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}