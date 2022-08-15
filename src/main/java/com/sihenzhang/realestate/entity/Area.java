package com.sihenzhang.realestate.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Area {
    @TableId(type = IdType.NONE)
    private Integer code;
    private String name;
    private Integer provinceCode;
    private Integer cityCode;
}
