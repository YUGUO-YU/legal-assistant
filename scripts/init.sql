-- 法律助手数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS legal_assistant DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE legal_assistant;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` CHAR(36) PRIMARY KEY,
    `phone` VARCHAR(20) UNIQUE COMMENT '手机号',
    `email` VARCHAR(255) UNIQUE COMMENT '邮箱',
    `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希',
    `nickname` VARCHAR(100) COMMENT '昵称',
    `avatar_url` VARCHAR(500) COMMENT '头像URL',
    `role` ENUM('admin', 'lawyer', 'assistant', 'guest') DEFAULT 'lawyer' COMMENT '角色',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_phone` (`phone`),
    INDEX `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 团队表
CREATE TABLE IF NOT EXISTS `team` (
    `id` CHAR(36) PRIMARY KEY,
    `name` VARCHAR(200) NOT NULL COMMENT '团队名称',
    `owner_id` CHAR(36) NOT NULL COMMENT '所有者ID',
    `invite_code` VARCHAR(20) UNIQUE COMMENT '邀请码',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_owner_id` (`owner_id`),
    FOREIGN KEY (`owner_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队表';

-- 团队成员表
CREATE TABLE IF NOT EXISTS `team_member` (
    `id` CHAR(36) PRIMARY KEY,
    `team_id` CHAR(36) NOT NULL COMMENT '团队ID',
    `user_id` CHAR(36) NOT NULL COMMENT '用户ID',
    `role` ENUM('owner', 'admin', 'member') DEFAULT 'member' COMMENT '团队内角色',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    `joined_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    INDEX `idx_team_id` (`team_id`),
    INDEX `idx_user_id` (`user_id`),
    FOREIGN KEY (`team_id`) REFERENCES `team`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='团队成员表';

-- 文书表
CREATE TABLE IF NOT EXISTS `document` (
    `id` CHAR(36) PRIMARY KEY,
    `user_id` CHAR(36) NOT NULL COMMENT '用户ID',
    `case_id` CHAR(36) COMMENT '关联案件ID',
    `title` VARCHAR(500) NOT NULL COMMENT '文书标题',
    `doc_type` ENUM('contract', 'litigation', 'non_litigation', 'other') NOT NULL COMMENT '文书类型',
    `content` LONGTEXT COMMENT '文书内容',
    `status` ENUM('draft', 'final', 'archived') DEFAULT 'draft' COMMENT '状态',
    `version` INT DEFAULT 1 COMMENT '版本号',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_case_id` (`case_id`),
    INDEX `idx_doc_type` (`doc_type`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文书表';

-- 文书版本表
CREATE TABLE IF NOT EXISTS `document_version` (
    `id` CHAR(36) PRIMARY KEY,
    `document_id` CHAR(36) NOT NULL COMMENT '文书ID',
    `content` LONGTEXT NOT NULL COMMENT '版本内容',
    `version` INT NOT NULL COMMENT '版本号',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_document_id` (`document_id`),
    FOREIGN KEY (`document_id`) REFERENCES `document`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文书版本表';

-- 案例收藏表
CREATE TABLE IF NOT EXISTS `case_bookmark` (
    `id` CHAR(36) PRIMARY KEY,
    `user_id` CHAR(36) NOT NULL COMMENT '用户ID',
    `external_id` VARCHAR(100) NOT NULL COMMENT '外部案例ID',
    `source` VARCHAR(50) NOT NULL COMMENT '数据来源',
    `title` VARCHAR(500) COMMENT '案例标题',
    `note` TEXT COMMENT '备注',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    UNIQUE KEY `uk_user_external` (`user_id`, `external_id`, `source`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='案例收藏表';

-- 案源线索表
CREATE TABLE IF NOT EXISTS `lead` (
    `id` CHAR(36) PRIMARY KEY,
    `user_id` CHAR(36) NOT NULL COMMENT '用户ID',
    `title` VARCHAR(500) NOT NULL COMMENT '案源标题',
    `description` TEXT COMMENT '描述',
    `source` VARCHAR(100) COMMENT '来源',
    `tags` JSON COMMENT '标签',
    `status` ENUM('new', 'contacted', 'following', 'converted', 'discarded') DEFAULT 'new' COMMENT '状态',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_status` (`status`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='案源线索表';

-- 短信验证码表
CREATE TABLE IF NOT EXISTS `sms_code` (
    `id` CHAR(36) PRIMARY KEY,
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `code` VARCHAR(10) NOT NULL COMMENT '验证码',
    `type` VARCHAR(20) DEFAULT 'login' COMMENT '验证码类型',
    `expires_at` TIMESTAMP NOT NULL COMMENT '过期时间',
    `used` TINYINT DEFAULT 0 COMMENT '是否已使用',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_phone` (`phone`),
    INDEX `idx_expires_at` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信验证码表';
