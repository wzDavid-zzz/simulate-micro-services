from django.urls import path
from .views import (
    RegisterServiceView,
    UnregisterServiceView,
    HeartBeatServiceView,
    DiscoveryServiceView
)

urlpatterns = [
    path('register', RegisterServiceView.as_view(), name='register_service'),
    path('unregister', UnregisterServiceView.as_view(), name='unregister_service'),
    path('heartbeat', HeartBeatServiceView.as_view(), name='heartbeat_service'),
    path('discovery', DiscoveryServiceView.as_view(), name='discovery_service'),
]