package com.sihenzhang.realestate.controller;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sihenzhang.realestate.entity.Sales;
import com.sihenzhang.realestate.result.Result;
import com.sihenzhang.realestate.result.ResultCode;
import com.sihenzhang.realestate.service.CustomerService;
import com.sihenzhang.realestate.service.HouseUnitService;
import com.sihenzhang.realestate.service.SalesService;
import com.sihenzhang.realestate.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sales")
public class SalesController {
    private SalesService salesService;
    private HouseUnitService houseUnitService;
    private CustomerService customerService;

    @Autowired
    public void setSalesService(SalesService salesService) {
        this.salesService = salesService;
    }

    @Autowired
    public void setHouseUnitService(HouseUnitService houseUnitService) {
        this.houseUnitService = houseUnitService;
    }

    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public Result getSales(@RequestParam(required = false) Integer current, @RequestParam(defaultValue = "5") Integer size) {
        if (current == null) {
            List<Sales> sales = salesService.list();
            return Result.buildSuccess("获取销售信息列表成功", Dict.of("sales", sales));
        }
        Page<Sales> page = new Page<>(current, size);
        Page<Sales> resultPage = salesService.page(page);
        Dict data = Dict.create().set("total", resultPage.getTotal()).set("current", resultPage.getCurrent()).set("sales", resultPage.getRecords());
        return Result.buildSuccess("获取销售信息列表成功", data);
    }

    @PostMapping
    public Result createSales(@RequestBody Sales sales) {
        if (houseUnitService.getById(sales.getUnitId()) == null) {
            return Result.buildFail("创建销售信息失败，户型不能为空");
        }
        if (customerService.getById(sales.getCustomerId()) == null) {
            return Result.buildFail("创建销售信息失败，客户不能为空");
        }
        if (BeanUtils.hasBlankField(sales, "salesTime", "completeTime", "totalPrice", "deposit", "mortgage")) {
            return Result.buildFail("创建销售信息失败，销售时间、交房时间、总价、首付、月供不能为空");
        }
        if (salesService.save(sales)) {
            return Result.build(ResultCode.CREATED, "创建销售信息成功");
        }
        return Result.buildFail("创建销售信息失败");
    }
}
