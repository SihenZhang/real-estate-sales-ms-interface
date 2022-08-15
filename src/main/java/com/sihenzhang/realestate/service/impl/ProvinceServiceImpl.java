package com.sihenzhang.realestate.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sihenzhang.realestate.entity.City;
import com.sihenzhang.realestate.entity.Province;
import com.sihenzhang.realestate.mapper.ProvinceMapper;
import com.sihenzhang.realestate.service.CityService;
import com.sihenzhang.realestate.service.ProvinceService;
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
public class ProvinceServiceImpl extends ServiceImpl<ProvinceMapper, Province> implements ProvinceService {
    private CityService cityService;

    @Autowired
    public void setCityService(CityService cityService) {
        this.cityService = cityService;
    }

    @Override
    public boolean save(Province province) {
        throw new UnsupportedOperationException("不允许修改！");
    }

    @Override
    public boolean removeById(Serializable id) {
        throw new UnsupportedOperationException("不允许修改！");
    }

    @Override
    public boolean updateById(Province province) {
        throw new UnsupportedOperationException("不允许修改！");
    }

    @Override
    @Cacheable(value = "province", key = "#id", unless = "#result == null")
    public Province getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(value = "provinces", key = "#subDistrict")
    public List<Province> list(int subDistrict) {
        // 省级只能获取下两级行政区
        Assert.checkBetween(subDistrict, 0, 2);
        // 如果不获取下级行政区，则直接返回
        List<Province> provinces = this.list();
        if (subDistrict == 0) {
            return provinces;
        }
        // 将省根据其行政编码进行映射
        Map<Integer, Province> keyProvinces = provinces.stream().collect(Collectors.toMap(Province::getCode, Function.identity()));
        // 根据其获取下几级的行政区来获取所有的市
        List<City> cities = cityService.list(subDistrict - 1);
        cities.forEach(city -> {
            // 获取市所属的省
            Province province = keyProvinces.get(city.getProvinceCode());
            // 如果市所属的省不存在，则跳过
            if (province == null) {
                return;
            }
            // 如果市所属的省的 districts 字段为空，则新建一个空列表
            if (province.getDistricts() == null) {
                province.setDistricts(new ArrayList<>());
            }
            // 将市添加到省的 districts 字段中
            province.getDistricts().add(city);
        });
        return provinces;
    }
}
