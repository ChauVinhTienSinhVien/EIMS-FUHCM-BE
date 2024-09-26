package com.fullsnacke.eimsfuhcmbe.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.AuthenticationException;

@Getter
@Setter
public class OAuth2AuthenticationProcessException extends AuthenticationException {
    private int errorCode;
    public OAuth2AuthenticationProcessException(String msg, Throwable t, int errorCode){
        super(msg, t);
        this.errorCode = errorCode;
    }

    public OAuth2AuthenticationProcessException(String msg, int errorCode){
        super(msg);
        this.errorCode = errorCode;
    }
}
