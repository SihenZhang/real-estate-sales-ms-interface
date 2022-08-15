package com.sihenzhang.realestate.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sihenzhang.realestate.entity.Customer;

import java.util.List;

public interface CustomerService extends IService<Customer> {
    boolean updateById(Long id, Customer customer);

    Customer getOne(QueryWrapper<Customer> wrapper);

    Customer getOne(LambdaQueryWrapper<Customer> wrapper);

    List<Customer> list(QueryWrapper<Customer> wrapper);

    List<Customer> list(LambdaQueryWrapper<Customer> wrapper);

    <P extends IPage<Customer>> P page(P page, QueryWrapper<Customer> wrapper);

    <P extends IPage<Customer>> P page(P page, LambdaQueryWrapper<Customer> wrapper);

    Customer getByUsername(String username);
}
