from django.urls import path
from .views import ClientInfoView

urlpatterns = [
    path('getInfo', ClientInfoView.as_view(), name='get_client_info'),
]