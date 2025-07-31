from rest_framework import serializers
from .models import ServiceRegistry


class RegisterServiceSerializer(serializers.Serializer):
    """服务注册请求序列化器"""
    service_name = serializers.CharField(max_length=100)
    service_id = serializers.CharField(max_length=100)
    ip_address = serializers.IPAddressField()
    port = serializers.IntegerField()


class UnregisterServiceSerializer(serializers.Serializer):
    """服务注销请求序列化器"""
    service_name = serializers.CharField(max_length=100)
    service_id = serializers.CharField(max_length=100, required=False)
    ip_address = serializers.IPAddressField()
    port = serializers.IntegerField()


class HeartBeatServiceSerializer(serializers.Serializer):
    """心跳检测请求序列化器"""
    service_id = serializers.CharField(max_length=100, required=False)
    ip_address = serializers.IPAddressField()
    port = serializers.IntegerField()


class DiscoverServiceSerializer(serializers.ModelSerializer):
    """服务发现响应序列化器"""
    
    class Meta:
        model = ServiceRegistry
        fields = ['service_name', 'service_id', 'ip_address', 'port']


class ServiceRegistrySerializer(serializers.ModelSerializer):
    """服务注册信息序列化器"""
    
    class Meta:
        model = ServiceRegistry
        fields = '__all__'