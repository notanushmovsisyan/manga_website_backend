package org.manga_backend.contentms.api.dto;

import java.time.Instant;
import java.util.UUID;

public record OutboxPayload(
        UUID eventId,
        String eventType,
        String aggregateType,
        Long aggregateId,
        Instant occurredAt,
        Object payload
) {}