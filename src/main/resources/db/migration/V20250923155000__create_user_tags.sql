CREATE TABLE IF NOT EXISTS user_tags (
                                         user_id BIGINT NOT NULL,
                                         tag_id BIGINT NOT NULL,
                                         PRIMARY KEY (user_id, tag_id),
                                         FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                         FOREIGN KEY (tag_id) REFERENCES tags(tag_id) ON DELETE CASCADE
);
