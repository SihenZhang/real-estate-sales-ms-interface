package com.sihenzhang.realestate.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sihenzhang.realestate.entity.Role;

public interface RoleService extends IService<Role> {
    boolean updateById(Long id, Role role);
}
