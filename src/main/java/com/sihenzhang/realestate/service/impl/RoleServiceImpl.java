package com.sihenzhang.realestate.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sihenzhang.realestate.entity.Role;
import com.sihenzhang.realestate.mapper.RoleMapper;
import com.sihenzhang.realestate.service.RoleService;
import com.sihenzhang.realestate.util.BeanUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Override
    public boolean save(Role role) {
        role = BeanUtils.setBlankStrFieldsToNull(role);
        role = BeanUtil.trimStrFields(role);
        return super.save(role);
    }

    @Override
    @CacheEvict(value = "role", key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @CacheEvict(value = "role", key = "#role.id")
    public boolean updateById(Role role) {
        role = BeanUtils.setBlankStrFieldsToNull(role);
        role = BeanUtil.trimStrFields(role);
        return super.updateById(role);
    }

    @Override
    public boolean updateById(Long id, Role role) {
        role.setId(id);
        return this.currentProxy().updateById(role);
    }

    @Override
    @Cacheable(value = "role", key = "#id", unless = "#result == null")
    public Role getById(Serializable id) {
        return super.getById(id);
    }

    /**
     * 获取当前类的代理对象，由于注解 @Cacheable 基于 AOP 实现，类内部方法不会走代理，所以需要获取代理对象
     */
    private RoleService currentProxy() {
        return (RoleService) AopContext.currentProxy();
    }
}
