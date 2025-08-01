# Redis 配置说明

## 默认配置

本项目默认配置为**无密码**的Redis连接。如果您的Redis实例没有设置密码验证，无需修改任何配置。

## 环境变量配置

在 `.env` 文件中，Redis相关配置如下：

```bash
# Redis配置
REDIS_HOST=localhost      # Redis服务器地址
REDIS_PORT=6379          # Redis端口
REDIS_PASSWORD=          # Redis密码（空表示无密码）
REDIS_DB=0               # Redis数据库编号
```

## 不同场景的配置

### 1. 无密码Redis（默认）
```bash
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_DB=0
```

### 2. 有密码的Redis
```bash
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password
REDIS_DB=0
```

### 3. 远程Redis服务器
```bash
REDIS_HOST=192.168.1.100
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password
REDIS_DB=0
```

### 4. Redis云服务
```bash
REDIS_HOST=your-redis-cloud.com
REDIS_PORT=6379
REDIS_PASSWORD=your_cloud_redis_password
REDIS_DB=0
```

## 连接URL格式

系统会根据是否设置密码自动生成正确的Redis连接URL：

- **无密码**: `redis://localhost:6379/0`
- **有密码**: `redis://:password@localhost:6379/0`

## 验证Redis连接

您可以使用以下命令验证Redis连接是否正常：

### 1. 使用redis-cli测试
```bash
# 无密码连接
redis-cli -h localhost -p 6379 ping

# 有密码连接
redis-cli -h localhost -p 6379 -a your_password ping
```

### 2. 使用Python测试
```python
import redis
from decouple import config

# 读取配置
redis_host = config('REDIS_HOST', default='localhost')
redis_port = config('REDIS_PORT', default=6379, cast=int)
redis_password = config('REDIS_PASSWORD', default='')
redis_db = config('REDIS_DB', default=0, cast=int)

# 创建连接
if redis_password:
    r = redis.Redis(host=redis_host, port=redis_port, password=redis_password, db=redis_db)
else:
    r = redis.Redis(host=redis_host, port=redis_port, db=redis_db)

# 测试连接
try:
    r.ping()
    print("Redis连接成功!")
except Exception as e:
    print(f"Redis连接失败: {e}")
```

## 常见问题

### Q: 我的Redis没有密码，但连接失败
**A**: 确保 `REDIS_PASSWORD` 在 `.env` 文件中为空值：
```bash
REDIS_PASSWORD=
```

### Q: 我想给Redis设置密码
**A**: 在Redis配置文件中添加：
```bash
# 编辑 redis.conf
requirepass your_password

# 重启Redis服务
sudo systemctl restart redis
```

然后更新 `.env` 文件：
```bash
REDIS_PASSWORD=your_password
```

### Q: 连接Redis时出现认证错误
**A**: 检查以下几点：
1. 确认Redis是否真的设置了密码
2. 检查 `.env` 文件中的密码是否正确
3. 确认Redis服务正在运行

### Q: 如何检查Redis是否在运行
**A**: 
```bash
# 检查Redis进程
ps aux | grep redis

# 检查Redis端口
netstat -tlnp | grep 6379

# 使用systemctl检查状态（Linux）
systemctl status redis
```

## 项目中Redis的使用

本项目主要将Redis用作Django的缓存后端。Redis在项目中的作用：

1. **缓存**: 提升应用性能
2. **会话存储**: 支持分布式会话（如需要）
3. **临时数据存储**: 存储临时计算结果

如果您不需要使用Redis，可以将缓存配置改为本地内存缓存：

```python
# 在 settings.py 中修改
CACHES = {
    'default': {
        'BACKEND': 'django.core.cache.backends.locmem.LocMemCache',
        'LOCATION': 'unique-snowflake',
    }
}
```