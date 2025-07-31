package com.ksyun.registry.dao.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 注册信息实体类
 */

@Data
@TableName("t_register")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistryDO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 服务唯一标识
     */
    private String serviceId;

    /**
     * ip地址
     */
    private String ipAddress;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 注销标识
     */
    private int delFlag;

    /**
     * 创建日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 注销时间戳
     */
    private Long delTime;
}
