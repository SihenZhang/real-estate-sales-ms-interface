package com.sihenzhang.realestate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sihenzhang.realestate.entity.Permission;
import com.sihenzhang.realestate.mapper.PermissionMapper;
import com.sihenzhang.realestate.service.PermissionService;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    @Override
    public boolean save(Permission permission) {
        throw new UnsupportedOperationException("不允许修改！");
    }

    @Override
    public boolean removeById(Serializable id) {
        throw new UnsupportedOperationException("不允许修改！");
    }

    @Override
    public boolean updateById(Permission permission) {
        throw new UnsupportedOperationException("不允许修改！");
    }
}
