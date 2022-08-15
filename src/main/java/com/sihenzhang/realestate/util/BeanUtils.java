package com.sihenzhang.realestate.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ModifierUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Field;

public final class BeanUtils {
    public static <T> T setBlankStrFieldsToNull(T bean, String... ignoreFields) {
        return BeanUtil.edit(bean, field -> {
            // 不处理忽略的字段
            if (ignoreFields != null && ArrayUtil.contains(ignoreFields, field.getName())) {
                return field;
            }
            // 只处理字符串字段
            if (String.class.equals(field.getType())) {
                final String val = (String) ReflectUtil.getFieldValue(bean, field);
                // 只处理非空的空白字符串
                if (val != null && StrUtil.isBlank(val)) {
                    ReflectUtil.setFieldValue(bean, field, null);
                }
            }
            return field;
        });
    }

    public static <T> boolean hasBlankField(T bean, String... fields) {
        if (bean == null) {
            return true;
        }
        for (Field field : ReflectUtil.getFields(bean.getClass())) {
            // 不处理 static 字段
            if (ModifierUtil.isStatic(field)) {
                continue;
            }
            // 没有指定字段则全部处理
            if (fields == null || ArrayUtil.contains(fields, field.getName())) {
                final Object val = ReflectUtil.getFieldValue(bean, field);
                // 字段为空则直接返回
                if (val == null) {
                    return true;
                }
                // 字符串则判断是否为空白字符串
                if (String.class.equals(field.getType()) && StrUtil.isBlank((String) val)) {
                    return true;
                }
            }
        }
        return false;
    }
}
