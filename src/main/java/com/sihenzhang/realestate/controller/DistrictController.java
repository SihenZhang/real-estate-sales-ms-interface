package com.sihenzhang.realestate.controller;

import cn.hutool.core.lang.Dict;
import com.sihenzhang.realestate.entity.Area;
import com.sihenzhang.realestate.entity.City;
import com.sihenzhang.realestate.entity.Province;
import com.sihenzhang.realestate.result.Result;
import com.sihenzhang.realestate.result.ResultCode;
import com.sihenzhang.realestate.service.AreaService;
import com.sihenzhang.realestate.service.CityService;
import com.sihenzhang.realestate.service.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/districts")
public class DistrictController {
    private ProvinceService provinceService;
    private CityService cityService;
    private AreaService areaService;

    @Autowired
    public void setProvinceService(ProvinceService provinceService) {
        this.provinceService = provinceService;
    }

    @Autowired
    public void setCityService(CityService cityService) {
        this.cityService = cityService;
    }

    @Autowired
    public void setAreaService(AreaService areaService) {
        this.areaService = areaService;
    }

    @GetMapping
    public Result getDistricts(@RequestParam(defaultValue = "1") Integer level, @RequestParam(defaultValue = "0") Integer subDistrict) {
        // 最多只能获取省级（1）、市级（2）和区级（3）
        level = Math.max(1, Math.min(level, 3));
        // 获取下几级的行政区
        subDistrict = Math.max(0, Math.min(subDistrict, 3 - level));
        switch (level) {
            case 1:
                List<Province> provinces = provinceService.list(subDistrict);
                return Result.buildSuccess("获取省级行政区列表成功", Dict.of("districts", provinces));
            case 2:
                List<City> cities = cityService.list(subDistrict);
                return Result.buildSuccess("获取市级行政区列表成功", Dict.of("districts", cities));
            default:
                List<Area> areas = areaService.list();
                return Result.buildSuccess("获取区级行政区列表成功", Dict.of("districts", areas));
        }
    }

    @GetMapping("/{code}")
    public Result getSubDistrictsByCode(@PathVariable Integer code) {
        // 先尝试根据编码获取省级行政区
        Province province = provinceService.getById(code);
        // 如果有找到，则获取其所属的市级行政区
        if (province != null) {
            List<City> cities = cityService.listByProvinceCode(code);
            province.setDistricts(cities);
            return Result.buildSuccess("获取下级行政区成功", province);
        }
        // 再尝试根据编码获取市级行政区
        City city = cityService.getById(code);
        // 如果有找到，则获取其所属的区级行政区
        if (city != null) {
            List<Area> areas = areaService.listByCityCode(code);
            city.setDistricts(areas);
            return Result.buildSuccess("获取下级行政区成功", city);
        }
        // 最后尝试根据编码获取区级行政区
        Area area = areaService.getById(code);
        // 如果有找到，则直接返回该行政区
        if (area != null) {
            return Result.buildSuccess("获取下级行政区成功", area);
        }
        // 如果没有找到，则返回错误信息
        return Result.build(ResultCode.NOT_FOUND, "获取下级行政区失败，行政区不存在");
    }
}
