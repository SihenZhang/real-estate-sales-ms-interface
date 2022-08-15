package com.sihenzhang.realestate.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Result {
    private int code;
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;

    public static Result build(int code, String message, Object data) {
        return new Result(code, message, data);
    }

    public static Result build(ResultCode resultCode, String message, Object data) {
        return build(resultCode.code, message, data);
    }

    public static Result build(ResultCode resultCode, String message) {
        return build(resultCode, message, null);
    }

    public static Result buildSuccess(String message, Object data) {
        return new Result(ResultCode.OK.code, message, data);
    }

    public static Result buildSuccess(String message) {
        return buildSuccess(message, null);
    }

    public static Result buildFail(String message) {
        return new Result(ResultCode.BAD_REQUEST.code, message, null);
    }
}
