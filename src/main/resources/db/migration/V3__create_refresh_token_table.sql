CREATE TABLE refresh_token (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            token VARCHAR(255) NOT NULL UNIQUE,
            expiry_date TIMESTAMP NOT NULL,
            user_id BIGINT NOT NULL UNIQUE,
            -- I used the on delete cascade because it's pretty logic , if we have deleted the user automatically the token will deleted
            CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);