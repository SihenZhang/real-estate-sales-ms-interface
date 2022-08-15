package com.sihenzhang.realestate.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sihenzhang.realestate.entity.House;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface HouseMapper extends BaseMapper<House> {
    @Select("SELECT id,h.name,area_code,p.name AS province_name,c.name AS city_name,a.name AS area_name,lng,lat," +
            "address,average_price,opening_time,complete_time,characteristic,sale_status,sales_address,sales_phone," +
            "plot_ratio,greening_rate,property,households,carport,building_num,manage_company,property_fee,developer," +
            "license,description,thumbnail FROM house AS h LEFT JOIN area AS a ON h.area_code = a.code " +
            "LEFT JOIN city AS c ON a.city_code = c.code LEFT JOIN province AS p ON a.province_code = p.code " +
            "${ew.customSqlSegment}")
    List<House> selectHouseVOList(@Param(Constants.WRAPPER) Wrapper<House> wrapper);

    @Select("SELECT id,h.name,area_code,p.name AS province_name,c.name AS city_name,a.name AS area_name,lng,lat," +
            "address,average_price,opening_time,complete_time,characteristic,sale_status,sales_address,sales_phone," +
            "plot_ratio,greening_rate,property,households,carport,building_num,manage_company,property_fee,developer," +
            "license,description,thumbnail FROM house AS h LEFT JOIN area AS a ON h.area_code = a.code " +
            "LEFT JOIN city AS c ON a.city_code = c.code LEFT JOIN province AS p ON a.province_code = p.code " +
            "${ew.customSqlSegment}")
    <P extends IPage<House>> P selectHouseVOPage(P page, @Param(Constants.WRAPPER) Wrapper<House> wrapper);
}
