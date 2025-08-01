import uuid
import requests
import logging
from django.conf import settings
from .serializers import ServiceDiscoverySerializer

logger = logging.getLogger(__name__)

# 全局服务ID
SERVICE_ID = str(uuid.uuid4())

# 注册中心配置
REGISTRY_CONFIG = settings.SERVICE_INSTANCES['registry']
REGISTRY_URL = f"http://{REGISTRY_CONFIG['host']}:{REGISTRY_CONFIG['port']}/api"


class ClientService:
    """客户端服务业务逻辑"""
    
    def __init__(self):
        self.service_id = SERVICE_ID
    
    def discover_services(self, service_name=None):
        """
        服务发现
        :param service_name: 服务名称，为空则返回所有服务
        :return: 服务列表
        """
        try:
            params = {}
            if service_name:
                params['name'] = service_name
            
            response = requests.get(
                f"{REGISTRY_URL}/discovery",
                params=params,
                timeout=5
            )
            
            if response.status_code == 200:
                data = response.json()
                if data.get('code') == 0:
                    services = data.get('data', [])
                    logger.info(f"服务发现成功: 找到 {len(services)} 个服务")
                    return services
                else:
                    logger.error(f"服务发现失败: {data.get('message')}")
                    return []
            else:
                logger.error(f"服务发现请求失败: {response.status_code}")
                return []
                
        except Exception as e:
            logger.error(f"服务发现异常: {str(e)}")
            return []
    
    def call_time_service(self, style='full'):
        """
        调用时间服务
        :param style: 时间格式
        :return: 时间服务响应
        """
        try:
            # 发现时间服务
            time_services = self.discover_services('time-service')
            
            if not time_services:
                return None, "时间服务不可用"
            
            # 选择第一个可用的时间服务
            service = time_services[0]
            service_url = f"http://{service['ip_address']}:{service['port']}/api/getDateTime"
            
            logger.info(f"本次调用的IP是: http://{service['ip_address']}:{service['port']}")
            
            # 调用时间服务
            response = requests.get(
                service_url,
                params={'style': style},
                timeout=5
            )
            
            if response.status_code == 200:
                data = response.json()
                if data.get('code') == 0:
                    time_data = data.get('data')
                    result = f"Hello Kingsoft Cloud Star Camp - {self.service_id} - {time_data.get('result')}"
                    return result, None
                else:
                    return None, f"时间服务返回错误: {data.get('message')}"
            else:
                return None, f"时间服务调用失败: {response.status_code}"
                
        except Exception as e:
            logger.error(f"调用时间服务异常: {str(e)}")
            return None, f"调用时间服务异常: {str(e)}"
    
    def get_client_info(self):
        """
        获取客户端信息（包含时间服务调用结果）
        :return: 客户端信息
        """
        result, error = self.call_time_service('full')
        
        return {
            'result': result,
            'error': error
        }