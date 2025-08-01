#!/usr/bin/env python
"""
注册中心服务启动脚本
"""
import os
import sys
import django
from django.core.management import execute_from_command_line
from decouple import config

if __name__ == '__main__':
    # 设置环境变量
    os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'microservices.settings')
    
    # 设置服务类型为注册中心，防止其他服务自动注册
    os.environ['MICROSERVICE_TYPE'] = 'registry'
    
    # 设置注册中心端口
    registry_port = config('REGISTRY_PORT', default=8001, cast=int)
    
    print(f"正在启动注册中心服务，端口: {registry_port}")
    print("注意: 仅启动注册中心，其他服务不会自动注册")
    
    # 启动Django开发服务器
    sys.argv = ['manage.py', 'runserver', f'127.0.0.1:{registry_port}']
    execute_from_command_line(sys.argv)