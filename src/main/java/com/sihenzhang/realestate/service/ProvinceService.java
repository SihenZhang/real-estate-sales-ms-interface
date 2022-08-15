package com.sihenzhang.realestate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sihenzhang.realestate.entity.Province;

import java.util.List;

public interface ProvinceService extends IService<Province> {
    List<Province> list(int subDistrict);
}
