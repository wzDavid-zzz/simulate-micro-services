from django.apps import AppConfig


class ClientServiceConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'client_service'
    verbose_name = '客户端服务'
    
    def ready(self):
        # 启动时注册服务和心跳任务
        from .tasks import start_service_registration
        start_service_registration()