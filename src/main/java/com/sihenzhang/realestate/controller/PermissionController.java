package com.sihenzhang.realestate.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sihenzhang.realestate.entity.Permission;
import com.sihenzhang.realestate.result.Result;
import com.sihenzhang.realestate.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissionController {
    private PermissionService permissionService;

    @Autowired
    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    // 除了拥有查询权限权限的角色外，拥有分配权限权限的角色也可以查询权限列表用以分配权限
    @SaCheckPermission(value = {"permission:query", "role:grant"}, mode = SaMode.OR)
    @GetMapping
    public Result getPermissions(@RequestParam(required = false) Integer current, @RequestParam(defaultValue = "5") Integer size) {
        if (current == null) {
            List<Permission> permissions = permissionService.list();
            return Result.buildSuccess("获取权限列表成功", Dict.of("permissions", permissions));
        }
        Page<Permission> page = new Page<>(current, size);
        Page<Permission> resultPage = permissionService.page(page);
        Dict data = Dict.create().set("total", resultPage.getTotal()).set("current", resultPage.getCurrent()).set("permissions", resultPage.getRecords());
        return Result.buildSuccess("获取权限列表成功", data);
    }
}
