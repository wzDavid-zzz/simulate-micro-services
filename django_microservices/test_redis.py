#!/usr/bin/env python
"""
Redis连接测试脚本
"""
import os
import sys
import django
from decouple import config

# 设置Django环境
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'microservices.settings')
django.setup()

from django.core.cache import cache

def test_redis_connection():
    """测试Redis连接"""
    
    print("=== Redis连接测试 ===")
    
    # 读取配置
    redis_host = config('REDIS_HOST', default='localhost')
    redis_port = config('REDIS_PORT', default=6379, cast=int)
    redis_password = config('REDIS_PASSWORD', default='')
    redis_db = config('REDIS_DB', default=0, cast=int)
    
    print(f"Redis配置:")
    print(f"  主机: {redis_host}")
    print(f"  端口: {redis_port}")
    print(f"  密码: {'已设置' if redis_password else '未设置'}")
    print(f"  数据库: {redis_db}")
    print()
    
    try:
        # 使用Django缓存框架测试连接
        print("测试Redis连接...")
        
        # 设置测试值
        test_key = 'django_microservices_test'
        test_value = 'Redis连接测试成功!'
        
        cache.set(test_key, test_value, 10)  # 设置10秒过期
        
        # 读取测试值
        retrieved_value = cache.get(test_key)
        
        if retrieved_value == test_value:
            print("✅ Redis连接测试成功!")
            print(f"   测试数据写入和读取正常: {retrieved_value}")
            
            # 清理测试数据
            cache.delete(test_key)
            print("✅ 测试数据清理完成")
            
        else:
            print("❌ Redis连接测试失败: 数据不一致")
            print(f"   期望值: {test_value}")
            print(f"   实际值: {retrieved_value}")
            
    except Exception as e:
        print("❌ Redis连接测试失败!")
        print(f"   错误信息: {e}")
        print()
        print("可能的解决方案:")
        print("1. 检查Redis服务是否正在运行:")
        print("   ps aux | grep redis")
        print("   systemctl status redis")
        print()
        print("2. 检查Redis连接配置:")
        print("   redis-cli -h localhost -p 6379 ping")
        print()
        print("3. 检查.env文件中的Redis配置:")
        print("   REDIS_HOST=localhost")
        print("   REDIS_PORT=6379")
        print("   REDIS_PASSWORD=")
        print("   REDIS_DB=0")
        print()
        print("4. 如果Redis有密码，请设置REDIS_PASSWORD")
        print()
        return False
    
    return True

def test_direct_redis_connection():
    """直接测试Redis连接（不通过Django）"""
    
    try:
        import redis
    except ImportError:
        print("❌ redis包未安装，请运行: pip install redis")
        return False
    
    print("\n=== 直接Redis连接测试 ===")
    
    # 读取配置
    redis_host = config('REDIS_HOST', default='localhost')
    redis_port = config('REDIS_PORT', default=6379, cast=int)
    redis_password = config('REDIS_PASSWORD', default='')
    redis_db = config('REDIS_DB', default=0, cast=int)
    
    try:
        # 创建Redis连接
        if redis_password:
            r = redis.Redis(host=redis_host, port=redis_port, password=redis_password, db=redis_db)
        else:
            r = redis.Redis(host=redis_host, port=redis_port, db=redis_db)
        
        # 测试ping
        response = r.ping()
        if response:
            print("✅ 直接Redis连接测试成功!")
            
            # 测试基本操作
            r.set('test_key', 'test_value')
            value = r.get('test_key')
            if value and value.decode() == 'test_value':
                print("✅ Redis读写操作正常")
                r.delete('test_key')
            else:
                print("❌ Redis读写操作异常")
            
            return True
        else:
            print("❌ Redis ping测试失败")
            return False
            
    except redis.AuthenticationError:
        print("❌ Redis认证失败，请检查密码配置")
        return False
    except redis.ConnectionError as e:
        print(f"❌ Redis连接失败: {e}")
        return False
    except Exception as e:
        print(f"❌ Redis测试出现异常: {e}")
        return False

if __name__ == '__main__':
    print("Django微服务 Redis连接测试\n")
    
    # 测试Django缓存连接
    django_test = test_redis_connection()
    
    # 测试直接Redis连接
    direct_test = test_direct_redis_connection()
    
    print("\n=== 测试结果汇总 ===")
    print(f"Django缓存连接: {'✅ 成功' if django_test else '❌ 失败'}")
    print(f"直接Redis连接: {'✅ 成功' if direct_test else '❌ 失败'}")
    
    if django_test and direct_test:
        print("\n🎉 所有Redis连接测试通过，您可以正常启动微服务！")
        sys.exit(0)
    else:
        print("\n⚠️  Redis连接存在问题，请查看上述错误信息并修复后重试")
        sys.exit(1)