#!/usr/bin/env python
"""
时间服务启动脚本
"""
import os
import sys
import django
from django.core.management import execute_from_command_line
from decouple import config

if __name__ == '__main__':
    # 设置环境变量
    os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'microservices.settings')
    
    # 设置服务类型为时间服务
    os.environ['MICROSERVICE_TYPE'] = 'time_service'
    
    # 获取端口参数
    instance = sys.argv[1] if len(sys.argv) > 1 else '1'
    
    if instance == '1':
        port = config('TIME_PORT', default=8002, cast=int)
    elif instance == '2':
        port = config('TIME_PORT_2', default=8003, cast=int)
    else:
        print("无效的实例编号，请使用 1 或 2")
        sys.exit(1)
    
    print(f"正在启动时间服务实例 {instance}，端口: {port}")
    print("时间服务将自动注册到注册中心")
    
    # 启动Django开发服务器
    sys.argv = ['manage.py', 'runserver', f'127.0.0.1:{port}']
    execute_from_command_line(sys.argv)