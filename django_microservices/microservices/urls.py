"""microservices URL Configuration"""
from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('admin/', admin.site.urls),
    path('api/', include('registry.urls')),
    path('api/', include('time_service.urls')),
    path('api/', include('client_service.urls')),
]