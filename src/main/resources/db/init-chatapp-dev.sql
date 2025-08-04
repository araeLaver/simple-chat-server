-- chatapp_dev 스키마 초기화 스크립트
SET search_path TO chatapp_dev;

-- 기존 테이블 삭제 (순서 중요 - FK 관계 고려)
DROP TABLE IF EXISTS user_sessions CASCADE;
DROP TABLE IF EXISTS messages CASCADE;
DROP TABLE IF EXISTS chat_rooms CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- 함수 삭제
DROP FUNCTION IF EXISTS update_updated_at_column() CASCADE;

-- Users Table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    last_login TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT true,
    profile_image VARCHAR(500),
    status_message VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Chat Rooms Table
CREATE TABLE chat_rooms (
    room_id VARCHAR(50) PRIMARY KEY,
    room_name VARCHAR(100) NOT NULL,
    room_type VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    encryption_key VARCHAR(255),
    description VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT true,
    max_users INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_room_type CHECK (room_type IN ('NORMAL', 'SECRET', 'VOLATILE'))
);

-- Messages Table
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    sender VARCHAR(50) NOT NULL,
    content TEXT,
    room_id VARCHAR(50) NOT NULL,
    message_type VARCHAR(20) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    security_type VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    expires_at TIMESTAMP,
    encryption_key VARCHAR(255),
    is_encrypted BOOLEAN NOT NULL DEFAULT false,
    file_url VARCHAR(500),
    file_name VARCHAR(255),
    file_size BIGINT,
    edited_at TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT chk_security_type CHECK (security_type IN ('NORMAL', 'SECRET', 'VOLATILE')),
    CONSTRAINT chk_message_type CHECK (message_type IN ('message', 'system', 'file', 'volatile')),
    CONSTRAINT fk_messages_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_messages_room_id FOREIGN KEY (room_id) REFERENCES chat_rooms(room_id) ON DELETE CASCADE
);

-- User Sessions Table
CREATE TABLE user_sessions (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL,
    room_id VARCHAR(50),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    is_online BOOLEAN NOT NULL DEFAULT true,
    last_activity TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    connected_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    disconnected_at TIMESTAMP,
    
    CONSTRAINT fk_sessions_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_sessions_room_id FOREIGN KEY (room_id) REFERENCES chat_rooms(room_id) ON DELETE SET NULL
);

-- Indexes for Performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_is_active ON users(is_active);
CREATE INDEX idx_users_created_at ON users(created_at);

CREATE INDEX idx_chat_rooms_room_type ON chat_rooms(room_type);
CREATE INDEX idx_chat_rooms_is_active ON chat_rooms(is_active);

CREATE INDEX idx_messages_room_id ON messages(room_id);
CREATE INDEX idx_messages_sender ON messages(sender);
CREATE INDEX idx_messages_timestamp ON messages(timestamp);
CREATE INDEX idx_messages_user_id ON messages(user_id);
CREATE INDEX idx_messages_security_type ON messages(security_type);
CREATE INDEX idx_messages_expires_at ON messages(expires_at);
CREATE INDEX idx_messages_is_deleted ON messages(is_deleted);

CREATE INDEX idx_sessions_session_id ON user_sessions(session_id);
CREATE INDEX idx_sessions_user_id ON user_sessions(user_id);
CREATE INDEX idx_sessions_room_id ON user_sessions(room_id);
CREATE INDEX idx_sessions_is_online ON user_sessions(is_online);
CREATE INDEX idx_sessions_last_activity ON user_sessions(last_activity);

-- Trigger Function for updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers
CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_chat_rooms_updated_at 
    BEFORE UPDATE ON chat_rooms 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Initial Data - Default Chat Rooms
INSERT INTO chat_rooms (room_id, room_name, room_type, description, max_users) VALUES
('general', '일반 채팅방', 'NORMAL', '모든 사용자가 참여할 수 있는 일반 채팅방입니다.', NULL),
('tech', '개발 이야기', 'NORMAL', '개발과 기술에 관한 이야기를 나누는 채팅방입니다.', NULL),
('casual', '자유 토론', 'NORMAL', '자유롭게 대화를 나누는 채팅방입니다.', NULL),
('secret', '🔐 비밀 대화', 'SECRET', '암호화된 메시지로 안전한 대화를 나누는 채팅방입니다.', 10),
('volatile', '💥 휘발성 채팅', 'VOLATILE', '메시지가 자동으로 삭제되는 휘발성 채팅방입니다.', 20);

-- Update secret room with encryption key
UPDATE chat_rooms SET encryption_key = 'secret123' WHERE room_id = 'secret';

-- Verify tables created
SELECT 'Tables created successfully in chatapp_dev schema' as status;
SELECT table_name FROM information_schema.tables 
WHERE table_schema = 'chatapp_dev' 
ORDER BY table_name;