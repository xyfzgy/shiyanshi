CREATE DATABASE shiyanshi;
-- 1. 菜谱源数据（RAG 的源，启动时 embedding 到 SimpleVectorStore）
CREATE TABLE recipe (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        NAME VARCHAR(200) NOT NULL COMMENT '菜名',
                        cuisine VARCHAR(50) COMMENT '菜系',
                        difficulty VARCHAR(20) COMMENT '难度',
                        prep_time INT COMMENT '准备时间(分钟)',
                        cook_time INT COMMENT '烹饪时间(分钟)',
                        servings INT COMMENT '默认几人份',
                        ingredients JSON COMMENT '材料列表',
                        steps JSON COMMENT '步骤',
                        tips TEXT COMMENT '小贴士',
                        taste_tags JSON COMMENT '口味标签',
                        source VARCHAR(100) COMMENT '数据来源',
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

-- 2. 对话记录
CREATE TABLE conversation (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              user_id BIGINT COMMENT '用户ID',
                              slots JSON COMMENT '槽位状态',
                              STATUS VARCHAR(20) DEFAULT 'COLLECTING' COMMENT 'COLLECTING/READY/DONE',
                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                              updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

-- 3. 消息记录
CREATE TABLE message (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         conversation_id BIGINT NOT NULL,
                         role VARCHAR(20) NOT NULL COMMENT 'user/assistant/system/tool',
                         content TEXT NOT NULL,
                         created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                         INDEX idx_conversation (conversation_id, created_at)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

-- 4. 感受记录
CREATE TABLE feel_record (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             conversation_id BIGINT,
                             user_id BIGINT,
                             dish_name VARCHAR(200) COMMENT '菜名',
                             rating INT COMMENT '1-10',
                             positive TEXT COMMENT '优点',
                             improvement TEXT COMMENT '改进点',
                             emotion_tags JSON COMMENT '情感标签',
                             want_again TINYINT(1) COMMENT '是否想再做',
                             created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

-- 5. 用户画像
CREATE TABLE user_profile (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              user_id BIGINT UNIQUE,
                              taste_preference JSON COMMENT '口味偏好',
                              taboo JSON COMMENT '忌口',
                              diet_goal VARCHAR(50) COMMENT '减脂/增肌/养生',
                              created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                              updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;