# 烽火云短信平台（Beacon Cloud）

> 基于 Spring Cloud Alibaba 的分布式短信平台：统一接入、策略校验、通道路由、CMPP 网关下发、状态报告回推与全链路日志检索。

## 项目简介

Beacon Cloud 是一套完整的短信发送与管理系统，覆盖从「客户调用接口」到「短信落到运营商网关」的全链路：

- 统一的短信下发入口，内置 apikey、IP 白名单、签名、模板、手机号格式等校验
- 基于责任链的发送策略：黑名单、敏感词、限流、分时段、通道路由与切换、余额扣费
- 基于 Netty 的 CMPP 网关，长连接对接运营商，支持消息回执与状态报告
- 异步回推状态报告给客户业务系统
- 全链路日志写入 Elasticsearch，后台支持多维度检索
- 完整的后台管理：客户、通道、模板、黑名单、敏感词、限流、费用与系统权限

## 架构与消息流向

```
                 ┌──────────────┐
  客户系统 ──────▶│  beacon-api  │  参数校验 / 责任链前置校验
                 └──────┬───────┘
                        │ sms_pre_send_topic
                 ┌──────▼──────────┐
                 │ beacon-strategy │  黑名单 / 敏感词 / 限流 / 路由 / 扣费
                 └──┬─────────┬────┘
     sms_gateway_   │         │ sms_write_log_topic
     topic_{id}     │         └──────────▶ beacon-search ──▶ Elasticsearch
                    ▼
             ┌─────────────────┐   CMPP 2.0（Netty 长连接）
             │ beacon-smsgateway│ ───────────────────▶ 运营商网关
             └────────┬────────┘
                      │ 状态报告 sms_push_report_topic
                      ▼
                 beacon-push ──HTTP──▶ 客户回调地址

  公共支撑：beacon-cache（Redis 统一读写）· beacon-monitor（xxl-job 巡检）· beacon-webmaster（后台）
```

## 模块说明

| 模块 | 说明 |
| --- | --- |
| `beacon-common` | 公共模型、常量、工具类：`StandardSubmit`、雪花 ID、运营商识别、统一异常 |
| `beacon-api` | 对外短信接口。参数校验 → 责任链校验 → 投递 `sms_pre_send_topic` |
| `beacon-strategy` | 核心策略模块。消费前置队列，完成全部业务校验、通道路由与扣费，投递网关队列 |
| `beacon-smsgateway` | CMPP 网关。Netty 长连接对接运营商，消费网关队列并下发短信 |
| `beacon-push` | 状态报告回推，通过 HTTP 回调客户业务地址 |
| `beacon-search` | 短信发送日志写入 Elasticsearch，并提供检索接口 |
| `beacon-monitor` | xxl-job 定时任务：余额巡检、队列堆积告警、邮件通知 |
| `beacon-cache` | Redis 统一读写服务，其他模块通过 Feign 调用，屏蔽序列化细节 |
| `beacon-webmaster` | 后台管理端。Spring Security + JWT 鉴权，MyBatis-Plus 持久层，Bootstrap/Layui 前端 |
| `beacon-test` | 开发调试模块，存放 Mapper 与联调用例 |

## 技术栈

| 分类 | 选型 |
| --- | --- |
| 语言 / 构建 | JDK 17、Maven |
| 基础框架 | Spring Boot 2.6.11、Spring Cloud 2021.0.4、Spring Cloud Alibaba 2021.0.4.0 |
| 注册 / 配置中心 | Nacos 2.x |
| 消息队列 | RabbitMQ（JSON 消息转换 + confirm / return 机制） |
| 缓存 | Redis |
| 数据库 | MySQL 8.0、MyBatis-Plus |
| 搜索 | Elasticsearch 7.x（RestHighLevelClient） |
| 网关协议 | Netty 4 + CMPP 2.0 |
| 安全 | Spring Security、JJWT、easy-captcha（图形验证码） |
| 任务调度 | xxl-job |
| 其他 | hippo4j（动态线程池）、Hutool DFA + IKAnalyzer（敏感词）、FastJSON、Lombok |

## 消息队列拓扑

| 队列 / RoutingKey | 生产者 → 消费者 |
| --- | --- |
| `sms_pre_send_topic` | `beacon-api` → `beacon-strategy` |
| `sms_gateway_topic_{channelId}` | `beacon-strategy` → `beacon-smsgateway` |
| `sms_write_log_topic` | `beacon-strategy` → `beacon-search` |
| `sms_push_report_topic` | `beacon-smsgateway` → `beacon-push` |
| `mobile_area_operator_topic` | `beacon-strategy` → `beacon-webmaster` |

## 目录结构

```
beacon_cloud
├── beacon-api          # 对外短信接口
├── beacon-cache        # Redis 统一读写服务
├── beacon-common       # 公共模块
├── beacon-monitor      # 定时任务与告警
├── beacon-push         # 状态报告回推
├── beacon-search       # ES 日志与检索
├── beacon-smsgateway   # CMPP 网关
├── beacon-strategy     # 发送策略
├── beacon-test         # 调试模块
├── beacon-webmaster    # 后台管理
└── pom.xml             # 父 POM，统一依赖版本
```

## 环境要求

| 组件 | 版本 | 说明 |
| --- | --- | --- |
| JDK | 17 | 父 POM 已指定 `source/target = 17` |
| Maven | 3.8+ | |
| MySQL | 8.0 | 业务库 `beacon_cloud` |
| Redis | 6.0+ | 缓存与会话 |
| RabbitMQ | 3.9+ | 模块间异步通信 |
| Nacos | 2.x | 注册中心 + 配置中心 |
| Elasticsearch | 7.x | 仅 `beacon-search` 需要 |
| xxl-job | 2.x | 仅 `beacon-monitor` 需要，需先部署调度中心 |

## 快速开始

### 1. 克隆代码

```bash
git clone https://github.com/你的用户名/beacon-cloud.git
cd beacon-cloud
```

### 2. 初始化数据库

创建数据库 `beacon_cloud`（字符集 `utf8mb4`），并按 `beacon-webmaster` 中的实体类建立业务表：
`client_business`、`client_balance`、`client_template`、`client_sign`、`client_channel`、
`channel`、`mobile_black`、`mobile_dirty_word`、`sms_user`、`sms_menu`、`sms_role` 等。

### 3. 配置 Nacos

各模块的 `application.yml` / `bootstrap.yml` **只保留服务名与注册中心地址**，业务配置统一放在 Nacos 配置中心，
Data ID 命名规则为 `beacon-<模块名>-dev.yml`。需要准备的配置项如下：

| 模块 | 必需配置 |
| --- | --- |
| `beacon-api` | RabbitMQ 连接、`headers`（用于取真实 IP 的请求头列表，逗号分隔）、`filters`（校验链顺序，默认 `apikey,ip,sign,template`）、`snowflake.machineId` / `snowflake.serviceId` |
| `beacon-cache` | `spring.redis.*` |
| `beacon-strategy` | RabbitMQ 连接、`gateway.sendtopic`（发往网关的队列前缀）、`snowflake.*` |
| `beacon-smsgateway` | RabbitMQ 连接、`spring.dynamic.thread-pool.*`（hippo4j 动态线程池） |
| `beacon-webmaster` | `spring.datasource.*`、`spring.redis.*`、`token.secret`、`token.expireTime` |
| `beacon-search` | `elasticsearch.hostAndPorts`、`elasticsearch.username`、`elasticsearch.password` |
| `beacon-monitor` | `xxl.job.admin.addresses`、`xxl.job.executor.appname` / `port` / `logpath`、`spring.mail.*` |

> 修改 Nacos 中 `spring.cloud.nacos.*.server-addr` 指向你自己的 Nacos 实例。

### 4. 编译

```bash
mvn clean install -DskipTests
```

### 5. 启动顺序

```
Nacos → MySQL / Redis / RabbitMQ
  ↓
beacon-cache → beacon-webmaster → beacon-api → beacon-strategy
  ↓
beacon-smsgateway → beacon-push → beacon-search → beacon-monitor
```

`beacon-cache` 必须先起来，`beacon-api`、`beacon-strategy` 等模块启动时依赖它的 Feign 接口。

### 6. 访问后台

浏览器打开 `http://localhost:8888/login.html`（端口以 Nacos 配置为准），使用 `sms_user` 表中的账号登录。

## 短信下发接口

**POST** `/sms/single_send`

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `apikey` | string | 是 | 客户身份标识 |
| `mobile` | string | 是 | 接收手机号 |
| `text` | string | 是 | 短信内容 |
| `state` | int | 是 | 0-验证码，1-通知，2-营销 |
| `uid` | string | 否 | 客户业务侧唯一标识，用于状态报告关联 |

```bash
curl -X POST http://localhost:8080/sms/single_send \
  -H "Content-Type: application/json" \
  -d '{
        "apikey": "your-apikey",
        "mobile": "13800138000",
        "text": "您的验证码是 1234，5 分钟内有效",
        "state": 0,
        "uid": "biz-order-001"
      }'
```

成功响应（仅代表已接收，不代表已送达）：

```json
{
  "code": 0,
  "msg": "接收成功"
}
```

参数不合法：

```json
{
  "code": -10,
  "msg": "参数不合法！"
}
```

## 设计要点

- **责任链校验**：`CheckFilterContext` 按 `filters` 配置的顺序装配校验器，新增校验规则只需实现 `CheckFilter` 并注册为 `@Service("名称")`
- **策略可插拔**：`beacon-strategy` 中每个策略过滤器独立成类，通过 `StrategyFilterContext` 串成链，便于按需增减
- **异步解耦**：模块之间只通过 RabbitMQ 通信，接口模块不做任何耗时校验，保证下发接口的响应速度
- **缓存统一出口**：所有 Redis 操作收敛到 `beacon-cache`，避免各模块的序列化方式不一致
- **日志可检索**：发送记录异步写 ES，不阻塞主链路

## 已知限制

- `beacon-cache` 使用了一个外部 Redis 客户端封装，需要在本地安装对应依赖后才能完整构建
- 仓库暂未包含数据库建表脚本，首次运行需按实体类手动建表
- 未提供 Docker Compose 一键启动，中间件需自行部署

## Roadmap

- [ ] 补充完整的建表 SQL 与 Nacos 配置示例
- [ ] 提供 `docker-compose.yml` 一键拉起 MySQL / Redis / RabbitMQ / Nacos
- [ ] 补充单元测试与 GitHub Actions 持续集成
- [ ] 增加发送量统计与可视化看板

## 贡献

欢迎提交 Issue 与 Pull Request。提交前请确保：

1. 代码通过 `mvn clean install`
2. 新增配置项同步更新本文档
3. 提交信息遵循 `feat:` / `fix:` / `docs:` 等前缀

## License

本项目基于 [MIT](LICENSE) 协议开源。

---

如果这个项目对你有帮助，欢迎点个 ⭐ Star。
