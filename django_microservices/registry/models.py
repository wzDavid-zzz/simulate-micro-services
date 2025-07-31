from django.db import models
from django.utils import timezone


class ServiceRegistry(models.Model):
    """服务注册信息模型"""
    
    service_name = models.CharField(max_length=100, verbose_name='服务名')
    service_id = models.CharField(max_length=100, unique=True, verbose_name='服务唯一标识')
    ip_address = models.GenericIPAddressField(verbose_name='IP地址')
    port = models.IntegerField(verbose_name='端口')
    del_flag = models.BooleanField(default=False, verbose_name='注销标识')
    create_time = models.DateTimeField(auto_now_add=True, verbose_name='创建时间')
    update_time = models.DateTimeField(auto_now=True, verbose_name='更新时间')
    del_time = models.BigIntegerField(null=True, blank=True, verbose_name='注销时间戳')
    last_heartbeat = models.DateTimeField(default=timezone.now, verbose_name='最后心跳时间')

    class Meta:
        db_table = 't_register'
        verbose_name = '服务注册信息'
        verbose_name_plural = '服务注册信息'
        indexes = [
            models.Index(fields=['service_name']),
            models.Index(fields=['service_id']),
            models.Index(fields=['del_flag']),
        ]

    def __str__(self):
        return f"{self.service_name}({self.service_id})"

    def is_healthy(self, timeout_seconds=60):
        """检查服务是否健康（基于心跳超时）"""
        if self.del_flag:
            return False
        
        time_diff = timezone.now() - self.last_heartbeat
        return time_diff.total_seconds() < timeout_seconds

    def get_url(self):
        """获取服务访问URL"""
        return f"http://{self.ip_address}:{self.port}"