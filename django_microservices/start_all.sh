#!/bin/bash

# Django微服务集群启动脚本

echo "正在启动Django微服务集群..."

# 检查Python环境
if ! command -v python &> /dev/null; then
    echo "错误: Python未安装或不在PATH中"
    exit 1
fi

# 安装依赖
echo "检查并安装依赖..."
pip install -r requirements.txt

# 运行数据库迁移
echo "执行数据库迁移..."
python manage.py makemigrations
python manage.py migrate

# 创建超级用户（可选，仅在首次运行时需要）
# echo "创建超级用户..."
# python manage.py createsuperuser --noinput --username admin --email admin@example.com || true

# 启动注册中心
echo "启动注册中心服务..."
nohup python start_registry.py > logs/registry.log 2>&1 &
REGISTRY_PID=$!
echo "注册中心启动成功，PID: $REGISTRY_PID"

# 等待注册中心启动
sleep 3

# 启动时间服务实例1
echo "启动时间服务实例1..."
nohup python start_time_service.py 1 > logs/time_service_1.log 2>&1 &
TIME1_PID=$!
echo "时间服务实例1启动成功，PID: $TIME1_PID"

# 启动时间服务实例2
echo "启动时间服务实例2..."
nohup python start_time_service.py 2 > logs/time_service_2.log 2>&1 &
TIME2_PID=$!
echo "时间服务实例2启动成功，PID: $TIME2_PID"

# 等待时间服务启动
sleep 2

# 启动客户端服务实例1
echo "启动客户端服务实例1..."
nohup python start_client_service.py 1 > logs/client_service_1.log 2>&1 &
CLIENT1_PID=$!
echo "客户端服务实例1启动成功，PID: $CLIENT1_PID"

# 启动客户端服务实例2
echo "启动客户端服务实例2..."
nohup python start_client_service.py 2 > logs/client_service_2.log 2>&1 &
CLIENT2_PID=$!
echo "客户端服务实例2启动成功，PID: $CLIENT2_PID"

# 保存PID到文件
mkdir -p pids
echo $REGISTRY_PID > pids/registry.pid
echo $TIME1_PID > pids/time_service_1.pid
echo $TIME2_PID > pids/time_service_2.pid
echo $CLIENT1_PID > pids/client_service_1.pid
echo $CLIENT2_PID > pids/client_service_2.pid

echo ""
echo "所有服务启动完成！"
echo "注册中心:        http://127.0.0.1:8001"
echo "时间服务实例1:    http://127.0.0.1:8002"
echo "时间服务实例2:    http://127.0.0.1:8003"
echo "客户端服务实例1:  http://127.0.0.1:8004"
echo "客户端服务实例2:  http://127.0.0.1:8005"
echo ""
echo "API测试:"
echo "curl http://127.0.0.1:8004/api/getInfo"
echo "curl http://127.0.0.1:8002/api/getDateTime?style=full"
echo "curl http://127.0.0.1:8001/api/discovery"
echo ""
echo "使用 ./stop_all.sh 停止所有服务"