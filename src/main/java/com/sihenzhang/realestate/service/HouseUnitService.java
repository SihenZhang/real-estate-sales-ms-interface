package com.sihenzhang.realestate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sihenzhang.realestate.entity.HouseUnit;

import java.util.List;

public interface HouseUnitService extends IService<HouseUnit> {
    boolean updateById(Long id, HouseUnit houseUnit);

    List<HouseUnit> listByHouseId(Long houseId);

    <P extends IPage<HouseUnit>> P pageByHouseId(P page, Long houseId);
}
