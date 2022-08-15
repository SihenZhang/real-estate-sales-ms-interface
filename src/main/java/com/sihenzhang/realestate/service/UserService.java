package com.sihenzhang.realestate.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sihenzhang.realestate.entity.User;

import java.util.List;

public interface UserService extends IService<User> {
    boolean updateById(Long id, User user);

    User getOne(QueryWrapper<User> wrapper);

    User getOne(LambdaQueryWrapper<User> wrapper);

    List<User> list(QueryWrapper<User> wrapper);

    List<User> list(LambdaQueryWrapper<User> wrapper);

    <P extends IPage<User>> P page(P page, QueryWrapper<User> wrapper);

    <P extends IPage<User>> P page(P page, LambdaQueryWrapper<User> wrapper);

    User getByUsername(String username);

    User login(User user);

    boolean grantRole(Long id, Long roleId);
}
