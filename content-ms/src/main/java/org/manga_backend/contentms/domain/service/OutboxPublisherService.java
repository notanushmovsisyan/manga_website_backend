package org.manga_backend.contentms.domain.service;

import org.manga_backend.contentms.persistence.entity.OutboxEntity;
import org.manga_backend.contentms.persistence.repository.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OutboxPublisherService {

    private static final String TOPIC = "chapter-published";

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisherService.class);

    public OutboxPublisherService(
            OutboxRepository outboxRepository,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishEvents() {

        List<OutboxEntity> events = outboxRepository.findTop100ByPublishedFalseOrderByCreatedAtAsc();

        for (OutboxEntity event : events) {
            try {
                publishSingleEvent(event);
            } catch (Exception e) {
                log.error("Failed to publish event id={} error={}", event.getId(), e.getMessage());
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishSingleEvent(OutboxEntity event) throws Exception {

        kafkaTemplate.send(
                TOPIC,
                event.getAggregateId().toString(),
                event.getPayload()
        ).get();

        event.setPublished(true);

        outboxRepository.save(event);
    }
}