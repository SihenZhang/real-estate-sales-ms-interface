package com.sihenzhang.realestate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sihenzhang.realestate.entity.House;

public interface HouseService extends IService<House> {
    boolean updateById(Long id, House house);
}
