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
    `avatar_url` VARCHAR(500) COMMENT '头像 URL',
    `wechat_openid` VARCHAR(64) UNIQUE COMMENT '微信 OpenID',
    `wechat_unionid` VARCHAR(64) UNIQUE COMMENT '微信 UnionID',
    `role` ENUM('admin', 'lawyer', 'assistant', 'guest') DEFAULT 'lawyer' COMMENT '角色',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_phone` (`phone`),
    INDEX `idx_email` (`email`),
    INDEX `idx_wechat_openid` (`wechat_openid`),
    INDEX `idx_wechat_unionid` (`wechat_unionid`)
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
    `doc_type` VARCHAR(50) NOT NULL COMMENT '文书类型: contract/agreement/letter/other',
    `content` LONGTEXT COMMENT '文书内容',
    `status` VARCHAR(20) DEFAULT 'draft' COMMENT '状态: draft/review/approved/archived',
    `word_count` INT DEFAULT 0 COMMENT '字数统计',
    `version` INT DEFAULT 1 COMMENT '版本号',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_case_id` (`case_id`),
    INDEX `idx_doc_type` (`doc_type`),
    INDEX `idx_status` (`status`),
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
    `tags` VARCHAR(1000) COMMENT '标签(逗号分隔)',
    `status` VARCHAR(20) DEFAULT 'new' COMMENT '状态: new/following/closed',
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

-- ============================================
-- 测试数据
-- ============================================

-- 插入测试用户 (密码: 123456, BCrypt加密)
INSERT INTO `user` (`id`, `phone`, `email`, `password_hash`, `nickname`, `role`, `status`) VALUES
('550e8400-e29b-41d4-a716-446655440001', '13800138000', 'lawyer@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4L0p5Vv9M8J8W0zZ8Z8Z8Z8Z8Z8', '张律师', 'lawyer', 1),
('550e8400-e29b-41d4-a716-446655440002', '13800138001', 'user@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4L0p5Vv9M8J8W0zZ8Z8Z8Z8Z8Z8', '李用户', 'user', 1);

-- 插入测试案源
INSERT INTO `lead` (`id`, `user_id`, `title`, `description`, `source`, `tags`, `status`) VALUES
('660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', '婚姻家庭纠纷咨询', '客户咨询离婚财产分割问题，金额约500万', '客户推荐', '婚姻家庭,财产分割', 'following'),
('660e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', '合同违约案件', '某公司违约拖欠货款约200万元', '线上推广', '合同纠纷,民事', 'new'),
('660e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', '劳动仲裁案件', '员工要求支付拖欠工资及经济补偿金', '同行介绍', '劳动纠纷,仲裁', 'closed');

-- 插入测试文书
INSERT INTO `document` (`id`, `user_id`, `title`, `doc_type`, `content`, `status`, `word_count`) VALUES
('770e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', '房屋租赁合同', 'contract', '甲方（出租人）：XXX\n乙方（承租人）：XXX\n...', 'approved', 2500),
('770e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', '离婚协议书', 'agreement', '协议人（男方）：XXX\n协议人（女方）：XXX\n...', 'draft', 1800),
('770e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', '律师函', 'letter', '致：XXX公司\n...', 'review', 800);

-- 文书模板表
CREATE TABLE IF NOT EXISTS `document_templates` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '模板 ID',
    `name` VARCHAR(100) NOT NULL COMMENT '模板名称',
    `category` VARCHAR(50) NOT NULL COMMENT '分类：civil/contract/marriage/other',
    `description` TEXT COMMENT '模板描述',
    `file_path` VARCHAR(500) NOT NULL COMMENT '模板文件路径',
    `variables` JSON COMMENT '模板变量列表',
    `download_count` INT DEFAULT 0 COMMENT '下载次数',
    `is_public` TINYINT DEFAULT 1 COMMENT '是否公开：1-是，0-否',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_category` (`category`),
    INDEX `idx_is_public` (`is_public`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文书模板表';

-- 文书生成历史表
CREATE TABLE IF NOT EXISTS `document_histories` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '历史记录 ID',
    `user_id` BIGINT NOT NULL COMMENT '用户 ID',
    `template_id` BIGINT NOT NULL COMMENT '模板 ID',
    `file_name` VARCHAR(200) NOT NULL COMMENT '生成的文件名',
    `file_path` VARCHAR(500) NOT NULL COMMENT '生成的文件路径',
    `template_data` JSON COMMENT '填写的模板数据',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_template_id` (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文书生成历史表';

-- 初始化文书模板数据
INSERT INTO `document_templates` (`name`, `category`, `description`, `file_path`, `variables`, `is_public`) VALUES
('民事起诉状', 'civil', '适用于民事案件起诉', '/templates/civil/complaint.docx', 
'[{"name":"plaintiff_name","label":"原告姓名","type":"text","required":true,"placeholder":"请输入原告姓名"},
  {"name":"plaintiff_gender","label":"原告性别","type":"text","required":true,"placeholder":"男/女"},
  {"name":"plaintiff_ethnicity","label":"原告民族","type":"text","required":false,"placeholder":"请输入民族"},
  {"name":"plaintiff_birthdate","label":"原告出生日期","type":"text","required":false,"placeholder":"如：1990年1月1日"},
  {"name":"plaintiff_address","label":"原告住址","type":"text","required":true,"placeholder":"请输入住址"},
  {"name":"plaintiff_phone","label":"原告电话","type":"text","required":true,"placeholder":"请输入联系电话"},
  {"name":"defendant_name","label":"被告姓名","type":"text","required":true,"placeholder":"请输入被告姓名"},
  {"name":"defendant_gender","label":"被告性别","type":"text","required":true,"placeholder":"男/女"},
  {"name":"defendant_address","label":"被告住址","type":"text","required":true,"placeholder":"请输入住址"},
  {"name":"claim","label":"诉讼请求","type":"textarea","required":true,"placeholder":"请输入诉讼请求"},
  {"name":"facts","label":"事实与理由","type":"textarea","required":true,"placeholder":"请输入事实与理由"},
  {"name":"court_name","label":"受理法院","type":"text","required":true,"placeholder":"如：北京市朝阳区人民法院"},
  {"name":"sign_date","label":"签署日期","type":"text","required":true,"placeholder":"如：2024年6月7日"}]', 1),

('民事答辩状', 'civil', '适用于民事案件答辩', '/templates/civil/defense.docx', 
'[{"name":"defendant_name","label":"被告姓名","type":"text","required":true,"placeholder":"请输入被告姓名"},
  {"name":"plaintiff_name","label":"原告姓名","type":"text","required":true,"placeholder":"请输入原告姓名"},
  {"name":"defense_opinion","label":"答辩意见","type":"textarea","required":true,"placeholder":"请输入答辩意见"},
  {"name":"sign_date","label":"签署日期","type":"text","required":true,"placeholder":"如：2024年6月7日"}]', 1),

('借款合同', 'contract', '适用于个人/企业借款', '/templates/contract/loan.docx', 
'[{"name":"lender_name","label":"出借人","type":"text","required":true,"placeholder":"请输入出借人姓名"},
  {"name":"borrower_name","label":"借款人","type":"text","required":true,"placeholder":"请输入借款人姓名"},
  {"name":"loan_amount","label":"借款金额","type":"text","required":true,"placeholder":"请输入金额"},
  {"name":"loan_date","label":"借款日期","type":"text","required":true,"placeholder":"如：2024年1月1日"},
  {"name":"repay_date","label":"还款日期","type":"text","required":true,"placeholder":"如：2024年12月31日"},
  {"name":"interest_rate","label":"利率","type":"text","required":false,"placeholder":"如：年利率 4.35%"},
  {"name":"sign_date","label":"签署日期","type":"text","required":true,"placeholder":"如：2024年6月7日"}]', 1),

('房屋租赁合同', 'contract', '适用于房屋租赁', '/templates/contract/lease.docx', 
'[{"name":"landlord_name","label":"出租人","type":"text","required":true,"placeholder":"请输入出租人姓名"},
  {"name":"tenant_name","label":"承租人","type":"text","required":true,"placeholder":"请输入承租人姓名"},
  {"name":"house_address","label":"房屋地址","type":"text","required":true,"placeholder":"请输入房屋地址"},
  {"name":"rent_amount","label":"月租金","type":"text","required":true,"placeholder":"请输入租金金额"},
  {"name":"lease_term","label":"租赁期限","type":"text","required":true,"placeholder":"如：1年"},
  {"name":"sign_date","label":"签署日期","type":"text","required":true,"placeholder":"如：2024年6月7日"}]', 1),

('律师函', 'other', '律师正式函告', '/templates/other/lawyer_letter.docx', 
'[{"name":"client_name","label":"委托人","type":"text","required":true,"placeholder":"请输入委托人姓名"},
  {"name":"recipient_name","label":"收件人","type":"text","required":true,"placeholder":"请输入收件人姓名"},
  {"name":"letter_content","label":"函告内容","type":"textarea","required":true,"placeholder":"请输入函告内容"},
  {"name":"lawyer_name","label":"律师姓名","type":"text","required":true,"placeholder":"请输入律师姓名"},
  {"name":"law_firm","label":"律师事务所","type":"text","required":true,"placeholder":"请输入律所名称"},
  {"name":"sign_date","label":"签署日期","type":"text","required":true,"placeholder":"如：2024年6月7日"}]', 1),

('离婚协议书', 'marriage', '协议离婚使用', '/templates/marriage/divorce_agreement.docx', 
'[{"name":"husband_name","label":"男方姓名","type":"text","required":true,"placeholder":"请输入男方姓名"},
  {"name":"wife_name","label":"女方姓名","type":"text","required":true,"placeholder":"请输入女方姓名"},
  {"name":"divorce_reason","label":"离婚原因","type":"textarea","required":true,"placeholder":"请输入离婚原因"},
  {"name":"property_division","label":"财产分割","type":"textarea","required":true,"placeholder":"请输入财产分割方案"},
  {"name":"child_custody","label":"子女抚养","type":"textarea","required":false,"placeholder":"请输入子女抚养安排"},
  {"name":"sign_date","label":"签署日期","type":"text","required":true,"placeholder":"如：2024年6月7日"}]', 1);
