package com.sihenzhang.realestate.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sihenzhang.realestate.entity.HousePhoto;
import com.sihenzhang.realestate.mapper.HousePhotoMapper;
import com.sihenzhang.realestate.service.HousePhotoService;
import com.sihenzhang.realestate.util.BeanUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class HousePhotoServiceImpl extends ServiceImpl<HousePhotoMapper, HousePhoto> implements HousePhotoService {
    @Override
    public boolean save(HousePhoto housePhoto) {
        housePhoto = BeanUtils.setBlankStrFieldsToNull(housePhoto);
        housePhoto = BeanUtil.trimStrFields(housePhoto);
        return super.save(housePhoto);
    }

    @Override
    @CacheEvict(value = "housePhoto", key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @CacheEvict(value = "housePhoto", key = "#housePhoto.id")
    public boolean updateById(HousePhoto housePhoto) {
        housePhoto = BeanUtils.setBlankStrFieldsToNull(housePhoto);
        housePhoto = BeanUtil.trimStrFields(housePhoto);
        return super.updateById(housePhoto);
    }

    @Override
    public boolean updateById(Long id, HousePhoto housePhoto) {
        housePhoto.setId(id);
        return this.currentProxy().updateById(housePhoto);
    }

    @Override
    @Cacheable(value = "housePhoto", key = "#id", unless = "#result == null")
    public HousePhoto getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public List<HousePhoto> listByHouseId(Long houseId) {
        return this.list(Wrappers.<HousePhoto>lambdaQuery().eq(HousePhoto::getHouseId, houseId));
    }

    @Override
    public <P extends IPage<HousePhoto>> P pageByHouseId(P page, Long houseId) {
        return this.page(page, Wrappers.<HousePhoto>lambdaQuery().eq(HousePhoto::getHouseId, houseId));
    }

    private HousePhotoService currentProxy() {
        return (HousePhotoService) AopContext.currentProxy();
    }
}
