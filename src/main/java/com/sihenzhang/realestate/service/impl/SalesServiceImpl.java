package com.sihenzhang.realestate.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sihenzhang.realestate.entity.Sales;
import com.sihenzhang.realestate.mapper.SalesMapper;
import com.sihenzhang.realestate.service.SalesService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesServiceImpl extends ServiceImpl<SalesMapper, Sales> implements SalesService {
    @Override
    public List<Sales> list(Wrapper<Sales> queryWrapper) {
        return this.getBaseMapper().selectSalesVOList(queryWrapper);
    }

    @Override
    public List<Sales> list() {
        return this.list(Wrappers.emptyWrapper());
    }

    @Override
    public <P extends IPage<Sales>> P page(P page, Wrapper<Sales> queryWrapper) {
        return this.getBaseMapper().selectSalesVOPage(page, queryWrapper);
    }

    @Override
    public <P extends IPage<Sales>> P page(P page) {
        return this.page(page, Wrappers.emptyWrapper());
    }
}
