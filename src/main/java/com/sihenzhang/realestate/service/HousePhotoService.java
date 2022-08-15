package com.sihenzhang.realestate.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sihenzhang.realestate.entity.HousePhoto;

import java.util.List;

public interface HousePhotoService extends IService<HousePhoto> {
    boolean updateById(Long id, HousePhoto housePhoto);

    List<HousePhoto> listByHouseId(Long houseId);

    <P extends IPage<HousePhoto>> P pageByHouseId(P page, Long houseId);
}
