package com.fullsnacke.eimsfuhcmbe.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.AuthenticationException;

@Getter
@Setter
public class AuthenticationProcessException extends AuthenticationException {
    private ErrorCode errorCode;

    public AuthenticationProcessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
