CREATE TABLE outbox_events (
                               id BIGSERIAL PRIMARY KEY,

                               aggregate_type VARCHAR(255),

                               aggregate_id BIGINT,

                               event_type VARCHAR(255),

                               payload TEXT,

                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               published BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_outbox_published_created
    ON outbox_events(published, created_at);