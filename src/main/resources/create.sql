CREATE TABLE jwt_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    access_token VARCHAR(255),
    id_token VARCHAR(512),
    refresh_token VARCHAR(255),
    issued_at TIMESTAMP,
    expire_time BIGINT,
    role VARCHAR(255)
);


CREATE TABLE otp (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    otp_code VARCHAR(255),
    expiration_time TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE realm (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    root_url VARCHAR(255),
    name VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);


CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255),
    is_email_verified VARCHAR(255),
    password VARCHAR(255),
    salt VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    is_blocked INT
);

CREATE TABLE user_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255),
    phone_number VARCHAR(255),
    profile_pic_url VARCHAR(255),
    display_name VARCHAR(255),
    role VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    user_id BIGINT UNIQUE,
    FOREIGN KEY (user_id) REFERENCES user (id)
);

CREATE TABLE user_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    role_id BIGINT,
    FOREIGN KEY (role_id) REFERENCES role (id)
);

CREATE TABLE verify_token (
    email VARCHAR(255) PRIMARY KEY,
    verification_token VARCHAR(255),
    verification_otp VARCHAR(255),
    verification_type VARCHAR(255),
    created_at TIMESTAMP
);

CREATE TABLE mapping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    location VARCHAR(255),
    parent_mapping BIGINT,
    user_group_id BIGINT,
    realm BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (parent_mapping) REFERENCES mapping (id),
    FOREIGN KEY (user_group_id) REFERENCES user_group (id), -- Assuming there's a UserGroup table
    FOREIGN KEY (realm) REFERENCES realm (id) -- Assuming there's a Realm table
);

CREATE TABLE role_mapping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mapping BIGINT,
    role_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (mapping) REFERENCES mapping (id),
    FOREIGN KEY (role_id) REFERENCES role (id)
);

CREATE TABLE user_group_mapping (
    user_id BIGINT,
    user_group_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (user_group_id) REFERENCES user_group(id)
);
