import os
from django.apps import AppConfig


class TimeServiceConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'time_service'
    verbose_name = '时间服务'
    
    def ready(self):
        # 只有当环境变量指示启动时间服务时才注册
        service_type = os.environ.get('MICROSERVICE_TYPE', '').lower()
        
        # 如果是时间服务或者是完整启动，则注册服务
        if service_type in ['time_service', 'time', 'all', '']:
            # 检查是否在runserver命令中且端口匹配时间服务端口
            import sys
            if 'runserver' in sys.argv:
                from decouple import config
                time_port_1 = str(config('TIME_PORT', default=8002))
                time_port_2 = str(config('TIME_PORT_2', default=8003))
                
                # 检查命令行参数中的端口
                for arg in sys.argv:
                    if ':' in arg and (time_port_1 in arg or time_port_2 in arg):
                        from .tasks import start_service_registration
                        start_service_registration()
                        break