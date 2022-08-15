package com.sihenzhang.realestate.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sihenzhang.realestate.entity.Customer;
import com.sihenzhang.realestate.result.Result;
import com.sihenzhang.realestate.result.ResultCode;
import com.sihenzhang.realestate.service.CustomerService;
import com.sihenzhang.realestate.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private CustomerService customerService;

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @SaCheckPermission("customer:query")
    @GetMapping
    public Result getCustomers(@RequestParam(required = false) Integer current, @RequestParam(defaultValue = "5") Integer size) {
        if (current == null) {
            List<Customer> customers = customerService.list();
            return Result.buildSuccess("获取客户列表成功", Dict.of("customers", customers));
        }
        Page<Customer> page = new Page<>(current, size);
        Page<Customer> resultPage = customerService.page(page);
        Dict data = Dict.create().set("total", resultPage.getTotal()).set("current", resultPage.getCurrent()).set("customers", resultPage.getRecords());
        return Result.buildSuccess("获取客户列表成功", data);
    }

    @SaCheckPermission("customer:query")
    @GetMapping("/{id}")
    public Result getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getById(id);
        if (customer != null) {
            return Result.buildSuccess("获取客户成功", customer);
        }
        return Result.build(ResultCode.NOT_FOUND, "获取客户失败，用户不存在");
    }

    @SaCheckPermission("customer:create")
    @PostMapping
    public Result createCustomer(@RequestBody Customer customer) {
        if (customerService.getByUsername(customer.getUsername()) != null) {
            return Result.buildFail("创建客户失败，用户名已存在");
        }
        if (BeanUtils.hasBlankField(customer, "username", "password", "realName")) {
            return Result.buildFail("创建客户失败，用户名、密码和真实姓名不能为空");
        }
        if (customerService.save(customer)) {
            return Result.build(ResultCode.CREATED, "创建客户成功");
        }
        return Result.buildFail("创建客户失败");
    }

    @SaCheckPermission("customer:update")
    @PutMapping("/{id}")
    public Result updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        if (customerService.getById(id) == null) {
            return Result.build(ResultCode.NOT_FOUND, "编辑客户失败，客户不存在");
        }
        if (BeanUtils.hasBlankField(customer, "username", "realName")) {
            return Result.buildFail("编辑客户失败，用户名和真实姓名不能为空");
        }
        if (customerService.updateById(id, customer)) {
            return Result.buildSuccess("编辑客户成功");
        }
        return Result.buildFail("编辑客户失败");
    }

    @SaCheckPermission("customer:delete")
    @DeleteMapping("/{id}")
    public Result deleteCustomer(@PathVariable Long id) {
        if (customerService.getById(id) == null) {
            return Result.build(ResultCode.NOT_FOUND, "删除客户失败，客户不存在");
        }
        if (customerService.removeById(id)) {
            return Result.buildSuccess("删除客户成功");
        }
        return Result.buildFail("删除客户失败");
    }
}
