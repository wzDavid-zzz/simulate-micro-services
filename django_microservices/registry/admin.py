from django.contrib import admin
from .models import ServiceRegistry


@admin.register(ServiceRegistry)
class ServiceRegistryAdmin(admin.ModelAdmin):
    list_display = ['service_name', 'service_id', 'ip_address', 'port', 'del_flag', 'last_heartbeat', 'create_time']
    list_filter = ['service_name', 'del_flag', 'create_time']
    search_fields = ['service_name', 'service_id', 'ip_address']
    readonly_fields = ['create_time', 'update_time']
    
    def get_queryset(self, request):
        qs = super().get_queryset(request)
        return qs.order_by('-create_time')