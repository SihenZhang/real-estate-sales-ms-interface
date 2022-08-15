package com.sihenzhang.realestate.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sihenzhang.realestate.entity.Customer;
import com.sihenzhang.realestate.mapper.CustomerMapper;
import com.sihenzhang.realestate.service.CustomerService;
import com.sihenzhang.realestate.util.BeanUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {
    @Override
    public boolean save(Customer customer) {
        customer = BeanUtils.setBlankStrFieldsToNull(customer);
        customer = BeanUtil.trimStrFields(customer);
        customer.setPassword(BCrypt.hashpw(customer.getPassword()));
        customer.setRoleId(Customer.ROLE_ID);
        return super.save(customer);
    }

    @Override
    @CacheEvict(value = "customer", key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @CacheEvict(value = "customer", key = "#customer.id")
    public boolean updateById(Customer customer) {
        customer = BeanUtils.setBlankStrFieldsToNull(customer);
        customer = BeanUtil.trimStrFields(customer);
        return super.updateById(customer);
    }

    @Override
    public boolean updateById(Long id, Customer customer) {
        customer.setId(id);
        return this.currentProxy().updateById(customer);
    }

    @Override
    @Cacheable(value = "customer", key = "#id")
    public Customer getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public Customer getOne(QueryWrapper<Customer> wrapper) {
        return super.getOne(wrapper.eq("role_id", Customer.ROLE_ID), false);
    }

    @Override
    public Customer getOne(LambdaQueryWrapper<Customer> wrapper) {
        return super.getOne(wrapper.eq(Customer::getRoleId, Customer.ROLE_ID), false);
    }

    @Override
    public List<Customer> list(QueryWrapper<Customer> wrapper) {
        return super.list(wrapper.eq("role_id", Customer.ROLE_ID));
    }

    @Override
    public List<Customer> list(LambdaQueryWrapper<Customer> wrapper) {
        return super.list(wrapper.eq(Customer::getRoleId, Customer.ROLE_ID));
    }

    @Override
    public List<Customer> list() {
        return this.list(Wrappers.query());
    }

    @Override
    public <P extends IPage<Customer>> P page(P page, QueryWrapper<Customer> wrapper) {
        return super.page(page, wrapper.eq("role_id", Customer.ROLE_ID));
    }

    @Override
    public <P extends IPage<Customer>> P page(P page, LambdaQueryWrapper<Customer> wrapper) {
        return super.page(page, wrapper.eq(Customer::getRoleId, Customer.ROLE_ID));
    }

    @Override
    public <P extends IPage<Customer>> P page(P page) {
        return this.page(page, Wrappers.query());
    }

    @Override
    public Customer getByUsername(String username) {
        return this.getOne(Wrappers.<Customer>lambdaQuery().eq(Customer::getUsername, username));
    }

    /**
     * 获取当前类的代理对象，由于注解 @Cacheable 基于 AOP 实现，类内部方法不会走代理，所以需要获取代理对象
     */
    private CustomerService currentProxy() {
        return (CustomerService) AopContext.currentProxy();
    }
}
