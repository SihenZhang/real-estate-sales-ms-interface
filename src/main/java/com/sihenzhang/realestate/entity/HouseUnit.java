package com.sihenzhang.realestate.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseUnit {
    @TableId
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long houseId;
    private String title;
    private String pic;
    private Byte saleStatus;
    private BigDecimal price;
    private String characteristic;
    private Double area;
    private Double height;
    private String aspect;
    private Byte room;
    private Byte livingRoom;
    private Byte kitchen;
    private Byte restroom;
    private Byte isMain;
    private String description;
}
