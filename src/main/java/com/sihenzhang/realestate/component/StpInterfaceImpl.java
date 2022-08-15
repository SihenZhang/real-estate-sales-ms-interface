package com.sihenzhang.realestate.component;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Opt;
import com.sihenzhang.realestate.entity.Role;
import com.sihenzhang.realestate.entity.User;
import com.sihenzhang.realestate.service.RolePermissionService;
import com.sihenzhang.realestate.service.RoleService;
import com.sihenzhang.realestate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface {
    private UserService userService;
    private RoleService roleService;
    private RolePermissionService rolePermissionService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setRolePermissionService(RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return Opt.ofNullable(Convert.toLong(loginId))
                .map(userService::getById)
                .map(User::getRoleId)
                .map(rolePermissionService::listPermissionCodesByRoleId)
                .orElseGet(List::of);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return Opt.ofNullable(Convert.toLong(loginId))
                .map(userService::getById)
                .map(User::getRoleId)
                .map(roleService::getById)
                .map(Role::getName)
                .map(List::of)
                .orElseGet(List::of);
    }
}
