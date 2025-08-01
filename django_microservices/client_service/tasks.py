import threading
import time
import requests
import logging
from django.conf import settings
from .services import SERVICE_ID

logger = logging.getLogger(__name__)

# 服务配置
CLIENT_SERVICE_CONFIG = {
    'service_name': 'client-service',
    'service_id': SERVICE_ID,
    'ip_address': '127.0.0.1',
    'port': getattr(settings, 'CLIENT_SERVICE_PORT', 8004)
}

# 注册中心配置
REGISTRY_CONFIG = settings.SERVICE_INSTANCES['registry']
REGISTRY_URL = f"http://{REGISTRY_CONFIG['host']}:{REGISTRY_CONFIG['port']}/api"


class ServiceManager:
    """服务管理器"""
    
    def __init__(self):
        self.running = False
        self.heartbeat_thread = None
    
    def register_service(self):
        """注册服务到注册中心"""
        try:
            response = requests.post(
                f"{REGISTRY_URL}/register",
                json=CLIENT_SERVICE_CONFIG,
                timeout=5
            )
            if response.status_code == 200:
                logger.info(f"客户端服务注册成功: {CLIENT_SERVICE_CONFIG}")
            else:
                logger.error(f"客户端服务注册失败: {response.text}")
        except Exception as e:
            logger.error(f"客户端服务注册异常: {str(e)}")
    
    def unregister_service(self):
        """从注册中心注销服务"""
        try:
            response = requests.post(
                f"{REGISTRY_URL}/unregister",
                json=CLIENT_SERVICE_CONFIG,
                timeout=5
            )
            if response.status_code == 200:
                logger.info(f"客户端服务注销成功: {CLIENT_SERVICE_CONFIG}")
            else:
                logger.error(f"客户端服务注销失败: {response.text}")
        except Exception as e:
            logger.error(f"客户端服务注销异常: {str(e)}")
    
    def send_heartbeat(self):
        """发送心跳到注册中心"""
        heartbeat_data = {
            'service_id': CLIENT_SERVICE_CONFIG['service_id'],
            'ip_address': CLIENT_SERVICE_CONFIG['ip_address'],
            'port': CLIENT_SERVICE_CONFIG['port']
        }
        
        try:
            response = requests.post(
                f"{REGISTRY_URL}/heartbeat",
                json=heartbeat_data,
                timeout=5
            )
            if response.status_code == 200:
                logger.debug(f"客户端服务心跳发送成功")
            else:
                logger.warning(f"客户端服务心跳发送失败: {response.text}")
        except Exception as e:
            logger.warning(f"客户端服务心跳发送异常: {str(e)}")
    
    def heartbeat_loop(self):
        """心跳循环"""
        while self.running:
            self.send_heartbeat()
            time.sleep(20)  # 每20秒发送一次心跳
    
    def start_heartbeat(self):
        """启动心跳线程"""
        if not self.running:
            self.running = True
            self.heartbeat_thread = threading.Thread(target=self.heartbeat_loop, daemon=True)
            self.heartbeat_thread.start()
            logger.info("客户端服务心跳线程启动成功")
    
    def stop_heartbeat(self):
        """停止心跳线程"""
        self.running = False
        if self.heartbeat_thread:
            self.heartbeat_thread.join(timeout=1)
            logger.info("客户端服务心跳线程停止成功")


# 全局服务管理器实例
service_manager = ServiceManager()


def start_service_registration():
    """启动服务注册和心跳"""
    def delayed_start():
        # 等待Django完全启动
        time.sleep(3)
        service_manager.register_service()
        service_manager.start_heartbeat()
    
    # 在后台线程中启动，避免阻塞Django启动
    threading.Thread(target=delayed_start, daemon=True).start()


def stop_service_registration():
    """停止服务并注销"""
    service_manager.stop_heartbeat()
    service_manager.unregister_service()