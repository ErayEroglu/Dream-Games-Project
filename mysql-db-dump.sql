CREATE TABLE IF NOT EXISTS users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       level INT NOT NULL DEFAULT 1,
                       coins INT NOT NULL DEFAULT 2000,
                       ab_test_group ENUM('GroupA', 'GroupB') NOT NULL
);

CREATE TABLE IF NOT EXISTS partnerships (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             sender_id BIGINT NOT NULL,
                             receiver_id BIGINT NOT NULL,
                             status VARCHAR(20) NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (sender_id) REFERENCES users(id),
                             FOREIGN KEY (receiver_id) REFERENCES users(id)
) engine = InnoDB;