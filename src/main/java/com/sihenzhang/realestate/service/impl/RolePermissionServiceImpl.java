package com.sihenzhang.realestate.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sihenzhang.realestate.entity.RolePermission;
import com.sihenzhang.realestate.mapper.RolePermissionMapper;
import com.sihenzhang.realestate.service.RolePermissionService;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {
    @Override
    public List<RolePermission> list(Wrapper<RolePermission> wrapper) {
        return this.getBaseMapper().selectRolePermissionVOList(wrapper);
    }

    @Override
    public List<RolePermission> list() {
        return this.list(Wrappers.emptyWrapper());
    }

    @Override
    @CacheEvict(value = "permissionsByRole", key = "#roleId")
    public boolean removeByRoleId(Long roleId, Long permissionId) {
        return this.remove(Wrappers.<RolePermission>lambdaQuery().eq(RolePermission::getRoleId, roleId).eq(RolePermission::getPermissionId, permissionId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "permissionsByRole", key = "#roleId")
    public int updateByRoleId(Long roleId, List<Long> permissionIds) {
        this.remove(Wrappers.<RolePermission>lambdaQuery().eq(RolePermission::getRoleId, roleId));
        if (permissionIds == null || permissionIds.isEmpty()) {
            return 0;
        }
        // 使用 JDBC 批量操作来提高性能
        List<RolePermission> newPermissions = permissionIds.stream().map(permissionId -> {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            return rolePermission;
        }).collect(Collectors.toList());
        this.saveBatch(newPermissions);
        return permissionIds.size();
    }

    @Override
    @Cacheable(value = "permissionsByRole", key = "#roleId")
    public List<RolePermission> listByRoleId(Long roleId) {
        return this.list(Wrappers.<RolePermission>lambdaQuery().eq(RolePermission::getRoleId, roleId));
    }

    @Override
    public List<String> listPermissionCodesByRoleId(Long roleId) {
        return this.currentProxy().listByRoleId(roleId).stream().map(RolePermission::getPermissionCode).collect(Collectors.toList());
    }

    /**
     * 获取当前类的代理对象，由于注解 @Cacheable 基于 AOP 实现，类内部方法不会走代理，所以需要获取代理对象
     */
    private RolePermissionService currentProxy() {
        return (RolePermissionService) AopContext.currentProxy();
    }
}
