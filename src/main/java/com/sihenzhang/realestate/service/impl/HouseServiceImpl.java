package com.sihenzhang.realestate.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sihenzhang.realestate.entity.House;
import com.sihenzhang.realestate.mapper.HouseMapper;
import com.sihenzhang.realestate.service.HouseService;
import com.sihenzhang.realestate.util.BeanUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class HouseServiceImpl extends ServiceImpl<HouseMapper, House> implements HouseService {
    @Override
    public boolean save(House house) {
        house = BeanUtils.setBlankStrFieldsToNull(house);
        house = BeanUtil.trimStrFields(house);
        return super.save(house);
    }

    @Override
    @CacheEvict(value = "house", key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @CacheEvict(value = "house", key = "#house.id")
    public boolean updateById(House house) {
        house = BeanUtils.setBlankStrFieldsToNull(house);
        house = BeanUtil.trimStrFields(house);
        return super.updateById(house);
    }

    @Override
    public boolean updateById(Long id, House house) {
        house.setId(id);
        return this.currentProxy().updateById(house);
    }

    @Override
    @Cacheable(value = "house", key = "#id", unless = "#result == null")
    public House getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public List<House> list(Wrapper<House> wrapper) {
        return this.getBaseMapper().selectHouseVOList(wrapper);
    }

    @Override
    public List<House> list() {
        return this.list(Wrappers.emptyWrapper());
    }

    @Override
    public <P extends IPage<House>> P page(P page, Wrapper<House> wrapper) {
        return this.getBaseMapper().selectHouseVOPage(page, wrapper);
    }

    @Override
    public <P extends IPage<House>> P page(P page) {
        return this.page(page, Wrappers.emptyWrapper());
    }

    private HouseService currentProxy() {
        return (HouseService) AopContext.currentProxy();
    }
}
