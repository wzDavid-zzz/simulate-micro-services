from rest_framework import serializers


class TimeResponseSerializer(serializers.Serializer):
    """时间响应序列化器"""
    result = serializers.CharField()
    service_id = serializers.CharField()


class TimeRequestSerializer(serializers.Serializer):
    """时间请求序列化器"""
    style = serializers.CharField(default='full')
    
    def validate_style(self, value):
        """验证时间格式参数"""
        allowed_styles = ['date', 'time', 'unix', 'full']
        if value.lower() not in allowed_styles:
            raise serializers.ValidationError(f"style必须是以下值之一: {', '.join(allowed_styles)}")
        return value.lower()