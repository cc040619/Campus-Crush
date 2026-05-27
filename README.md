# 大学恋爱记录系统 🌸

一个清新粉色主题的大学恋爱记录与校园社区平台，支持恋爱记录、社区互动、即时通讯等功能。

## 技术栈

### 后端
- Spring Boot 4.0.5
- JDK 17
- MySQL 8.0+
- MyBatis + PageHelper
- Redis（缓存 & 限流）
- WebSocket（即时通讯）
- JWT（JJWT，用户认证）
- Spring Security（BCrypt 密码加密）
- Spring Mail（QQ 邮箱验证码）
- 阿里云 OSS（文件存储）
- Lombok

### 前端
- Vue 3 + 组合式 API
- Vite
- Element Plus
- Axios
- GSAP（动画）

## 快速开始

### 环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Redis

### 1. 数据库初始化

```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source sql/init.sql
```

### 2. 后端启动

修改 `src/main/resources/application.yml` 中的数据库连接信息和 Redis 配置。

```bash
# 编译运行
mvn clean spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/Campus-Crush-0.0.1-SNAPSHOT.jar
```

后端默认运行在：http://localhost:8081

### 3. 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端默认运行在：http://localhost:3000 ，API 请求代理到后端 8081 端口。

## 默认账号

- 用户名：admin
- 密码：admin（MD5 加密存储）

## 功能模块

### 1. 用户系统
- 用户名/密码登录
- 邮箱验证码登录
- 用户注册（含限流保护）
- 个人信息管理（昵称、头像、简介、性别）
- 邮箱绑定
- 登录冻结机制（5 次失败冻结 15 分钟）
- JWT Token 认证（HttpOnly Cookie）

### 2. 恋爱记录
- **恋爱打卡** — 情侣每日打卡，支持图文、点赞
- **周打卡** — 每周打卡追踪
- **恋爱纪念日** — 纪念日增删改查，倒计时提醒
- **恋爱相册** — 照片上传（OSS），浏览管理
- **恋爱日记** — 日记增删改查，心情标签
- **愿望清单** — 情侣共同心愿的添加、完成、删除

### 3. 情侣系统
- 用户搜索配对
- 情侣申请/接受/拒绝流程
- 情侣通知
- 情侣信息展示（在一起天数、双方头像昵称）
- 解除绑定

### 4. 社区模块
- **帖子** — 发布/编辑/删除，图文混排，分类标签，位置标记，可见性控制（公开/好友/私密）
- **评论** — 支持嵌套回复（楼中楼）
- **点赞 & 收藏** — 帖子点赞收藏，评论点赞
- **关注** — 关注/取消关注，关注列表
- **好友** — 好友申请/同意/拒绝，好友列表，昵称备注，用户搜索
- **互动通知** — 点赞、收藏、评论通知，关注通知
- **热门话题** — 基于浏览量的热门帖子排行

### 5. 即时通讯
- **社区聊天** — WebSocket 实时消息（`/ws/chat`），消息已读/置顶/清除/搜索
- **悄悄话** — 情侣专属 WebSocket 聊天（`/ws/whisper`）
- **投诉举报** — 聊天投诉功能

### 6. 统计与工具
- **个人统计** — 基础数据、社交数据、产出数据统计图表
- **情侣统计** — 情侣概览和图表数据
- **天气查询** — 当日天气信息
- **兴趣标签** — 53 个可选分类标签的增删改查

### 7. 邮箱服务
- QQ 邮箱验证码发送
- 纪念日邮件提醒设置
- 提醒列表（按剩余天数排序）

## 项目结构

```
Campus-Crush/
├── src/main/java/com/cc/campuscrush/
│   ├── common/              # 通用类（Result、Redis常量）
│   ├── config/              # 配置类（CORS、Redis、Security、WebSocket、异步线程池）
│   ├── controller/          # 控制器层（22个）
│   ├── entity/              # 实体类（25个）
│   ├── exception/           # 自定义异常
│   ├── handler/             # 全局异常处理器
│   ├── interceptor/         # JWT登录拦截器
│   ├── mapper/              # MyBatis Mapper接口（23个）
│   ├── service/             # 服务层接口（23个）
│   │   └── impl/            # 服务层实现（22个）
│   ├── utils/               # 工具类（OSS、JWT、限流、Redis上下文）
│   ├── vo/                  # 视图对象
│   └── websocket/           # WebSocket处理器（聊天、悄悄话）
├── src/main/resources/
│   ├── mapper/              # MyBatis XML映射文件
│   └── application.yml      # 应用配置
├── sql/                     # 数据库初始化脚本
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── api/             # API接口封装
│   │   ├── components/      # 公共组件
│   │   ├── composables/     # 组合式函数
│   │   ├── data/            # 静态数据
│   │   ├── directives/      # 自定义指令
│   │   ├── router/          # 路由配置
│   │   ├── utils/           # 工具函数
│   │   └── views/           # 页面组件（19个页面）
│   └── vite.config.js
└── pom.xml
```

## 数据库表

| 表名 | 说明 |
|------|------|
| `sys_user` | 用户表 |
| `love_anniversary` | 恋爱纪念日 |
| `love_album` | 恋爱相册 |
| `love_diary` | 恋爱日记 |
| `love_couple_profile` | 情侣信息表 |
| `love_couple_request` | 情侣申请表 |
| `love_notification` | 情侣通知表 |
| `love_checkin` | 打卡记录表 |
| `love_checkin_like` | 打卡点赞表 |
| `love_week_checkin` | 周打卡表 |
| `love_whisper` | 悄悄话消息表 |
| `love_wishlist_item` | 愿望清单表 |
| `t_post` | 社区帖子表 |
| `t_comment` | 评论表 |
| `t_like` | 点赞表 |
| `t_collect` | 收藏表 |
| `t_follow` | 关注表 |
| `t_friend` | 好友表 |
| `t_chat` | 聊天消息表 |
| `t_follow_notice` | 关注通知表 |
| `t_interaction_notice` | 互动通知表 |
| `t_post_browse` | 帖子浏览记录表 |
| `t_complaint` | 投诉表 |
| `t_user_category` | 用户分类标签表 |
| `email_settings` | 邮箱设置表 |

## API 接口概览

### 用户相关
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/user/login` | 密码登录 |
| POST | `/api/user/login/code` | 验证码登录 |
| POST | `/api/user/register` | 注册 |
| POST | `/api/user/logout` | 退出登录 |
| GET | `/api/user/info` | 获取个人信息 |
| PUT | `/api/user/profile` | 更新个人资料 |
| PUT | `/api/user/password` | 修改密码 |
| POST | `/api/user/send-code` | 发送验证码 |
| POST | `/api/user/bind-email` | 绑定邮箱 |

### 社区帖子
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/community/list` | 帖子列表 |
| GET | `/api/community/post/{id}` | 帖子详情 |
| POST | `/api/community/post` | 发布帖子 |
| PUT | `/api/community/post/{id}` | 编辑帖子 |
| DELETE | `/api/community/post/{id}` | 删除帖子 |
| POST | `/api/community/post/{id}/like` | 点赞/取消 |
| POST | `/api/community/post/{id}/collect` | 收藏/取消 |

### 评论
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/community/comment/list` | 评论列表 |
| POST | `/api/community/comment` | 发表评论 |
| DELETE | `/api/community/comment/{id}` | 删除评论 |
| POST | `/api/community/comment/{id}/like` | 评论点赞 |

### 关注 & 好友
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/community/follow/{userId}` | 关注/取消 |
| GET | `/api/community/follow/list` | 关注列表 |
| POST | `/api/community/friend/request` | 发送好友请求 |
| POST | `/api/community/friend/agree/{id}` | 同意好友请求 |
| GET | `/api/community/friend/list` | 好友列表 |

### 聊天
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/community/friend/chat/history/{userId}` | 聊天记录 |
| POST | `/api/community/friend/chat/search` | 搜索聊天记录 |
| POST | `/api/community/friend/chat/complaint` | 投诉举报 |
| WS | `/ws/chat` | WebSocket 实时聊天 |

### 情侣功能
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/couple/info` | 情侣信息 |
| POST | `/api/couple/request` | 发送情侣申请 |
| POST | `/api/couple/accept/{id}` | 接受申请 |
| POST | `/api/couple/reject/{id}` | 拒绝申请 |
| POST | `/api/couple/unbind` | 解除绑定 |

### 打卡 & 周打卡
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/checkin/list` | 打卡列表 |
| POST | `/api/checkin/create` | 创建打卡 |
| POST | `/api/checkin/like/{id}` | 打卡点赞 |
| GET | `/api/week/checkin` | 本周打卡状态 |
| PUT | `/api/week/checkin/update` | 更新打卡 |

### 悄悄话
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/whisper/contacts` | 联系人列表 |
| GET | `/api/whisper/history/{userId}` | 聊天记录 |
| WS | `/ws/whisper` | WebSocket 情侣聊天 |

### 纪念日 / 相册 / 日记 / 愿望清单
| 方法 | 路径 | 说明 |
|------|------|------|
| CRUD | `/api/anniversary/*` | 纪念日管理 |
| CRUD | `/api/album/*` | 相册管理 |
| CRUD | `/api/diary/*` | 日记管理 |
| CRUD | `/api/wishlist/*` | 愿望清单管理 |

### 其他
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/statistics/*` | 个人统计数据 |
| GET | `/api/stats/*` | 情侣统计数据 |
| GET | `/api/weather` | 天气查询 |
| POST | `/api/oss/upload` | 文件上传（OSS） |
| CRUD | `/api/user-category/*` | 兴趣标签管理 |
| CRUD | `/api/email/*` | 邮箱设置 |
| GET | `/api/community/interaction/*` | 互动通知 |

## 配色方案

- 主色：#FFC0CB（清新粉）
- 辅助色：#FFE6E6、#FFF0F5
- 文字主色：#333333
- 按钮色：#FF9999
- 边框/分割线：#F8E1E1
- 背景色：#FFFDFD

## 注意事项

1. 确保 MySQL 和 Redis 已启动
2. 数据库连接信息在 `application.yml` 中配置
3. 前端开发模式下，API 请求代理到后端 8081 端口
4. 密码采用 MD5 加密存储
5. 图片上传使用阿里云 OSS，需配置 AccessKey
6. 邮件验证码使用 QQ 邮箱 SMTP，需配置授权码
7. JWT Token 存储在 HttpOnly Cookie 中，过期时间 24 小时
8. 登录限流：同一 IP 每分钟最多 10 次请求

## 许可证

MIT License

---

Made with 💕 for lovers

## 作者信息
QQ：3417479720
wx：c17744070306