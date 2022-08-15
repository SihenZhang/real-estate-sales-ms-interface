package com.sihenzhang.realestate.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sihenzhang.realestate.entity.RolePermission;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RolePermissionMapper extends BaseMapper<RolePermission> {
    @Select("SELECT rp.id,role_id,r.name AS role_name,permission_id,p.name AS permission_name,code AS permission_code " +
            "FROM role_permission AS rp LEFT JOIN role AS r ON rp.role_id=r.id LEFT JOIN permission AS p ON rp.permission_id=p.id " +
            "${ew.customSqlSegment}")
    List<RolePermission> selectRolePermissionVOList(@Param(Constants.WRAPPER) Wrapper<RolePermission> wrapper);
}
