CREATE TABLE IF NOT EXISTS comments (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        post_id BIGINT NOT NULL,
                                        user_id BIGINT NOT NULL,
                                        content VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    parent_id BIGINT DEFAULT NULL,
    FOREIGN KEY (post_id) REFERENCES user_posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE
    );

