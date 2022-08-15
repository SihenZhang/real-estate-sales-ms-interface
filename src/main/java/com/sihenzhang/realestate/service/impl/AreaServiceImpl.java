package com.sihenzhang.realestate.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sihenzhang.realestate.entity.Area;
import com.sihenzhang.realestate.mapper.AreaMapper;
import com.sihenzhang.realestate.service.AreaService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {
    @Override
    public boolean save(Area area) {
        throw new UnsupportedOperationException("不允许修改！");
    }

    @Override
    public boolean removeById(Serializable id) {
        throw new UnsupportedOperationException("不允许修改！");
    }

    @Override
    public boolean updateById(Area area) {
        throw new UnsupportedOperationException("不允许修改！");
    }

    @Override
    @Cacheable(value = "area", key = "#id", unless = "#result == null")
    public Area getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(value = "areas", key = "0")
    public List<Area> list() {
        return super.list();
    }

    @Override
    @Cacheable(value = "areasByCity", key = "#cityCode")
    public List<Area> listByCityCode(Integer cityCode) {
        return this.list(Wrappers.<Area>lambdaQuery().eq(Area::getCityCode, cityCode));
    }
}
