package com.sihenzhang.realestate.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sihenzhang.realestate.entity.Customer;
import com.sihenzhang.realestate.entity.Role;
import com.sihenzhang.realestate.entity.RolePermission;
import com.sihenzhang.realestate.entity.User;
import com.sihenzhang.realestate.result.Result;
import com.sihenzhang.realestate.result.ResultCode;
import com.sihenzhang.realestate.service.PermissionService;
import com.sihenzhang.realestate.service.RolePermissionService;
import com.sihenzhang.realestate.service.RoleService;
import com.sihenzhang.realestate.service.UserService;
import com.sihenzhang.realestate.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private UserService userService;
    private RoleService roleService;
    private PermissionService permissionService;
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
    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Autowired
    public void setRolePermissionService(RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }

    // 除了拥有查询角色权限的角色外，拥有创建用户权限和授予角色权限的角色也可以查询角色列表用以创建用户和授予角色
    @SaCheckPermission(value = {"role:query", "user:create", "user:grant"}, mode = SaMode.OR)
    @GetMapping
    public Result getRoles(@RequestParam(defaultValue = "false") Boolean withoutCustomer, @RequestParam(required = false) Integer current, @RequestParam(defaultValue = "5") Integer size) {
        // 可以获取非客户的其它角色，用于为用户授予角色
        Wrapper<Role> wrapper = withoutCustomer ? Wrappers.<Role>lambdaQuery().ne(Role::getId, Customer.ROLE_ID) : Wrappers.emptyWrapper();
        if (current == null) {
            List<Role> roles = roleService.list(wrapper);
            return Result.buildSuccess("获取角色列表成功", Dict.of("roles", roles));
        }
        Page<Role> page = new Page<>(current, size);
        Page<Role> resultPage = roleService.page(page, wrapper);
        Dict data = Dict.create().set("total", resultPage.getTotal()).set("current", resultPage.getCurrent()).set("roles", resultPage.getRecords());
        return Result.buildSuccess("获取角色列表成功", data);
    }

    @SaCheckPermission("role:query")
    @GetMapping("/{id}")
    public Result getRoleById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role != null) {
            return Result.buildSuccess("获取角色成功", role);
        }
        return Result.build(ResultCode.NOT_FOUND, "获取角色失败，角色不存在");
    }

    @SaCheckPermission("role:create")
    @PostMapping
    public Result createRole(@RequestBody Role role) {
        if (roleService.getOne(Wrappers.<Role>lambdaQuery().eq(Role::getName, role.getName()), false) != null) {
            return Result.buildFail("创建角色失败，角色已存在");
        }
        if (BeanUtils.hasBlankField(role, "name")) {
            return Result.buildFail("创建角色失败，角色名不能为空");
        }
        if (roleService.save(role)) {
            return Result.build(ResultCode.CREATED, "创建角色成功");
        }
        return Result.buildFail("创建角色失败");
    }

    @SaCheckPermission("role:update")
    @PutMapping("/{id}")
    public Result updateRole(@PathVariable Long id, @RequestBody Role role) {
        if (id.equals(Customer.ROLE_ID)) {
            return Result.buildFail("编辑角色失败，该角色不可编辑");
        }
        if (roleService.getById(id) == null) {
            return Result.build(ResultCode.NOT_FOUND, "编辑角色失败，角色不存在");
        }
        if (BeanUtils.hasBlankField(role, "name")) {
            return Result.buildFail("编辑角色失败，角色名不能为空");
        }
        if (roleService.updateById(id, role)) {
            return Result.buildSuccess("编辑角色成功");
        }
        return Result.buildFail("编辑角色失败");
    }

    @SaCheckPermission("role:delete")
    @DeleteMapping("/{id}")
    public Result deleteRole(@PathVariable Long id) {
        if (id.equals(Customer.ROLE_ID)) {
            return Result.buildFail("删除角色失败，该角色不可删除");
        }
        if (roleService.getById(id) == null) {
            return Result.build(ResultCode.NOT_FOUND, "删除角色失败，角色不存在");
        }
        if (roleService.removeById(id)) {
            return Result.buildSuccess("删除角色成功");
        }
        return Result.buildFail("删除角色失败");
    }

    @SaCheckPermission("role:query")
    @GetMapping("/{id}/permissions")
    public Result getPermissionsByRoleId(@PathVariable Long id) {
        if (roleService.getById(id) == null) {
            return Result.build(ResultCode.NOT_FOUND, "获取角色权限失败，角色不存在");
        }
        return Result.buildSuccess("获取角色权限成功", Dict.of("rolePermissions", rolePermissionService.listByRoleId(id)));
    }

    @SaCheckPermission("role:grant")
    @PutMapping("/{id}/permissions")
    public Result grantPermissions(@PathVariable Long id, @RequestBody List<Long> permissionIds) {
        if (roleService.getById(id) == null) {
            return Result.build(ResultCode.NOT_FOUND, "分配角色权限失败，角色不存在");
        }
        rolePermissionService.updateByRoleId(id, permissionIds);
        List<RolePermission> rolePermissions = rolePermissionService.listByRoleId(id);
        Dict data = Dict.create().set("roleId", id).set("rolePermissions", rolePermissions);
        // 如果当前用户为自己的角色分配权限，则更新当前用户的权限
        Long currentUserId = StpUtil.getLoginIdAsLong();
        User currentUser = userService.getById(currentUserId);
        if (currentUser != null && id.equals(currentUser.getRoleId())) {
            List<String> permissions = rolePermissionService.listPermissionCodesByRoleId(id);
            data = data.set("permissions", permissions);
        }
        return Result.buildSuccess("分配角色权限成功", data);
    }

    @SaCheckPermission("role:grant")
    @DeleteMapping("/{rid}/permissions/{pid}")
    public Result deletePermissionByRoleId(@PathVariable Long rid, @PathVariable Long pid) {
        if (roleService.getById(rid) == null) {
            return Result.build(ResultCode.NOT_FOUND, "删除角色权限失败，角色不存在");
        }
        if (permissionService.getById(pid) == null) {
            return Result.build(ResultCode.NOT_FOUND, "删除角色权限失败，权限不存在");
        }
        if (rolePermissionService.removeByRoleId(rid, pid)) {
            List<RolePermission> rolePermissions = rolePermissionService.listByRoleId(rid);
            Dict data = Dict.create().set("roleId", rid).set("rolePermissions", rolePermissions);
            // 如果当前用户为自己的角色删除权限，则更新当前用户的权限
            Long currentUserId = StpUtil.getLoginIdAsLong();
            User currentUser = userService.getById(currentUserId);
            if (currentUser != null && rid.equals(currentUser.getRoleId())) {
                List<String> permissions = rolePermissionService.listPermissionCodesByRoleId(rid);
                data = data.set("permissions", permissions);
            }
            return Result.buildSuccess("删除角色权限成功", data);
        }
        return Result.buildFail("删除角色权限失败");
    }
}
