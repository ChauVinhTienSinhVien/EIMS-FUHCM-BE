package com.fullsnacke.eimsfuhcmbe.exception.repository.semester;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SemesterNotFoundException extends RuntimeException {

    private String message;

}
