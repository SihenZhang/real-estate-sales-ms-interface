package com.sihenzhang.realestate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sihenzhang.realestate.entity.RolePermission;

import java.util.List;

public interface RolePermissionService extends IService<RolePermission> {
    int updateByRoleId(Long roleId, List<Long> permissionIds);

    boolean removeByRoleId(Long roleId, Long permissionId);

    List<RolePermission> listByRoleId(Long roleId);

    List<String> listPermissionCodesByRoleId(Long roleId);
}
