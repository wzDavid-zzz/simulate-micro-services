from rest_framework import serializers


class ClientInfoResponseSerializer(serializers.Serializer):
    """客户端信息响应序列化器"""
    result = serializers.CharField(allow_null=True)
    error = serializers.CharField(allow_null=True)


class ServiceDiscoverySerializer(serializers.Serializer):
    """服务发现响应序列化器"""
    service_name = serializers.CharField()
    service_id = serializers.CharField()
    ip_address = serializers.IPAddressField()
    port = serializers.IntegerField()