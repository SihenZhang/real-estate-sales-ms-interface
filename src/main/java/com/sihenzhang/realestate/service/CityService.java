package com.sihenzhang.realestate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sihenzhang.realestate.entity.City;

import java.util.List;

public interface CityService extends IService<City> {
    List<City> list(int subDistrict);

    List<City> listByProvinceCode(Integer provinceCode);
}
