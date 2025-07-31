from django.urls import path
from .views import TimeServiceView

urlpatterns = [
    path('getDateTime', TimeServiceView.as_view(), name='get_datetime'),
]