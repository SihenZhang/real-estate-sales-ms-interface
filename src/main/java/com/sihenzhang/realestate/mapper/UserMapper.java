package com.sihenzhang.realestate.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sihenzhang.realestate.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT u.id,username,password,real_name,sex,age,phone,role_id,r.name AS role_name FROM user AS u " +
            "LEFT JOIN role AS r ON u.role_id = r.id ${ew.customSqlSegment}")
    User selectUserVO(@Param(Constants.WRAPPER) Wrapper<User> wrapper);

    @Select("SELECT u.id,username,password,real_name,sex,age,phone,role_id,r.name AS role_name FROM user AS u " +
            "LEFT JOIN role AS r ON u.role_id = r.id ${ew.customSqlSegment}")
    List<User> selectUserVOList(@Param(Constants.WRAPPER) Wrapper<User> wrapper);

    @Select("SELECT u.id,username,password,real_name,sex,age,phone,role_id,r.name AS role_name FROM user AS u " +
            "LEFT JOIN role AS r ON u.role_id = r.id ${ew.customSqlSegment}")
    <P extends IPage<User>> P selectUserVOPage(P page, @Param(Constants.WRAPPER) Wrapper<User> wrapper);
}
