-- 法律文书生成系统数据库表结构

-- 文书模板表
CREATE TABLE IF NOT EXISTS `document_templates` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '模板 ID',
    `name` VARCHAR(100) NOT NULL COMMENT '模板名称',
    `category` VARCHAR(50) NOT NULL COMMENT '分类：civil_litigation/contract/marriage/labor/other',
    `description` TEXT COMMENT '模板描述',
    `file_path` VARCHAR(500) NOT NULL COMMENT '模板文件路径',
    `variables` JSON COMMENT '模板变量列表',
    `download_count` INT DEFAULT 0 COMMENT '下载次数',
    `is_public` TINYINT DEFAULT 1 COMMENT '是否公开：1-是，0-否',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
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
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_template_id` (`template_id`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文书生成历史表';

-- 初始化模板分类数据（示例）
INSERT INTO `document_templates` (`name`, `category`, `description`, `file_path`, `variables`, `is_public`) VALUES
('民事起诉状', 'civil_litigation', '适用于民事案件起诉', '/templates/civil/complaint.docx', 
'[{"name":"plaintiff_name","label":"原告姓名","type":"text","required":true,"placeholder":"请输入原告姓名"},
  {"name":"plaintiff_phone","label":"原告电话","type":"text","required":true,"placeholder":"请输入原告电话"},
  {"name":"defendant_name","label":"被告姓名","type":"text","required":true,"placeholder":"请输入被告姓名"},
  {"name":"claim","label":"诉讼请求","type":"textarea","required":true,"placeholder":"请输入诉讼请求"},
  {"name":"facts","label":"事实与理由","type":"textarea","required":true,"placeholder":"请输入事实与理由"},
  {"name":"court_name","label":"受理法院","type":"text","required":true,"placeholder":"请输入法院名称"},
  {"name":"sign_date","label":"签署日期","type":"date","required":true,"placeholder":"请选择日期"}]', 1),
  
('民事答辩状', 'civil_litigation', '适用于民事案件答辩', '/templates/civil/defense.docx', 
'[{"name":"defendant_name","label":"被告姓名","type":"text","required":true},
  {"name":"plaintiff_name","label":"原告姓名","type":"text","required":true},
  {"name":"defense_opinion","label":"答辩意见","type":"textarea","required":true},
  {"name":"sign_date","label":"签署日期","type":"date","required":true}]', 1),
  
('借款合同', 'contract', '适用于个人/企业借款', '/templates/contract/loan.docx', 
'[{"name":"lender_name","label":"出借人姓名","type":"text","required":true},
  {"name":"borrower_name","label":"借款人姓名","type":"text","required":true},
  {"name":"loan_amount","label":"借款金额","type":"number","required":true},
  {"name":"loan_date","label":"借款日期","type":"date","required":true},
  {"name":"repay_date","label":"还款日期","type":"date","required":true},
  {"name":"interest_rate","label":"利率","type":"text","required":false}]', 1),
  
('房屋租赁合同', 'contract', '适用于房屋租赁', '/templates/contract/lease.docx', 
'[{"name":"landlord_name","label":"出租人姓名","type":"text","required":true},
  {"name":"tenant_name","label":"承租人姓名","type":"text","required":true},
  {"name":"house_address","label":"房屋地址","type":"text","required":true},
  {"name":"rent_amount","label":"租金金额","type":"number","required":true},
  {"name":"lease_term","label":"租赁期限","type":"text","required":true},
  {"name":"sign_date","label":"签署日期","type":"date","required":true}]', 1),
  
('律师函', 'other', '律师正式函告', '/templates/other/lawyer_letter.docx', 
'[{"name":"client_name","label":"委托人姓名","type":"text","required":true},
  {"name":"recipient_name","label":"收件人姓名","type":"text","required":true},
  {"name":"letter_content","label":"函告内容","type":"textarea","required":true},
  {"name":"lawyer_name","label":"律师姓名","type":"text","required":true},
  {"name":"law_firm","label":"律师事务所","type":"text","required":true},
  {"name":"sign_date","label":"签署日期","type":"date","required":true}]', 1),
  
('离婚协议书', 'marriage', '协议离婚使用', '/templates/marriage/divorce_agreement.docx', 
'[{"name":"husband_name","label":"男方姓名","type":"text","required":true},
  {"name":"wife_name","label":"女方姓名","type":"text","required":true},
  {"name":"divorce_reason","label":"离婚原因","type":"textarea","required":true},
  {"name":"property_division","label":"财产分割","type":"textarea","required":true},
  {"name":"child_custody","label":"子女抚养","type":"textarea","required":false},
  {"name":"sign_date","label":"签署日期","type":"date","required":true}]', 1);
