# API 文档

## 统一响应格式

所有API接口都使用统一的响应格式：

```json
{
    "code": 0,          // 0表示成功，非0表示失败
    "message": "成功",   // 响应消息
    "data": {}          // 响应数据
}
```

## 1. 注册中心 API

### 1.1 服务注册

**请求**
- URL: `POST /api/register`
- Content-Type: `application/json`

**请求参数**
```json
{
    "service_name": "time-service",     // 服务名称
    "service_id": "uuid-string",        // 服务唯一标识
    "ip_address": "127.0.0.1",         // IP地址
    "port": 8002                       // 端口号
}
```

**响应示例**
```json
{
    "code": 0,
    "message": "服务注册成功",
    "data": null
}
```

### 1.2 服务注销

**请求**
- URL: `POST /api/unregister`
- Content-Type: `application/json`

**请求参数**
```json
{
    "service_name": "time-service",     // 服务名称
    "service_id": "uuid-string",        // 服务唯一标识（可选）
    "ip_address": "127.0.0.1",         // IP地址
    "port": 8002                       // 端口号
}
```

**响应示例**
```json
{
    "code": 0,
    "message": "服务注销成功",
    "data": null
}
```

### 1.3 心跳检测

**请求**
- URL: `POST /api/heartbeat`
- Content-Type: `application/json`

**请求参数**
```json
{
    "service_id": "uuid-string",        // 服务唯一标识（可选）
    "ip_address": "127.0.0.1",         // IP地址
    "port": 8002                       // 端口号
}
```

**响应示例**
```json
{
    "code": 0,
    "message": "心跳检测成功",
    "data": null
}
```

### 1.4 服务发现

**请求**
- URL: `GET /api/discovery`
- 查询参数: `name` (可选) - 服务名称

**请求示例**
```bash
# 获取所有服务
curl http://127.0.0.1:8001/api/discovery

# 获取特定服务
curl "http://127.0.0.1:8001/api/discovery?name=time-service"
```

**响应示例**
```json
{
    "code": 0,
    "message": "服务发现成功",
    "data": [
        {
            "service_name": "time-service",
            "service_id": "uuid-string-1",
            "ip_address": "127.0.0.1",
            "port": 8002
        },
        {
            "service_name": "time-service",
            "service_id": "uuid-string-2",
            "ip_address": "127.0.0.1",
            "port": 8003
        }
    ]
}
```

## 2. 时间服务 API

### 2.1 获取当前时间

**请求**
- URL: `GET /api/getDateTime`
- 查询参数: `style` (可选) - 时间格式

**时间格式说明**
- `full`: `yyyy-MM-dd HH:mm:ss` (默认)
- `date`: `yyyy-MM-dd`
- `time`: `HH:mm:ss`
- `unix`: Unix时间戳

**请求示例**
```bash
# 获取完整时间格式
curl "http://127.0.0.1:8002/api/getDateTime?style=full"

# 获取日期格式
curl "http://127.0.0.1:8002/api/getDateTime?style=date"

# 获取时间格式
curl "http://127.0.0.1:8002/api/getDateTime?style=time"

# 获取Unix时间戳
curl "http://127.0.0.1:8002/api/getDateTime?style=unix"
```

**响应示例**
```json
{
    "code": 0,
    "message": "获取时间成功",
    "data": {
        "result": "2024-01-20 15:30:45",
        "service_id": "uuid-string"
    }
}
```

## 3. 客户端服务 API

### 3.1 获取客户端信息

**请求**
- URL: `GET /api/getInfo`

**请求示例**
```bash
curl http://127.0.0.1:8004/api/getInfo
```

**成功响应示例**
```json
{
    "code": 0,
    "message": "获取客户端信息成功",
    "data": {
        "result": "Hello Kingsoft Cloud Star Camp - client-uuid - 2024-01-20 15:30:45",
        "error": null
    }
}
```

**失败响应示例**
```json
{
    "code": 1,
    "message": "获取客户端信息失败",
    "data": {
        "result": null,
        "error": "时间服务不可用"
    }
}
```

## 4. 错误码说明

| 错误码 | 说明 |
|-------|------|
| 0 | 成功 |
| 1 | 业务逻辑错误 |
| 400 | 请求参数错误 |
| 500 | 服务器内部错误 |

## 5. 服务调用流程

### 5.1 服务启动流程
1. 注册中心启动并监听8001端口
2. 时间服务启动，自动向注册中心注册
3. 客户端服务启动，自动向注册中心注册
4. 所有服务开始定时发送心跳（20秒间隔）

### 5.2 服务调用流程
1. 客户端接收到`/api/getInfo`请求
2. 客户端通过注册中心发现可用的时间服务实例
3. 客户端选择一个时间服务实例并调用其API
4. 时间服务返回当前时间
5. 客户端整合结果并返回给调用方

### 5.3 健康检查机制
- 每个服务实例每20秒向注册中心发送心跳
- 注册中心检查服务最后心跳时间，超过60秒认为服务不健康
- 服务发现时只返回健康的服务实例

## 6. 测试用例

### 6.1 完整流程测试
```bash
# 1. 启动所有服务
./start_all.sh

# 2. 等待5秒让所有服务完成注册
sleep 5

# 3. 查看注册的服务
curl http://127.0.0.1:8001/api/discovery

# 4. 调用客户端服务
curl http://127.0.0.1:8004/api/getInfo

# 5. 直接调用时间服务
curl "http://127.0.0.1:8002/api/getDateTime?style=full"

# 6. 停止所有服务
./stop_all.sh
```

### 6.2 负载均衡测试
```bash
# 多次调用客户端服务，观察调用的时间服务实例
for i in {1..5}; do
    echo "第 $i 次调用:"
    curl http://127.0.0.1:8004/api/getInfo
    echo ""
done
```

### 6.3 故障恢复测试
```bash
# 1. 停止一个时间服务实例
kill -9 $(lsof -ti :8002)

# 2. 调用客户端服务，应该自动切换到其他实例
curl http://127.0.0.1:8004/api/getInfo

# 3. 重启时间服务实例
python start_time_service.py 1 &
```