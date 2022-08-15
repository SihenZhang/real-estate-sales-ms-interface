package com.sihenzhang.realestate.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sihenzhang.realestate.entity.House;
import com.sihenzhang.realestate.entity.HousePhoto;
import com.sihenzhang.realestate.entity.HouseUnit;
import com.sihenzhang.realestate.result.Result;
import com.sihenzhang.realestate.result.ResultCode;
import com.sihenzhang.realestate.service.HousePhotoService;
import com.sihenzhang.realestate.service.HouseService;
import com.sihenzhang.realestate.service.HouseUnitService;
import com.sihenzhang.realestate.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/houses")
public class HouseController {
    private HouseService houseService;
    private HouseUnitService houseUnitService;
    private HousePhotoService housePhotoService;

    @Autowired
    public void setHouseService(HouseService houseService) {
        this.houseService = houseService;
    }

    @Autowired
    public void setHouseUnitService(HouseUnitService houseUnitService) {
        this.houseUnitService = houseUnitService;
    }

    @Autowired
    public void setHousePhotoService(HousePhotoService housePhotoService) {
        this.housePhotoService = housePhotoService;
    }

    @SaCheckPermission("house:query")
    @GetMapping
    public Result getHouses(@RequestParam(required = false) Integer current, @RequestParam(defaultValue = "5") Integer size) {
        if (current == null) {
            List<House> houses = houseService.list();
            return Result.buildSuccess("获取楼盘列表成功", Dict.of("houses", houses));
        }
        Page<House> page = new Page<>(current, size);
        Page<House> resultPage = houseService.page(page);
        Dict data = Dict.create().set("total", resultPage.getTotal()).set("current", resultPage.getCurrent()).set("houses", resultPage.getRecords());
        return Result.buildSuccess("获取楼盘列表成功", data);
    }

    @SaCheckPermission("house:query")
    @GetMapping("/{id}")
    public Result getHouseById(@PathVariable Long id) {
        House house = houseService.getById(id);
        if (house != null) {
            return Result.buildSuccess("获取楼盘成功", house);
        }
        return Result.build(ResultCode.NOT_FOUND, "获取楼盘失败，楼盘不存在");
    }

    @SaCheckPermission("house:create")
    @PostMapping
    public Result createHouse(@RequestBody House house) {
        if (BeanUtils.hasBlankField(house, "name", "areaCode", "lng", "lat", "address", "averagePrice")) {
            return Result.buildFail("创建楼盘失败，楼盘名称、区代码、经度、纬度、楼盘地址和均价不能为空");
        }
        if (houseService.save(house)) {
            return Result.build(ResultCode.CREATED, "创建楼盘成功");
        }
        return Result.buildFail("创建楼盘失败");
    }

    @SaCheckPermission("house:delete")
    @DeleteMapping("/{id}")
    public Result deleteHouse(@PathVariable Long id) {
        if (houseService.getById(id) == null) {
            return Result.build(ResultCode.NOT_FOUND, "删除楼盘失败，楼盘不存在");
        }
        if (houseService.removeById(id)) {
            return Result.buildSuccess("删除楼盘成功");
        }
        return Result.buildFail("删除楼盘失败");
    }

    @GetMapping("/{id}/units")
    public Result getHouseUnitsByHouseId(@PathVariable Long id, @RequestParam(required = false) Integer current, @RequestParam(defaultValue = "5") Integer size) {
        House house = houseService.getById(id);
        if (house == null) {
            return Result.build(ResultCode.NOT_FOUND, "获取户型列表失败，楼盘不存在");
        }
        if (current == null) {
            List<HouseUnit> houseUnits = houseUnitService.listByHouseId(id);
            Dict data = Dict.create().set("houseId", id).set("houseName", house.getName()).set("houseUnits", houseUnits);
            return Result.buildSuccess("获取户型列表成功", data);
        }
        Page<HouseUnit> page = new Page<>(current, size);
        Page<HouseUnit> resultPage = houseUnitService.pageByHouseId(page, id);
        Dict data = Dict.create().set("total", resultPage.getTotal()).set("current", resultPage.getCurrent()).set("houseId", id).set("houseName", house.getName()).set("houseUnits", resultPage.getRecords());
        return Result.buildSuccess("获取户型列表成功", data);
    }

    @GetMapping("/{hid}/units/{uid}")
    public Result getHouseUnitById(@PathVariable Long hid, @PathVariable Long uid) {
        if (houseService.getById(hid) == null) {
            return Result.build(ResultCode.NOT_FOUND, "获取户型失败，楼盘不存在");
        }
        HouseUnit houseUnit = houseUnitService.getById(uid);
        if (houseUnit == null) {
            return Result.build(ResultCode.NOT_FOUND, "获取户型失败，户型不存在");
        }
        if (houseUnit.getHouseId().equals(hid)) {
            return Result.buildSuccess("获取户型成功", houseUnit);
        }
        return Result.build(ResultCode.NOT_FOUND, "获取户型失败，户型不存在");
    }

    @PostMapping("/{id}/units")
    public Result createHouseUnit(@PathVariable Long id, @RequestBody HouseUnit houseUnit) {
        if (houseService.getById(id) == null) {
            return Result.build(ResultCode.NOT_FOUND, "创建户型失败，楼盘不存在");
        }
        houseUnit.setHouseId(id);
        if (BeanUtils.hasBlankField(houseUnit, "title")) {
            return Result.buildFail("创建户型失败，户型标题不能为空");
        }
        if (houseUnitService.save(houseUnit)) {
            return Result.build(ResultCode.CREATED, "创建户型成功");
        }
        return Result.buildFail("创建户型失败");
    }

    @PutMapping("/{hid}/units/{uid}")
    public Result updateHouseUnit(@PathVariable Long hid, @PathVariable Long uid, @RequestBody HouseUnit houseUnit) {
        if (houseService.getById(hid) == null) {
            return Result.build(ResultCode.NOT_FOUND, "编辑户型失败，楼盘不存在");
        }
        HouseUnit oldHouseUnit = houseUnitService.getById(uid);
        if (oldHouseUnit == null) {
            return Result.build(ResultCode.NOT_FOUND, "编辑户型失败，户型不存在");
        }
        if (!oldHouseUnit.getHouseId().equals(hid)) {
            return Result.build(ResultCode.NOT_FOUND, "编辑户型失败，户型不存在");
        }
        if (BeanUtils.hasBlankField(houseUnit, "title")) {
            return Result.buildFail("编辑户型失败，户型标题不能为空");
        }
        if (houseUnitService.updateById(uid, houseUnit)) {
            return Result.buildSuccess("编辑户型成功");
        }
        return Result.buildFail("编辑户型失败");
    }

    @DeleteMapping("/{hid}/units/{uid}")
    public Result deleteHouseUnit(@PathVariable Long hid, @PathVariable Long uid) {
        if (houseService.getById(hid) == null) {
            return Result.build(ResultCode.NOT_FOUND, "删除户型失败，楼盘不存在");
        }
        HouseUnit oldHouseUnit = houseUnitService.getById(uid);
        if (oldHouseUnit == null) {
            return Result.build(ResultCode.NOT_FOUND, "删除户型失败，户型不存在");
        }
        if (!oldHouseUnit.getHouseId().equals(hid)) {
            return Result.build(ResultCode.NOT_FOUND, "删除户型失败，户型不存在");
        }
        if (houseUnitService.removeById(uid)) {
            return Result.buildSuccess("删除户型成功");
        }
        return Result.buildFail("删除户型失败");
    }

    @GetMapping("/{id}/photos")
    public Result getHousePhotosByHouseId(@PathVariable Long id, @RequestParam(required = false) Integer current, @RequestParam(defaultValue = "5") Integer size) {
        House house = houseService.getById(id);
        if (house == null) {
            return Result.build(ResultCode.NOT_FOUND, "获取楼盘图片列表失败，楼盘不存在");
        }
        if (current == null) {
            List<HousePhoto> housePhotos = housePhotoService.listByHouseId(id);
            Dict data = Dict.create().set("houseId", id).set("houseName", house.getName()).set("housePhotos", housePhotos);
            return Result.buildSuccess("获取楼盘图片列表成功", data);
        }
        Page<HousePhoto> page = new Page<>(current, size);
        Page<HousePhoto> resultPage = housePhotoService.pageByHouseId(page, id);
        Dict data = Dict.create().set("total", resultPage.getTotal()).set("current", resultPage.getCurrent()).set("houseId", id).set("houseName", house.getName()).set("housePhotos", resultPage.getRecords());
        return Result.buildSuccess("获取楼盘图片列表成功", data);
    }

    @GetMapping("/{hid}/photos/{pid}")
    public Result getHousePhotoById(@PathVariable Long hid, @PathVariable Long pid) {
        House house = houseService.getById(hid);
        if (house == null) {
            return Result.build(ResultCode.NOT_FOUND, "获取楼盘图片失败，楼盘不存在");
        }
        HousePhoto housePhoto = housePhotoService.getById(pid);
        if (housePhoto == null) {
            return Result.build(ResultCode.NOT_FOUND, "获取楼盘图片失败，图片不存在");
        }
        if (!housePhoto.getHouseId().equals(hid)) {
            return Result.build(ResultCode.NOT_FOUND, "获取楼盘图片失败，图片不存在");
        }
        Dict data = Dict.create().set("houseId", hid).set("houseName", house.getName()).set("housePhoto", housePhoto);
        return Result.buildSuccess("获取楼盘图片成功", data);
    }

    @PostMapping("/{id}/photos")
    public Result createHousePhoto(@PathVariable Long id, @RequestBody HousePhoto housePhoto) {
        if (houseService.getById(id) == null) {
            return Result.build(ResultCode.NOT_FOUND, "创建楼盘图片失败，楼盘不存在");
        }
        housePhoto.setHouseId(id);
        if (BeanUtils.hasBlankField(housePhoto, "title", "type")) {
            return Result.buildFail("创建楼盘图片失败，图片标题和图片类型不能为空");
        }
        if (housePhotoService.save(housePhoto)) {
            return Result.build(ResultCode.CREATED, "创建楼盘图片成功");
        }
        return Result.buildFail("创建楼盘图片失败");
    }

    @DeleteMapping("/{hid}/photos/{pid}")
    public Result deleteHousePhoto(@PathVariable Long hid, @PathVariable Long pid) {
        House house = houseService.getById(hid);
        if (house == null) {
            return Result.build(ResultCode.NOT_FOUND, "删除楼盘图片失败，楼盘不存在");
        }
        HousePhoto housePhoto = housePhotoService.getById(pid);
        if (housePhoto == null) {
            return Result.build(ResultCode.NOT_FOUND, "删除楼盘图片失败，图片不存在");
        }
        if (!housePhoto.getHouseId().equals(hid)) {
            return Result.build(ResultCode.NOT_FOUND, "删除楼盘图片失败，图片不存在");
        }
        if (housePhotoService.removeById(pid)) {
            return Result.buildSuccess("删除楼盘图片成功");
        }
        return Result.buildFail("删除楼盘图片失败");
    }
}
