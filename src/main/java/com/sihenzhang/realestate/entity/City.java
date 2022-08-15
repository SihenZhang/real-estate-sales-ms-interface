package com.sihenzhang.realestate.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class City {
    @TableId(type = IdType.NONE)
    private Integer code;
    private String name;
    private Integer provinceCode;
    @TableField(exist = false)
    private List<Area> districts;
}
