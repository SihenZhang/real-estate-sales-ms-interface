package com.sihenzhang.realestate.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.sihenzhang.realestate.entity.Sales;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SalesMapper extends BaseMapper<Sales> {
    @Select("SELECT s.id,unit_id,h.name AS house_name,title AS unit_title,customer_id,real_name AS customer_real_name," +
            "sales_time,s.complete_time,total_price,deposit,mortgage FROM sales AS s " +
            "LEFT JOIN house_unit AS u ON s.unit_id = u.id LEFT JOIN house AS h ON u.house_id = h.id " +
            "LEFT JOIN user AS c ON s.customer_id = c.id ${ew.customSqlSegment}")
    List<Sales> selectSalesVOList(@Param(Constants.WRAPPER) Wrapper<Sales> queryWrapper);

    @Select("SELECT s.id,unit_id,h.name AS house_name,title AS unit_title,customer_id,real_name AS customer_real_name," +
            "sales_time,s.complete_time,total_price,deposit,mortgage FROM sales AS s " +
            "LEFT JOIN house_unit AS u ON s.unit_id = u.id LEFT JOIN house AS h ON u.house_id = h.id " +
            "LEFT JOIN user AS c ON s.customer_id = c.id ${ew.customSqlSegment}")
    <P extends IPage<Sales>> P selectSalesVOPage(P page, @Param(Constants.WRAPPER) Wrapper<Sales> queryWrapper);
}
