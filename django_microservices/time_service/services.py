import uuid
import logging
from datetime import datetime
from django.utils import timezone

logger = logging.getLogger(__name__)

# 全局服务ID
SERVICE_ID = str(uuid.uuid4())


class TimeService:
    """时间服务业务逻辑"""
    
    @staticmethod
    def get_current_time(style='full'):
        """
        根据指定格式获取当前时间
        :param style: 时间格式 ('date', 'time', 'unix', 'full')
        :return: 格式化的时间字符串
        """
        now = timezone.now()
        
        if style == 'date':
            formatted_time = now.strftime('%Y-%m-%d')
        elif style == 'time':
            formatted_time = now.strftime('%H:%M:%S')
        elif style == 'unix':
            formatted_time = str(int(now.timestamp()))
        else:  # 'full' or default
            formatted_time = now.strftime('%Y-%m-%d %H:%M:%S')
        
        logger.info(f"时间服务被调用: style={style}, result={formatted_time}")
        
        return {
            'result': formatted_time,
            'service_id': SERVICE_ID
        }