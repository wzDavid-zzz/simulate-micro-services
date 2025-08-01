import os
from django.apps import AppConfig


class ClientServiceConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'client_service'
    verbose_name = '客户端服务'
    
    def ready(self):
        # 只有当环境变量指示启动客户端服务时才注册
        service_type = os.environ.get('MICROSERVICE_TYPE', '').lower()
        
        # 如果是客户端服务或者是完整启动，则注册服务
        if service_type in ['client_service', 'client', 'all', '']:
            # 检查是否在runserver命令中且端口匹配客户端服务端口
            import sys
            if 'runserver' in sys.argv:
                from decouple import config
                client_port_1 = str(config('CLIENT_PORT', default=8004))
                client_port_2 = str(config('CLIENT_PORT_2', default=8005))
                
                # 检查命令行参数中的端口
                for arg in sys.argv:
                    if ':' in arg and (client_port_1 in arg or client_port_2 in arg):
                        from .tasks import start_service_registration
                        start_service_registration()
                        break