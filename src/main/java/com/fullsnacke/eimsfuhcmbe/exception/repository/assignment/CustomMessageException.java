package com.fullsnacke.eimsfuhcmbe.exception.repository.assignment;

import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@Setter
public class CustomMessageException extends RuntimeException {
    private HttpStatusCode statusCode;
    private String message;
    private String path;

    public CustomMessageException(HttpStatus statusCode, String message){
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

}
