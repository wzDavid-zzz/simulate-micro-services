#!/bin/bash

# Django微服务集群停止脚本

echo "正在停止Django微服务集群..."

# 从PID文件中读取进程ID并终止
if [ -d "pids" ]; then
    for pid_file in pids/*.pid; do
        if [ -f "$pid_file" ]; then
            PID=$(cat "$pid_file")
            SERVICE_NAME=$(basename "$pid_file" .pid)
            
            if ps -p $PID > /dev/null 2>&1; then
                echo "停止服务: $SERVICE_NAME (PID: $PID)"
                kill $PID
                
                # 等待进程结束
                for i in {1..10}; do
                    if ! ps -p $PID > /dev/null 2>&1; then
                        break
                    fi
                    sleep 1
                done
                
                # 如果进程仍然存在，强制终止
                if ps -p $PID > /dev/null 2>&1; then
                    echo "强制终止服务: $SERVICE_NAME (PID: $PID)"
                    kill -9 $PID
                fi
            else
                echo "服务 $SERVICE_NAME 未运行"
            fi
            
            rm "$pid_file"
        fi
    done
    
    rmdir pids 2>/dev/null
else
    echo "未找到PID文件，尝试通过端口终止进程..."
    
    # 通过端口查找并终止进程
    for port in 8001 8002 8003 8004 8005; do
        PID=$(lsof -ti :$port 2>/dev/null)
        if [ -n "$PID" ]; then
            echo "终止端口 $port 上的进程 (PID: $PID)"
            kill $PID
            sleep 1
            
            # 检查进程是否仍在运行
            if ps -p $PID > /dev/null 2>&1; then
                echo "强制终止端口 $port 上的进程 (PID: $PID)"
                kill -9 $PID
            fi
        fi
    done
fi

echo "所有微服务已停止"