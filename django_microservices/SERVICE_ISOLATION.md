# 服务隔离配置说明

## 问题描述

在原始配置中，当单独启动注册中心时，由于Django加载了所有在 `INSTALLED_APPS` 中的应用，会导致 `time_service` 和 `client_service` 的 `apps.py` 中的 `ready()` 方法被调用，从而自动注册这些服务，即使它们实际上并没有启动对应的服务端口。

## 解决方案

### 1. 环境变量控制

通过 `MICROSERVICE_TYPE` 环境变量来控制哪些服务应该启动注册功能：

- `registry`: 仅启动注册中心
- `time_service`: 启动时间服务
- `client_service`: 启动客户端服务
- `all`: 启动所有服务（一键启动场景）

### 2. 端口检测机制

在 `apps.py` 的 `ready()` 方法中，不仅检查环境变量，还检查当前Django实例是否运行在对应服务的端口上，确保只有真正启动的服务才会注册。

## 修改的文件

### 1. `time_service/apps.py`
```python
def ready(self):
    service_type = os.environ.get('MICROSERVICE_TYPE', '').lower()
    
    if service_type in ['time_service', 'time', 'all', '']:
        import sys
        if 'runserver' in sys.argv:
            from decouple import config
            time_port_1 = str(config('TIME_PORT', default=8002))
            time_port_2 = str(config('TIME_PORT_2', default=8003))
            
            for arg in sys.argv:
                if ':' in arg and (time_port_1 in arg or time_port_2 in arg):
                    from .tasks import start_service_registration
                    start_service_registration()
                    break
```

### 2. `client_service/apps.py`
```python
def ready(self):
    service_type = os.environ.get('MICROSERVICE_TYPE', '').lower()
    
    if service_type in ['client_service', 'client', 'all', '']:
        import sys
        if 'runserver' in sys.argv:
            from decouple import config
            client_port_1 = str(config('CLIENT_PORT', default=8004))
            client_port_2 = str(config('CLIENT_PORT_2', default=8005))
            
            for arg in sys.argv:
                if ':' in arg and (client_port_1 in arg or client_port_2 in arg):
                    from .tasks import start_service_registration
                    start_service_registration()
                    break
```

### 3. 启动脚本修改

各个启动脚本都设置了相应的环境变量：

- `start_registry.py`: `MICROSERVICE_TYPE=registry`
- `start_time_service.py`: `MICROSERVICE_TYPE=time_service`
- `start_client_service.py`: `MICROSERVICE_TYPE=client_service`

## 使用方法

### 1. 单独启动注册中心
```bash
python start_registry.py
```
现在只会启动注册中心，不会有其他服务自动注册。

### 2. 单独启动时间服务
```bash
# 确保注册中心已启动
python start_time_service.py 1
```
时间服务会自动注册到注册中心。

### 3. 单独启动客户端服务
```bash
# 确保注册中心已启动
python start_client_service.py 1
```
客户端服务会自动注册到注册中心。

### 4. 一键启动所有服务
```bash
./start_all.sh
```
所有服务都会正常启动和注册。

## 测试验证

使用提供的测试脚本验证修改是否生效：

```bash
python test_single_service.py
```

该脚本会执行以下测试：
1. 单独启动注册中心，检查是否有其他服务误注册
2. 测试完整的服务注册流程

## 工作原理

### 1. 环境变量检查
首先检查 `MICROSERVICE_TYPE` 环境变量，如果不匹配当前服务类型，则直接跳过注册。

### 2. 命令检查
检查当前是否在执行 `runserver` 命令，只有在运行Django开发服务器时才进行注册。

### 3. 端口检查
检查命令行参数中的端口是否匹配当前服务的配置端口，确保只有在正确端口上运行的服务才会注册。

### 4. 双重保障
这种设计提供了双重保障：
- 环境变量控制粗粒度的服务类型
- 端口检查确保细粒度的实例匹配

## 兼容性

这种修改保持了原有功能的完整性：
- 一键启动脚本 `start_all.sh` 继续正常工作
- 各个单独启动脚本功能正常
- 所有API接口保持不变
- 服务注册和发现逻辑保持不变

## 调试信息

各个启动脚本会显示相应的提示信息：
- 注册中心: "注意: 仅启动注册中心，其他服务不会自动注册"
- 时间服务: "时间服务将自动注册到注册中心"
- 客户端服务: "客户端服务将自动注册到注册中心"

这样可以帮助您了解当前启动的服务状态。