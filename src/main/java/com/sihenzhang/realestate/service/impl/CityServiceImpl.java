package com.sihenzhang.realestate.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sihenzhang.realestate.entity.Area;
import com.sihenzhang.realestate.entity.City;
import com.sihenzhang.realestate.mapper.CityMapper;
import com.sihenzhang.realestate.service.AreaService;
import com.sihenzhang.realestate.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl extends ServiceImpl<CityMapper, City> implements CityService {
    private AreaService areaService;

    @Autowired
    public void setAreaService(AreaService areaService) {
        this.areaService = areaService;
    }

    @Override
    public boolean save(City city) {
        throw new UnsupportedOperationException("不允许修改！");
    }

    @Override
    public boolean removeById(Serializable id) {
        throw new UnsupportedOperationException("不允许修改！");
    }

    @Override
    public boolean updateById(City city) {
        throw new UnsupportedOperationException("不允许修改！");
    }

    @Override
    @Cacheable(value = "city", key = "#id", unless = "#result == null")
    public City getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(value = "cities", key = "#subDistrict")
    public List<City> list(int subDistrict) {
        // 市级只能获取下一级行政区
        Assert.checkBetween(subDistrict, 0, 1);
        List<City> cities = this.list();
        // 如果不获取下级行政区，则直接返回
        if (subDistrict == 0) {
            return cities;
        }
        // 将市根据其行政编码进行映射
        Map<Integer, City> keyCities = cities.stream().collect(Collectors.toMap(City::getCode, Function.identity()));
        // 获取所有的区
        List<Area> areas = areaService.list();
        areas.forEach(area -> {
            // 获取区所属的市
            City city = keyCities.get(area.getCityCode());
            // 如果区所属的市不存在，则跳过
            if (city == null) {
                return;
            }
            // 如果区所属的市的 districts 字段为空，则新建一个空列表
            if (city.getDistricts() == null) {
                city.setDistricts(new ArrayList<>());
            }
            // 将区添加到市的 districts 字段中
            city.getDistricts().add(area);
        });
        return cities;
    }

    @Override
    @Cacheable(value = "citiesByProvince", key = "#provinceCode")
    public List<City> listByProvinceCode(Integer provinceCode) {
        return this.list(Wrappers.<City>lambdaQuery().eq(City::getProvinceCode, provinceCode));
    }
}
