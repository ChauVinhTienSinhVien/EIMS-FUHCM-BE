package com.fullsnacke.eimsfuhcmbe.exception.repository.assignment;

import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import lombok.*;

@Getter
@Setter
public class InvigilatorAssignException extends RuntimeException {
    private ErrorCode errorCode;

    public InvigilatorAssignException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
