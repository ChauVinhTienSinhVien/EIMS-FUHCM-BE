package com.fullsnacke.eimsfuhcmbe.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode{
    EMAIL_NOT_VERIFIED("Email not found.", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_FOUND("Your email is not permitted to log in to the system.", HttpStatus.FORBIDDEN),
    LOGIN_PAGE_NOT_FOUND("Login page not found.", HttpStatus.NOT_FOUND),
    LOGIN_FAILED("Login failed.", HttpStatus.UNAUTHORIZED),

    ;

    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(String message, HttpStatusCode statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
