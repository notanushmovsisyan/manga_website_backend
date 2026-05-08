CREATE TABLE refresh_tokens (
                                id BIGSERIAL PRIMARY KEY,

                                user_id BIGINT NOT NULL,

                                token TEXT NOT NULL UNIQUE,

                                expires_at TIMESTAMP NOT NULL,

                                is_revoked BOOLEAN NOT NULL DEFAULT FALSE,

                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT fk_refresh_tokens_user
                                    FOREIGN KEY (user_id)
                                        REFERENCES users (id)
                                        ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);