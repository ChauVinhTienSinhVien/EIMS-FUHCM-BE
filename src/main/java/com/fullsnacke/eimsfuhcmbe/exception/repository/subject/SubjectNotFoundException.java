package com.fullsnacke.eimsfuhcmbe.exception.repository.subject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SubjectNotFoundException extends RuntimeException {

    private String message;

}
