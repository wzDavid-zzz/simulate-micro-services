# Django 微服务项目

这是一个基于Django的微服务架构演示项目，从原始的Java Spring Boot微服务项目改写而来。项目实现了服务注册与发现、服务间通信、心跳检测等微服务核心功能。

## 🏗️ 项目架构

```
django_microservices/
├── microservices/          # Django主项目配置
├── registry/               # 服务注册中心
├── time_service/           # 时间服务
├── client_service/         # 客户端服务
├── requirements.txt        # Python依赖包
├── .env.example           # 环境变量示例
├── start_all.sh           # 启动所有服务脚本
├── stop_all.sh            # 停止所有服务脚本
├── REDIS_CONFIG.md        # Redis配置说明
└── logs/                  # 日志目录
```

## 🎯 服务说明

### 1. 注册中心 (Registry Service)
- **端口**: 8001
- **功能**: 
  - 服务注册 (`POST /api/register`)
  - 服务注销 (`POST /api/unregister`)
  - 心跳检测 (`POST /api/heartbeat`)
  - 服务发现 (`GET /api/discovery`)
- **数据存储**: MySQL数据库

### 2. 时间服务 (Time Service)
- **端口**: 8002, 8003 (支持多实例)
- **功能**: 
  - 提供时间查询API (`GET /api/getDateTime?style=full`)
  - 支持多种时间格式: date, time, unix, full
- **服务治理**: 自动注册到注册中心，定时发送心跳

### 3. 客户端服务 (Client Service)
- **端口**: 8004, 8005 (支持多实例)
- **功能**:
  - 提供客户端信息API (`GET /api/getInfo`)
  - 通过服务发现调用时间服务
  - 实现服务间通信和负载均衡

## 🚀 快速开始

### 环境要求

- Python 3.8+
- MySQL 5.7+
- Redis 6.0+ (无需密码验证)

### 1. 安装依赖

```bash
# 克隆项目
cd django_microservices

# 安装Python依赖
pip install -r requirements.txt
```

### 2. 配置环境

```bash
# 复制环境变量文件
cp .env.example .env

# 编辑环境变量（根据你的环境修改数据库配置）
# 注意：Redis密码默认为空，如果你的Redis有密码请设置REDIS_PASSWORD
# 详细的Redis配置说明请查看 REDIS_CONFIG.md
vim .env
```

### 3. 测试Redis连接（推荐）

```bash
# 测试Redis连接是否正常
python test_redis.py
```

### 4. 初始化数据库

```bash
# 执行数据库迁移
python manage.py makemigrations
python manage.py migrate

# 创建超级用户（可选）
python manage.py createsuperuser
```

### 5. 启动服务

#### 方式一：一键启动所有服务
```bash
./start_all.sh
```

#### 方式二：单独启动服务
```bash
# 启动注册中心
python start_registry.py

# 启动时间服务实例
python start_time_service.py 1    # 实例1
python start_time_service.py 2    # 实例2

# 启动客户端服务实例
python start_client_service.py 1  # 实例1
python start_client_service.py 2  # 实例2
```

### 6. 停止服务

```bash
./stop_all.sh
```

## 📱 API 测试

### 服务访问地址
- 注册中心: http://127.0.0.1:8001
- 时间服务实例1: http://127.0.0.1:8002  
- 时间服务实例2: http://127.0.0.1:8003
- 客户端服务实例1: http://127.0.0.1:8004
- 客户端服务实例2: http://127.0.0.1:8005

### API 示例

#### 1. 查看所有注册的服务
```bash
curl http://127.0.0.1:8001/api/discovery
```

#### 2. 查看特定服务
```bash
curl "http://127.0.0.1:8001/api/discovery?name=time-service"
```

#### 3. 调用时间服务
```bash
curl "http://127.0.0.1:8002/api/getDateTime?style=full"
curl "http://127.0.0.1:8002/api/getDateTime?style=date"
curl "http://127.0.0.1:8002/api/getDateTime?style=time"
curl "http://127.0.0.1:8002/api/getDateTime?style=unix"
```

#### 4. 调用客户端服务（会自动调用时间服务）
```bash
curl http://127.0.0.1:8004/api/getInfo
curl http://127.0.0.1:8005/api/getInfo
```

## 🔧 技术栈

- **Web框架**: Django 4.2.7
- **API框架**: Django REST Framework 3.14.0
- **数据库**: MySQL 8.0 + Django ORM
- **缓存**: Redis 6.0
- **服务通信**: HTTP + requests库
- **任务调度**: Python threading
- **配置管理**: python-decouple

## 🌟 功能特性

### 1. 服务注册与发现
- ✅ 自动服务注册
- ✅ 健康检查机制
- ✅ 服务发现API
- ✅ 服务注销功能

### 2. 高可用性
- ✅ 支持多实例部署
- ✅ 心跳检测（20秒间隔）
- ✅ 自动故障切换
- ✅ 服务状态监控

### 3. 服务治理
- ✅ 统一异常处理
- ✅ 统一响应格式
- ✅ 请求日志记录
- ✅ 配置管理

### 4. 开发友好
- ✅ 热重载支持
- ✅ 详细日志输出
- ✅ 管理后台
- ✅ API文档

## 📊 项目对比

| 功能特性 | Java版本 | Django版本 |
|---------|---------|-----------|
| 服务注册 | ✅ | ✅ |
| 服务发现 | ✅ | ✅ |
| 心跳检测 | ✅ | ✅ |
| 多实例支持 | ✅ | ✅ |
| 数据库支持 | MyBatis-Plus | Django ORM |
| 服务通信 | OpenFeign | requests |
| 配置管理 | application.yaml | .env + settings.py |
| 启动方式 | jar包 | Python脚本 |

## 🐛 故障排除

### 常见问题

1. **服务启动失败**
   - 检查端口是否被占用
   - 确认数据库连接配置
   - 查看日志文件排查错误

2. **服务注册失败**
   - 确保注册中心先启动
   - 检查网络连接
   - 验证服务配置

3. **心跳检测失败**
   - 检查注册中心状态
   - 验证服务实例配置
   - 查看网络连接

4. **Redis连接失败**
   - 确保Redis服务正在运行
   - 检查Redis端口配置
   - 如果Redis有密码，请设置REDIS_PASSWORD环境变量

### 日志文件
- `logs/registry.log` - 注册中心日志
- `logs/time_service_1.log` - 时间服务实例1日志
- `logs/time_service_2.log` - 时间服务实例2日志
- `logs/client_service_1.log` - 客户端服务实例1日志
- `logs/client_service_2.log` - 客户端服务实例2日志

## 🤝 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📝 许可证

本项目基于 MIT 许可证开源 - 查看 [LICENSE](LICENSE) 文件了解详情

## 👥 作者

- 项目移植：基于原Java Spring Boot微服务项目改写
- Django实现：重新设计和实现微服务架构