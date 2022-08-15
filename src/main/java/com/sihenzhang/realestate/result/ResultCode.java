package com.sihenzhang.realestate.result;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ResultCode {
    OK(200),
    CREATED(201),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500);

    public final int code;
}
