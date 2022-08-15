package com.sihenzhang.realestate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sihenzhang.realestate.entity.Area;

import java.io.Serializable;
import java.util.List;

public interface AreaService extends IService<Area> {
    List<Area> listByCityCode(Integer cityCode);
}
