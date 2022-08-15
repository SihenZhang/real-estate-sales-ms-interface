package com.sihenzhang.realestate.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission {
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long roleId;
    @TableField(exist = false)
    private String roleName;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long permissionId;
    @TableField(exist = false)
    private String permissionName;
    @TableField(exist = false)
    private String permissionCode;
}
