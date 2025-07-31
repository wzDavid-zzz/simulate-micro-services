import logging
from rest_framework.views import APIView
from .services import ClientService
from .serializers import ClientInfoResponseSerializer
from registry.utils import APIResponse

logger = logging.getLogger(__name__)


class ClientInfoView(APIView):
    """客户端信息视图"""
    
    def get(self, request):
        """获取客户端信息"""
        
        try:
            # 创建客户端服务实例
            client_service = ClientService()
            
            # 获取客户端信息
            client_info = client_service.get_client_info()
            
            # 序列化响应数据
            serializer = ClientInfoResponseSerializer(client_info)
            
            # 根据结果返回成功或失败
            if client_info['result']:
                return APIResponse.success(
                    data=serializer.data,
                    message="获取客户端信息成功"
                )
            else:
                return APIResponse.failure(
                    message="获取客户端信息失败",
                    data=serializer.data
                )
                
        except Exception as e:
            logger.error(f"获取客户端信息异常: {str(e)}")
            return APIResponse.error("获取客户端信息失败")