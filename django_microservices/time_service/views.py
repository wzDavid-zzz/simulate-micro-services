import logging
from rest_framework.views import APIView
from .services import TimeService
from .serializers import TimeRequestSerializer, TimeResponseSerializer
from registry.utils import APIResponse

logger = logging.getLogger(__name__)


class TimeServiceView(APIView):
    """时间服务视图"""
    
    def get(self, request):
        """获取当前时间"""
        
        # 获取请求参数
        style = request.query_params.get('style', 'full')
        
        # 验证参数
        serializer = TimeRequestSerializer(data={'style': style})
        if not serializer.is_valid():
            return APIResponse.failure("参数错误", data=serializer.errors)
        
        try:
            # 调用业务服务
            result = TimeService.get_current_time(serializer.validated_data['style'])
            
            # 序列化响应数据
            response_serializer = TimeResponseSerializer(result)
            
            return APIResponse.success(
                data=response_serializer.data,
                message="获取时间成功"
            )
            
        except Exception as e:
            logger.error(f"获取时间失败: {str(e)}")
            return APIResponse.error("获取时间失败")