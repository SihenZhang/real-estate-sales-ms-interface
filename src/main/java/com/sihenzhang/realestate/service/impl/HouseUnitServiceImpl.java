package com.sihenzhang.realestate.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sihenzhang.realestate.entity.HouseUnit;
import com.sihenzhang.realestate.mapper.HouseUnitMapper;
import com.sihenzhang.realestate.service.HouseUnitService;
import com.sihenzhang.realestate.util.BeanUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class HouseUnitServiceImpl extends ServiceImpl<HouseUnitMapper, HouseUnit> implements HouseUnitService {
    @Override
    public boolean save(HouseUnit houseUnit) {
        houseUnit = BeanUtils.setBlankStrFieldsToNull(houseUnit);
        houseUnit = BeanUtil.trimStrFields(houseUnit);
        return super.save(houseUnit);
    }

    @Override
    @CacheEvict(value = "houseUnit", key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @CacheEvict(value = "houseUnit", key = "#houseUnit.id")
    public boolean updateById(HouseUnit houseUnit) {
        houseUnit = BeanUtils.setBlankStrFieldsToNull(houseUnit);
        houseUnit = BeanUtil.trimStrFields(houseUnit);
        return super.updateById(houseUnit);
    }

    @Override
    public boolean updateById(Long id, HouseUnit houseUnit) {
        houseUnit.setId(id);
        return this.currentProxy().updateById(houseUnit);
    }

    @Override
    @Cacheable(value = "houseUnit", key = "#id", unless = "#result == null")
    public HouseUnit getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public List<HouseUnit> listByHouseId(Long houseId) {
        return this.list(Wrappers.<HouseUnit>lambdaQuery().eq(HouseUnit::getHouseId, houseId));
    }

    @Override
    public <P extends IPage<HouseUnit>> P pageByHouseId(P page, Long houseId) {
        return this.page(page, Wrappers.<HouseUnit>lambdaQuery().eq(HouseUnit::getHouseId, houseId));
    }

    private HouseUnitService currentProxy() {
        return (HouseUnitService) AopContext.currentProxy();
    }
}
