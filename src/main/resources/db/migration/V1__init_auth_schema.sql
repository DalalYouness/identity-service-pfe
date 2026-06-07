CREATE TABLE roles (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        role_name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE users (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        email VARCHAR(180) NOT NULL UNIQUE,
        password VARCHAR(255) NULL,
        username VARCHAR(100),
        auth_provider VARCHAR(50) DEFAULT 'LOCAL',
        account_status VARCHAR(20) DEFAULT 'ACTIVE',
        created_at DATETIME,
        updated_at DATETIME
) ENGINE=Inn
CREATE TABLE profils (
        id BIGINT PRIMARY KEY, -- هو بـيـدو PK و FK
        type_profil VARCHAR(30) NOT NULL, -- Discriminator: ADMIN, CLIENT, PRESTATAIRE
        firstname VARCHAR(100),
        lastname VARCHAR(100),
        phone_number VARCHAR(20),
        birthdate DATE,
        gender VARCHAR(10),
        photo_url VARCHAR(255),
        address VARCHAR(255),
        city VARCHAR(100),
        country VARCHAR(100),
        bio TEXT,
        is_available BOOLEAN DEFAULT TRUE,
        intervention_area VARCHAR(255), -- خاص فقط بالـ PRESTATAIRE
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        CONSTRAINT fk_profil_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;


CREATE TABLE user_roles (
        user_id BIGINT NOT NULL,
        role_id BIGINT NOT NULL,
        PRIMARY KEY (user_id, role_id),
        CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB;


INSERT INTO roles (role_name) VALUES ('ROLE_CLIENT');
INSERT INTO roles (role_name) VALUES ('ROLE_PRESTATAIRE');
INSERT INTO roles (role_name) VALUES ('ROLE_ADMIN');