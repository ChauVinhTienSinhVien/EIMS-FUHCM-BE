package com.fullsnacke.eimsfuhcmbe.exception;

public enum ErrorCode{
    UNAUTHORIZED(401, ""),
    FORBIDDEN(403, "Your email is not permitted to log in to the system.")
    ;

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
