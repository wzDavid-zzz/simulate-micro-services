from rest_framework.response import Response
from rest_framework import status


class APIResponse:
    """统一API响应格式"""
    
    @staticmethod
    def success(data=None, message="成功"):
        """成功响应"""
        response_data = {
            "code": 0,
            "message": message,
            "data": data
        }
        return Response(response_data, status=status.HTTP_200_OK)
    
    @staticmethod
    def failure(message="失败", code=1, data=None):
        """失败响应"""
        response_data = {
            "code": code,
            "message": message,
            "data": data
        }
        return Response(response_data, status=status.HTTP_400_BAD_REQUEST)
    
    @staticmethod
    def error(message="服务器错误", code=500):
        """错误响应"""
        response_data = {
            "code": code,
            "message": message,
            "data": None
        }
        return Response(response_data, status=status.HTTP_500_INTERNAL_SERVER_ERROR)