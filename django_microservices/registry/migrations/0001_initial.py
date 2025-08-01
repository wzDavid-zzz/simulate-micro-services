# Generated migration for ServiceRegistry model

from django.db import migrations, models
import django.utils.timezone


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='ServiceRegistry',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('service_name', models.CharField(max_length=100, verbose_name='服务名')),
                ('service_id', models.CharField(max_length=100, unique=True, verbose_name='服务唯一标识')),
                ('ip_address', models.GenericIPAddressField(verbose_name='IP地址')),
                ('port', models.IntegerField(verbose_name='端口')),
                ('del_flag', models.BooleanField(default=False, verbose_name='注销标识')),
                ('create_time', models.DateTimeField(auto_now_add=True, verbose_name='创建时间')),
                ('update_time', models.DateTimeField(auto_now=True, verbose_name='更新时间')),
                ('del_time', models.BigIntegerField(blank=True, null=True, verbose_name='注销时间戳')),
                ('last_heartbeat', models.DateTimeField(default=django.utils.timezone.now, verbose_name='最后心跳时间')),
            ],
            options={
                'verbose_name': '服务注册信息',
                'verbose_name_plural': '服务注册信息',
                'db_table': 't_register',
            },
        ),
        migrations.AddIndex(
            model_name='serviceregistry',
            index=models.Index(fields=['service_name'], name='registry_ser_service_c8e1c5_idx'),
        ),
        migrations.AddIndex(
            model_name='serviceregistry',
            index=models.Index(fields=['service_id'], name='registry_ser_service_fe4d89_idx'),
        ),
        migrations.AddIndex(
            model_name='serviceregistry',
            index=models.Index(fields=['del_flag'], name='registry_ser_del_fla_c5b5b3_idx'),
        ),
    ]