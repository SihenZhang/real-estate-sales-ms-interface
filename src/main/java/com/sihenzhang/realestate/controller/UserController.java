package com.sihenzhang.realestate.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sihenzhang.realestate.entity.User;
import com.sihenzhang.realestate.result.Result;
import com.sihenzhang.realestate.result.ResultCode;
import com.sihenzhang.realestate.service.RolePermissionService;
import com.sihenzhang.realestate.service.RoleService;
import com.sihenzhang.realestate.service.UserService;
import com.sihenzhang.realestate.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
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

    @SaCheckPermission("user:query")
    @GetMapping
    public Result getUsers(@RequestParam(required = false) Integer current, @RequestParam(defaultValue = "5") Integer size) {
        if (current == null) {
            List<User> users = userService.list();
            return Result.buildSuccess("获取用户列表成功", Dict.of("users", users));
        }
        Page<User> page = new Page<>(current, size);
        Page<User> resultPage = userService.page(page);
        Dict data = Dict.create().set("total", resultPage.getTotal()).set("current", resultPage.getCurrent()).set("users", resultPage.getRecords());
        return Result.buildSuccess("获取用户列表成功", data);
    }

    @SaCheckPermission("user:query")
    @GetMapping("/{id}")
    public Result getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            return Result.buildSuccess("获取用户成功", user);
        }
        return Result.build(ResultCode.NOT_FOUND, "获取用户失败，用户不存在");
    }

    @SaCheckPermission("user:create")
    @PostMapping
    public Result createUser(@RequestBody User user) {
        if (userService.getByUsername(user.getUsername()) != null) {
            return Result.buildFail("创建用户失败，用户名已存在");
        }
        if (roleService.getById(user.getRoleId()) == null) {
            return Result.build(ResultCode.NOT_FOUND, "创建用户失败，角色不存在");
        }
        if (BeanUtils.hasBlankField(user, "username", "password", "realName")) {
            return Result.buildFail("创建用户失败，用户名、密码和真实姓名不能为空");
        }
        if (userService.save(user)) {
            return Result.build(ResultCode.CREATED, "创建用户成功");
        }
        return Result.buildFail("创建用户失败");
    }

    @SaCheckPermission("user:update")
    @PutMapping("/{id}")
    public Result updateUser(@PathVariable Long id, @RequestBody User user) {
        if (userService.getById(id) == null) {
            return Result.build(ResultCode.NOT_FOUND, "编辑用户失败，用户不存在");
        }
        if (BeanUtils.hasBlankField(user, "username", "realName")) {
            return Result.buildFail("编辑用户失败，用户名和真实姓名不能为空");
        }
        if (userService.updateById(id, user)) {
            // 如果当前用户修改自己的信息，则更新当前用户的信息
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (id.equals(currentUserId)) {
                Dict data = Dict.create().set("username", user.getUsername()).set("realName", user.getRealName());
                return Result.buildSuccess("编辑用户成功", data);
            }
            return Result.buildSuccess("编辑用户成功");
        }
        return Result.buildFail("编辑用户失败");
    }

    @SaCheckPermission("user:delete")
    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable Long id) {
        if (userService.getById(id) == null) {
            return Result.build(ResultCode.NOT_FOUND, "删除用户失败，用户不存在");
        }
        if (userService.removeById(id)) {
            // 如果当前用户删除自己的信息，则退出登录
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (id.equals(currentUserId)) {
                StpUtil.logout();
            }
            return Result.buildSuccess("删除用户成功");
        }
        return Result.buildFail("删除用户失败");
    }

    @SaCheckPermission("user:grant")
    @PutMapping("/{id}/role")
    public Result grantRole(@PathVariable Long id, @RequestBody Long roleId) {
        if (userService.getById(id) == null) {
            return Result.build(ResultCode.NOT_FOUND, "授予角色失败，用户不存在");
        }
        if (roleService.getById(roleId) == null) {
            return Result.build(ResultCode.NOT_FOUND, "授予角色失败，角色不存在");
        }
        if (userService.grantRole(id, roleId)) {
            // 如果当前用户授予自己其它角色，则更新当前用户的权限
            Long currentUserId = StpUtil.getLoginIdAsLong();
            if (id.equals(currentUserId)) {
                List<String> permissions = rolePermissionService.listPermissionCodesByRoleId(roleId);
                Dict data = Dict.create().set("roleId", roleId).set("permissions", permissions);
                return Result.buildSuccess("授予角色成功", data);
            }
            return Result.buildSuccess("授予角色成功");
        }
        return Result.buildFail("授予角色失败");
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        User loginUser = userService.login(user);
        if (loginUser != null) {
            List<String> permissions = rolePermissionService.listPermissionCodesByRoleId(loginUser.getRoleId());
            if (!permissions.contains("user:login")) {
                throw new NotPermissionException("user:login");
            }
            StpUtil.login(loginUser.getId());
            Dict data = Dict.create()
                    .set("id", loginUser.getId())
                    .set("username", loginUser.getUsername())
                    .set("realName", loginUser.getRealName())
                    .set("roleId", loginUser.getRoleId())
                    .set("permissions", permissions);
            return Result.buildSuccess("登录成功", data);
        }
        return Result.buildFail("登录失败，用户名或密码错误");
    }

    @GetMapping("/current")
    public Result getCurrentUser() {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        User currentUser = userService.getById(currentUserId);
        if (currentUser != null) {
            List<String> permissions = rolePermissionService.listPermissionCodesByRoleId(currentUser.getRoleId());
            Dict data = Dict.create()
                    .set("id", currentUser.getId())
                    .set("username", currentUser.getUsername())
                    .set("realName", currentUser.getRealName())
                    .set("roleId", currentUser.getRoleId())
                    .set("permissions", permissions);
            return Result.buildSuccess("获取当前用户信息成功", data);
        }
        // 当前用户信息不存在，应该退出登录
        StpUtil.logout();
        return Result.build(ResultCode.NOT_FOUND, "获取当前用户信息失败，当前用户信息不存在");
    }

    @GetMapping("/logout")
    public Result logout() {
        StpUtil.logout();
        return Result.buildSuccess("退出登录成功");
    }
}
