package com.fullsnacke.eimsfuhcmbe.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode{
    EMAIL_NOT_VERIFIED("Email not found.", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_FOUND("Your email is not permitted to log in to the system.", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED("Login failed.", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
    OVERLAP_SLOT("Exam slot overlaps with existing assignments in the same semester", HttpStatus.CONFLICT),
    EXAM_SLOT_NOT_FOUND("Exam slot not found.", HttpStatus.BAD_REQUEST),
    ;

    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(String message, HttpStatusCode statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
