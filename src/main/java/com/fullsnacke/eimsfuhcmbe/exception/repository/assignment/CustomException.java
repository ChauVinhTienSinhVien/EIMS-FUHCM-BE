package com.fullsnacke.eimsfuhcmbe.exception.repository.assignment;

import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import lombok.*;

@Getter
@Setter
public class CustomException extends IllegalArgumentException {
    private ErrorCode errorCode;

    public CustomException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, String path){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorCode.setPath(path);
    }
}
