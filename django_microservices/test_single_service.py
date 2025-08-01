#!/usr/bin/env python
"""
测试单独启动服务的脚本
"""
import os
import sys
import subprocess
import time
import signal
import requests

def start_registry_only():
    """仅启动注册中心测试"""
    print("=== 测试单独启动注册中心 ===")
    
    # 启动注册中心
    print("启动注册中心...")
    process = subprocess.Popen([
        'python', 'start_registry.py'
    ], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    
    # 等待服务启动
    time.sleep(3)
    
    try:
        # 测试注册中心是否正常启动
        response = requests.get('http://127.0.0.1:8001/api/discovery', timeout=5)
        if response.status_code == 200:
            data = response.json()
            services = data.get('data', [])
            print(f"✅ 注册中心启动成功")
            print(f"📋 当前注册的服务数量: {len(services)}")
            
            if len(services) == 0:
                print("✅ 完美！没有其他服务自动注册")
                return True
            else:
                print("❌ 发现问题：其他服务自动注册了")
                print("注册的服务:")
                for service in services:
                    print(f"  - {service['service_name']} ({service['ip_address']}:{service['port']})")
                return False
        else:
            print(f"❌ 注册中心响应异常: {response.status_code}")
            return False
            
    except requests.exceptions.RequestException as e:
        print(f"❌ 无法连接到注册中心: {e}")
        return False
    
    finally:
        # 停止注册中心
        print("停止注册中心...")
        process.terminate()
        try:
            process.wait(timeout=5)
        except subprocess.TimeoutExpired:
            process.kill()
            process.wait()

def test_service_registration():
    """测试服务注册流程"""
    print("\n=== 测试服务注册流程 ===")
    
    registry_process = None
    time_process = None
    
    try:
        # 1. 启动注册中心
        print("1. 启动注册中心...")
        registry_process = subprocess.Popen([
            'python', 'start_registry.py'
        ], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        time.sleep(3)
        
        # 检查注册中心状态
        response = requests.get('http://127.0.0.1:8001/api/discovery', timeout=5)
        services_before = response.json().get('data', [])
        print(f"   注册中心启动，当前服务数量: {len(services_before)}")
        
        # 2. 启动时间服务
        print("2. 启动时间服务...")
        time_process = subprocess.Popen([
            'python', 'start_time_service.py', '1'
        ], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        time.sleep(5)  # 等待服务注册
        
        # 3. 检查服务是否正确注册
        response = requests.get('http://127.0.0.1:8001/api/discovery', timeout=5)
        services_after = response.json().get('data', [])
        print(f"   时间服务启动后，当前服务数量: {len(services_after)}")
        
        # 查找时间服务
        time_services = [s for s in services_after if s['service_name'] == 'time-service']
        if time_services:
            print("✅ 时间服务成功注册")
            for service in time_services:
                print(f"   - {service['service_name']} ({service['ip_address']}:{service['port']})")
            return True
        else:
            print("❌ 时间服务未能注册")
            return False
            
    except Exception as e:
        print(f"❌ 测试过程中出现异常: {e}")
        return False
        
    finally:
        # 清理进程
        print("清理测试进程...")
        if time_process:
            time_process.terminate()
            try:
                time_process.wait(timeout=5)
            except subprocess.TimeoutExpired:
                time_process.kill()
                time_process.wait()
        
        if registry_process:
            registry_process.terminate()
            try:
                registry_process.wait(timeout=5)
            except subprocess.TimeoutExpired:
                registry_process.kill()
                registry_process.wait()

if __name__ == '__main__':
    print("Django微服务单独启动测试\n")
    
    # 测试1：单独启动注册中心
    test1_result = start_registry_only()
    
    # 测试2：测试服务注册流程
    test2_result = test_service_registration()
    
    print("\n=== 测试结果汇总 ===")
    print(f"单独启动注册中心: {'✅ 通过' if test1_result else '❌ 失败'}")
    print(f"服务注册流程: {'✅ 通过' if test2_result else '❌ 失败'}")
    
    if test1_result and test2_result:
        print("\n🎉 所有测试通过！问题已解决")
        sys.exit(0)
    else:
        print("\n⚠️  存在问题，请检查配置")
        sys.exit(1)