package com.sihenzhang.realestate.component;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.hutool.core.util.StrUtil;
import com.sihenzhang.realestate.result.Result;
import com.sihenzhang.realestate.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public Result handleException(Exception e) {
        if (e instanceof NotLoginException) {
            return Result.build(ResultCode.UNAUTHORIZED, e.getMessage());
        } else if (e instanceof NotRoleException || e instanceof NotPermissionException) {
            return Result.build(ResultCode.FORBIDDEN, e.getMessage());
        } else if (e instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException ee = (MissingServletRequestParameterException) e;
            return Result.buildFail(StrUtil.format("缺少{}类型的参数：{}", ee.getParameterType(), ee.getParameterName()));
        } else if (e instanceof MissingPathVariableException) {
            MissingPathVariableException ee = (MissingPathVariableException) e;
            return Result.buildFail(StrUtil.format("缺少{}类型的路径变量：{}", ee.getParameter().getNestedParameterType().getSimpleName(), ee.getVariableName()));
        } else if (e instanceof MissingRequestValueException) {
            return Result.buildFail("缺少请求参数：" + e.getMessage());
        } else if (e instanceof DuplicateKeyException) {
            return Result.buildFail("数据库中已存在该记录：" + e.getMessage());
        } else if (e instanceof DataAccessException) {
            return Result.buildFail("数据库操作失败：" + e.getMessage());
        }
        log.error("Fail to handle exception:", e);
        return Result.build(ResultCode.INTERNAL_SERVER_ERROR, "未知错误：" + e.getMessage());
    }
}
