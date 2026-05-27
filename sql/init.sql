-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_crush DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE campus_crush;

-- 用户表
CREATE TABLE sys_user
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码',
    phone       VARCHAR(11) COMMENT '手机号',
    nickname    VARCHAR(50) COMMENT '昵称',
    avatar      VARCHAR(255) COMMENT '用户头像URL',
    gender      INT COMMENT '性别 0-未知 1-男 2-女',
    intro       VARCHAR(500) COMMENT '个人简介',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    email             VARCHAR(100) COMMENT 'QQ邮箱',
    login_fail_count  INT DEFAULT 0 COMMENT '登录失败次数',
    login_freeze_until DATETIME COMMENT '登录冻结截止时间',
    UNIQUE KEY uk_phone (phone),
    UNIQUE KEY uk_email (email)
) COMMENT ='用户表';

-- 已有数据库执行以下语句升级表结构
-- ALTER TABLE sys_user ADD COLUMN email VARCHAR(100) COMMENT 'QQ邮箱';
-- ALTER TABLE sys_user ADD COLUMN login_fail_count INT DEFAULT 0 COMMENT '登录失败次数';
-- ALTER TABLE sys_user ADD COLUMN login_freeze_until DATETIME COMMENT '登录冻结截止时间';
-- ALTER TABLE sys_user ADD UNIQUE KEY uk_email (email);
-- ALTER TABLE sys_user ADD UNIQUE KEY uk_phone (phone);

-- 恋爱纪念日
CREATE TABLE love_anniversary
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '纪念日ID',
    user_id        BIGINT                     NOT NULL COMMENT '用户ID',
    name           VARCHAR(100)               NOT NULL COMMENT '纪念日名称',
    type           VARCHAR(20) DEFAULT 'love' NOT NULL COMMENT '类型(love:恋爱纪念日,birthday:生日,festival:节日,other:其他)',
    date           DATE                       NOT NULL COMMENT '日期',
    remind_days    INT         DEFAULT 7 COMMENT '提前提醒天数',
    remind_enabled TINYINT(1)  DEFAULT 0 COMMENT '是否启用提醒(0:关闭,1:开启)',
    INDEX idx_user_id (user_id)
) COMMENT ='恋爱纪念日';

-- 邮箱设置表
CREATE TABLE email_settings
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    user_id      BIGINT NOT NULL COMMENT '用户ID',
    email        VARCHAR(255) COMMENT '邮箱地址',
    subscription VARCHAR(20) DEFAULT 'all' COMMENT '订阅类型(all/diary/anniversary/community)',
    enabled      TINYINT(1)  DEFAULT 0 COMMENT '是否启用(0:关闭,1:开启)',
    create_time  DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_id (user_id),
    INDEX idx_user_id (user_id)
) COMMENT ='邮箱设置表';

-- 恋爱相册
CREATE TABLE love_album
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '照片ID',
    user_id     BIGINT       NOT NULL COMMENT '用户ID',
    photo_name  VARCHAR(100) NOT NULL COMMENT '照片名称',
    photo_url   VARCHAR(500) NOT NULL COMMENT '照片路径',
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    INDEX idx_user_id (user_id)
) COMMENT ='恋爱相册';

-- 恋爱日记
CREATE TABLE love_diary
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日记ID',
    user_id     BIGINT       NOT NULL COMMENT '用户ID',
    title       VARCHAR(100) NOT NULL COMMENT '标题',
    content     TEXT COMMENT '内容',
    mood        VARCHAR(20) COMMENT '心情',
    image       VARCHAR(500) COMMENT '图片URL（可选）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id)
) COMMENT ='恋爱日记';

-- 已有数据库执行以下语句升级表结构（支持日记图片）
-- ALTER TABLE love_diary ADD COLUMN image VARCHAR(500) COMMENT '图片URL（可选）';

-- 插入默认管理员账号 (密码: admin, MD5加密后: 21232f297a57a5a743894a0e4a801fc3)
INSERT INTO sys_user (username, password, nickname, avatar)
VALUES ('admin', '21232f297a57a5a743894a0e4a801fc3', '管理员', NULL);

-- 社区功能模块

-- 帖子表
CREATE TABLE IF NOT EXISTS `t_post`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`       BIGINT       NOT NULL,
    `title`         VARCHAR(255) NOT NULL,
    `content`       TEXT         NOT NULL,
    `images`        JSON         NOT NULL COMMENT '图片URL数组',
    `categories`    JSON         NOT NULL COMMENT '分类数组',
    `location`      VARCHAR(255),
    `visibility`    INT          NOT NULL DEFAULT 1 COMMENT '可见性：1-公开，2-仅好友可见，3-私密',
    `like_count`    INT          NOT NULL DEFAULT 0,
    `collect_count` INT          NOT NULL DEFAULT 0,
    `browse_count`  INT          NOT NULL DEFAULT 0 COMMENT '浏览量',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 评论表
CREATE TABLE IF NOT EXISTS `t_comment`
(
    `id`          BIGINT   NOT NULL AUTO_INCREMENT,
    `post_id`     BIGINT   NOT NULL,
    `user_id`     BIGINT   NOT NULL,
    `parent_id`   BIGINT            DEFAULT NULL COMMENT '父评论ID，用于回复',
    `content`     TEXT     NOT NULL,
    `like_count`  INT      NOT NULL DEFAULT 0,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_post_id` (`post_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 点赞表
CREATE TABLE IF NOT EXISTS `t_like`
(
    `id`          BIGINT   NOT NULL AUTO_INCREMENT,
    `post_id`     BIGINT   NOT NULL COMMENT '帖子ID',
    `comment_id`  BIGINT   NULL     DEFAULT NULL COMMENT '评论ID（评论点赞时使用）',
    `user_id`     BIGINT   NOT NULL,
    `type`        INT      NOT NULL COMMENT '类型：1-帖子，2-评论',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_user_type` (`post_id`, `user_id`, `type`),
    UNIQUE KEY `uk_comment_user_type` (`comment_id`, `user_id`, `type`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_comment_id` (`comment_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 收藏表
CREATE TABLE IF NOT EXISTS `t_collect`
(
    `id`          BIGINT   NOT NULL AUTO_INCREMENT,
    `post_id`     BIGINT   NOT NULL,
    `user_id`     BIGINT   NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 关注表
CREATE TABLE IF NOT EXISTS `t_follow`
(
    `id`           BIGINT   NOT NULL AUTO_INCREMENT,
    `follower_id`  BIGINT   NOT NULL COMMENT '关注者ID',
    `following_id` BIGINT   NOT NULL COMMENT '被关注者ID',
    `create_time`  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_follower_following` (`follower_id`, `following_id`),
    KEY `idx_follower_id` (`follower_id`),
    KEY `idx_following_id` (`following_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 好友表
CREATE TABLE IF NOT EXISTS `t_friend`
(
    `id`              BIGINT   NOT NULL AUTO_INCREMENT,
    `user_id`         BIGINT   NOT NULL,
    `friend_id`       BIGINT   NOT NULL,
    `friend_nickname` VARCHAR(50) COMMENT '好友备注名称',
    `status`          INT      NOT NULL DEFAULT 1 COMMENT '状态：1-待确认，2-已好友',
    `create_time`     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_friend` (`user_id`, `friend_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_friend_id` (`friend_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- 聊天消息表
CREATE TABLE t_chat
(
    id              BIGINT AUTO_INCREMENT COMMENT '聊天消息ID' PRIMARY KEY,
    from_id         BIGINT                             NOT NULL COMMENT '发送者ID',
    to_id           BIGINT                             NOT NULL COMMENT '接收者ID',
    content         TEXT                               NOT NULL COMMENT '消息内容',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    is_read         TINYINT  DEFAULT 0 COMMENT '0未读，1已读',
    msg_type        TINYINT  DEFAULT 1 COMMENT '1文本，2图片，3表情',
    deleted_by_from TINYINT  DEFAULT 0 COMMENT '发送者是否删除：0-未删除，1-已删除',
    deleted_by_to   TINYINT  DEFAULT 0 COMMENT '接收者是否删除：0-未删除，1-已删除'
) COMMENT '聊天消息表' CHARSET = utf8mb4;

-- 核心联合索引（解决查询卡顿）
CREATE INDEX idx_chat_pair_time ON t_chat (from_id, to_id, create_time);

-- 关注通知表
CREATE TABLE IF NOT EXISTS `t_follow_notice`
(
    `id`                 BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`            BIGINT      NOT NULL COMMENT '接收通知的用户ID',
    `from_user_id`       BIGINT      NOT NULL COMMENT '发起操作的用户ID',
    `from_user_nickname` VARCHAR(50) NOT NULL COMMENT '发起操作的用户昵称',
    `from_user_avatar`   VARCHAR(255) COMMENT '发起操作的用户头像',
    `type`               INT         NOT NULL COMMENT '类型：1-关注，2-取消关注',
    `create_time`        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `is_read`            TINYINT     NOT NULL DEFAULT 0 COMMENT '0未读，1已读',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='关注通知表';

-- 帖子浏览记录表
CREATE TABLE IF NOT EXISTS `t_post_browse`
(
    `id`          BIGINT   NOT NULL AUTO_INCREMENT,
    `post_id`     BIGINT   NOT NULL COMMENT '被浏览的帖子ID',
    `user_id`     BIGINT   NOT NULL COMMENT '浏览者用户ID',
    `browse_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    PRIMARY KEY (`id`),
    KEY `idx_post_id` (`post_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_browse_time` (`browse_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='帖子浏览记录表';

-- 互动通知表（点赞、收藏）
CREATE TABLE IF NOT EXISTS `t_interaction_notice`
(
    `id`                 BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`            BIGINT      NOT NULL COMMENT '接收通知的用户ID（帖子作者）',
    `from_user_id`       BIGINT      NOT NULL COMMENT '发起操作的用户ID',
    `from_user_nickname` VARCHAR(50) NOT NULL COMMENT '发起操作的用户昵称',
    `from_user_avatar`   VARCHAR(255) COMMENT '发起操作的用户头像',
    `post_id`            BIGINT      NOT NULL COMMENT '被操作的帖子ID',
    `post_title`         VARCHAR(255) COMMENT '帖子标题',
    `type`               INT         NOT NULL COMMENT '类型：1-点赞，2-收藏，3-评论',
    `create_time`        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `is_read`            TINYINT     NOT NULL DEFAULT 0 COMMENT '0未读，1已读',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='互动通知表';

-- 投诉表
CREATE TABLE IF NOT EXISTS `t_complaint`
(
    `id`                BIGINT   NOT NULL AUTO_INCREMENT,
    `complaint_user_id` BIGINT   NOT NULL COMMENT '投诉用户ID',
    `target_user_id`    BIGINT   NOT NULL COMMENT '被投诉用户ID',
    `session_id`        VARCHAR(64) COMMENT '会话ID',
    `reason`            TEXT     NOT NULL COMMENT '投诉原因',
    `status`            TINYINT           DEFAULT 0 COMMENT '处理状态：0-待处理，1-已处理，2-已驳回',
    `create_time`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_complaint_user` (`complaint_user_id`),
    KEY `idx_target_user` (`target_user_id`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='投诉表';

-- 用户分类标签表
CREATE TABLE IF NOT EXISTS `t_user_category`
(
    `id`          BIGINT   NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
    `categories`  JSON     NOT NULL COMMENT '分类列表JSON数组',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户分类标签表';

-- ==================== 恋爱打卡系统新增表 ====================

-- 情侣信息表（扩展情侣资料）
CREATE TABLE IF NOT EXISTS love_couple_profile
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    couple_id      BIGINT NOT NULL COMMENT '情侣ID',
    user_id        BIGINT COMMENT '当前登录用户ID',
    user_name      VARCHAR(50) COMMENT '用户昵称（从sys_user获取）',
    user_avatar    VARCHAR(255) COMMENT '用户头像（从sys_user获取）',
    partner_id     BIGINT COMMENT '伴侣用户ID',
    partner_name   VARCHAR(50) COMMENT '伴侣昵称（从sys_user获取）',
    partner_avatar VARCHAR(255) COMMENT '伴侣头像（从sys_user获取）',
    start_date     DATE COMMENT '恋爱开始日期',
    create_time    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_couple_id (couple_id),
    INDEX idx_user_id (user_id)
) COMMENT ='情侣信息表';

-- 打卡记录表
CREATE TABLE IF NOT EXISTS love_checkin
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '打卡ID',
    couple_id     BIGINT NOT NULL COMMENT '关联情侣ID',
    user_id       BIGINT NOT NULL COMMENT '打卡用户ID',
    nickname      VARCHAR(50) COMMENT '打卡用户昵称',
    avatar        VARCHAR(255) COMMENT '打卡用户头像URL',
    content       TEXT COMMENT '打卡内容',
    images        JSON COMMENT '打卡图片数组',
    like_count    INT      DEFAULT 0 COMMENT '点赞数',
    comment_count INT      DEFAULT 0 COMMENT '评论数',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '打卡时间',
    INDEX idx_couple_id (couple_id),
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
) COMMENT ='打卡记录表';

-- 打卡点赞表
CREATE TABLE IF NOT EXISTS love_checkin_like
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    record_id   BIGINT NOT NULL COMMENT '打卡记录ID',
    user_id     BIGINT NOT NULL COMMENT '点赞用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY uk_record_user (record_id, user_id),
    INDEX idx_record_id (record_id),
    INDEX idx_user_id (user_id)
) COMMENT ='打卡点赞表';

-- 周打卡表
CREATE TABLE IF NOT EXISTS love_week_checkin
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '周打卡ID',
    couple_id    BIGINT NOT NULL COMMENT '关联情侣ID',
    week_start   DATE   NOT NULL COMMENT '本周一日期',
    day_num      INT    NOT NULL COMMENT '星期几(1=周一,7=周日)',
    checked      TINYINT(1) DEFAULT 0 COMMENT '是否已打卡(0:未打卡,1:已打卡)',
    user_id      BIGINT COMMENT '打卡用户ID',
    checkin_date DATE COMMENT '实际打卡日期',
    create_time  DATETIME   DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_couple_week_day (couple_id, week_start, day_num),
    INDEX idx_couple_week (couple_id, week_start)
) COMMENT ='周打卡表';

-- 愿望清单表
CREATE TABLE IF NOT EXISTS love_wishlist_item
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '心愿ID',
    couple_id   BIGINT       NOT NULL COMMENT '关联情侣ID',
    user_id     BIGINT       NOT NULL COMMENT '创建用户ID',
    title       VARCHAR(100) NOT NULL COMMENT '心愿标题',
    description VARCHAR(500) COMMENT '心愿描述',
    completed   TINYINT(1) DEFAULT 0 COMMENT '是否已完成(0:未完成,1:已完成)',
    create_time DATETIME   DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_couple_id (couple_id),
    INDEX idx_user_id (user_id)
) COMMENT ='愿望清单表';

-- ==================== 情侣申请与通知系统 ====================

-- 情侣申请表
CREATE TABLE IF NOT EXISTS love_couple_request
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '请求ID',
    from_user_id BIGINT NOT NULL COMMENT '发起者用户ID',
    to_user_id   BIGINT NOT NULL COMMENT '接收者用户ID',
    start_date   DATE COMMENT '提议的恋爱开始日期',
    status       TINYINT  DEFAULT 0 COMMENT '状态: 0-待处理, 1-已接受, 2-已拒绝',
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_to_user (to_user_id),
    INDEX idx_from_user (from_user_id)
) COMMENT ='情侣申请表';

-- 情侣通知表
CREATE TABLE IF NOT EXISTS love_notification
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '通知ID',
    user_id          BIGINT      NOT NULL COMMENT '接收通知的用户ID',
    type             VARCHAR(20) NOT NULL COMMENT '通知类型: couple_request/couple_accept/couple_reject',
    from_user_id     BIGINT COMMENT '发起者用户ID',
    from_user_name   VARCHAR(50) COMMENT '发起者昵称',
    from_user_avatar VARCHAR(255) COMMENT '发起者头像',
    content          VARCHAR(500) COMMENT '通知内容',
    related_id       BIGINT COMMENT '关联的请求ID',
    is_read          TINYINT  DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
    create_time      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (user_id, is_read)
) COMMENT ='情侣通知表';

-- 悄悄话消息表（情侣聊天）
CREATE TABLE IF NOT EXISTS love_whisper
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    from_user_id BIGINT NOT NULL COMMENT '发送者ID',
    to_user_id   BIGINT NOT NULL COMMENT '接收者ID',
    content      TEXT   NOT NULL COMMENT '消息内容',
    msg_type     TINYINT  DEFAULT 1 COMMENT '消息类型: 1-文本, 2-图片',
    is_read      TINYINT  DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_pair_time (from_user_id, to_user_id, create_time),
    INDEX idx_to_user (to_user_id, is_read)
) COMMENT ='悄悄话消息表' CHARSET = utf8mb4;