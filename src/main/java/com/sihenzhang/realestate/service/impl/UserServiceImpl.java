package com.sihenzhang.realestate.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sihenzhang.realestate.entity.Customer;
import com.sihenzhang.realestate.entity.User;
import com.sihenzhang.realestate.mapper.UserMapper;
import com.sihenzhang.realestate.service.UserService;
import com.sihenzhang.realestate.util.BeanUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public boolean save(User user) {
        user = BeanUtils.setBlankStrFieldsToNull(user);
        user = BeanUtil.trimStrFields(user);
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        return super.save(user);
    }

    @Override
    @CacheEvict(value = "user", key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @CacheEvict(value = "user", key = "#user.id")
    public boolean updateById(User user) {
        user = BeanUtils.setBlankStrFieldsToNull(user);
        user = BeanUtil.trimStrFields(user);
        return super.updateById(user);
    }

    @Override
    public boolean updateById(Long id, User user) {
        user.setId(id);
        return this.currentProxy().updateById(user);
    }

    @Override
    @Cacheable(value = "user", key = "#id", unless = "#result == null")
    public User getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public User getOne(QueryWrapper<User> wrapper) {
        return this.getBaseMapper().selectUserVO(wrapper.ne("role_id", Customer.ROLE_ID));
    }

    @Override
    public User getOne(LambdaQueryWrapper<User> wrapper) {
        return this.getBaseMapper().selectUserVO(wrapper.ne(User::getRoleId, Customer.ROLE_ID));
    }

    @Override
    public List<User> list(QueryWrapper<User> wrapper) {
        return this.getBaseMapper().selectUserVOList(wrapper.ne("role_id", Customer.ROLE_ID));
    }

    @Override
    public List<User> list(LambdaQueryWrapper<User> wrapper) {
        return this.getBaseMapper().selectUserVOList(wrapper.ne(User::getRoleId, Customer.ROLE_ID));
    }

    @Override
    public List<User> list() {
        return this.list(Wrappers.query());
    }

    @Override
    public <P extends IPage<User>> P page(P page, QueryWrapper<User> wrapper) {
        return this.getBaseMapper().selectUserVOPage(page, wrapper.ne("role_id", Customer.ROLE_ID));
    }

    @Override
    public <P extends IPage<User>> P page(P page, LambdaQueryWrapper<User> wrapper) {
        return this.getBaseMapper().selectUserVOPage(page, wrapper.ne(User::getRoleId, Customer.ROLE_ID));
    }

    @Override
    public <P extends IPage<User>> P page(P page) {
        return this.page(page, Wrappers.query());
    }

    @Override
    public User getByUsername(String username) {
        return this.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
    }

    @Override
    public User login(User user) {
        User userInDb = this.getByUsername(user.getUsername());
        if (userInDb != null) {
            if (BCrypt.checkpw(user.getPassword(), userInDb.getPassword())) {
                return userInDb;
            }
        }
        return null;
    }

    @Override
    public boolean grantRole(Long id, Long roleId) {
        User user = new User();
        user.setId(id);
        user.setRoleId(roleId);
        return this.currentProxy().updateById(user);
    }

    /**
     * 获取当前类的代理对象，由于注解 @Cacheable 基于 AOP 实现，类内部方法不会走代理，所以需要获取代理对象
     */
    private UserService currentProxy() {
        return (UserService) AopContext.currentProxy();
    }
}
