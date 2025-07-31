import logging
from datetime import datetime
from django.utils import timezone
from rest_framework.views import APIView
from rest_framework.decorators import api_view
from .models import ServiceRegistry
from .serializers import (
    RegisterServiceSerializer, 
    UnregisterServiceSerializer, 
    HeartBeatServiceSerializer,
    DiscoverServiceSerializer
)
from .utils import APIResponse

logger = logging.getLogger(__name__)


class RegisterServiceView(APIView):
    """服务注册视图"""
    
    def post(self, request):
        serializer = RegisterServiceSerializer(data=request.data)
        if not serializer.is_valid():
            return APIResponse.failure("参数错误", data=serializer.errors)
        
        data = serializer.validated_data
        
        try:
            # 检查服务是否已存在
            existing_service = ServiceRegistry.objects.filter(
                service_id=data['service_id']
            ).first()
            
            if existing_service:
                # 更新已存在的服务
                existing_service.service_name = data['service_name']
                existing_service.ip_address = data['ip_address']
                existing_service.port = data['port']
                existing_service.del_flag = False
                existing_service.last_heartbeat = timezone.now()
                existing_service.save()
                logger.info(f"服务更新成功: {data['service_name']}({data['service_id']})")
            else:
                # 创建新服务
                ServiceRegistry.objects.create(
                    service_name=data['service_name'],
                    service_id=data['service_id'],
                    ip_address=data['ip_address'],
                    port=data['port'],
                    last_heartbeat=timezone.now()
                )
                logger.info(f"服务注册成功: {data['service_name']}({data['service_id']})")
            
            return APIResponse.success(message="服务注册成功")
            
        except Exception as e:
            logger.error(f"服务注册失败: {str(e)}")
            return APIResponse.error("服务注册失败")


class UnregisterServiceView(APIView):
    """服务注销视图"""
    
    def post(self, request):
        serializer = UnregisterServiceSerializer(data=request.data)
        if not serializer.is_valid():
            return APIResponse.failure("参数错误", data=serializer.errors)
        
        data = serializer.validated_data
        
        try:
            # 通过IP和端口或者service_id查找服务
            query_filter = {
                'ip_address': data['ip_address'],
                'port': data['port'],
                'del_flag': False
            }
            
            if data.get('service_id'):
                query_filter['service_id'] = data['service_id']
            
            service = ServiceRegistry.objects.filter(**query_filter).first()
            
            if service:
                service.del_flag = True
                service.del_time = int(timezone.now().timestamp() * 1000)
                service.save()
                logger.info(f"服务注销成功: {service.service_name}({service.service_id})")
                return APIResponse.success(message="服务注销成功")
            else:
                return APIResponse.failure("服务不存在")
                
        except Exception as e:
            logger.error(f"服务注销失败: {str(e)}")
            return APIResponse.error("服务注销失败")


class HeartBeatServiceView(APIView):
    """心跳检测视图"""
    
    def post(self, request):
        serializer = HeartBeatServiceSerializer(data=request.data)
        if not serializer.is_valid():
            return APIResponse.failure("参数错误", data=serializer.errors)
        
        data = serializer.validated_data
        
        try:
            # 通过IP和端口查找服务
            services = ServiceRegistry.objects.filter(
                ip_address=data['ip_address'],
                port=data['port'],
                del_flag=False
            )
            
            if services.exists():
                # 更新心跳时间
                services.update(last_heartbeat=timezone.now())
                logger.debug(f"心跳更新成功: {data['ip_address']}:{data['port']}")
                return APIResponse.success(message="心跳检测成功")
            else:
                return APIResponse.failure("服务不存在")
                
        except Exception as e:
            logger.error(f"心跳检测失败: {str(e)}")
            return APIResponse.error("心跳检测失败")


class DiscoveryServiceView(APIView):
    """服务发现视图"""
    
    def get(self, request):
        service_name = request.query_params.get('name')
        
        try:
            # 基础查询条件
            queryset = ServiceRegistry.objects.filter(del_flag=False)
            
            # 过滤健康的服务
            healthy_services = []
            for service in queryset:
                if service.is_healthy():
                    healthy_services.append(service)
            
            # 根据服务名过滤
            if service_name:
                healthy_services = [s for s in healthy_services if s.service_name == service_name]
            
            # 序列化数据
            serializer = DiscoverServiceSerializer(healthy_services, many=True)
            
            logger.info(f"服务发现成功: 找到 {len(healthy_services)} 个健康服务")
            return APIResponse.success(data=serializer.data, message="服务发现成功")
            
        except Exception as e:
            logger.error(f"服务发现失败: {str(e)}")
            return APIResponse.error("服务发现失败")