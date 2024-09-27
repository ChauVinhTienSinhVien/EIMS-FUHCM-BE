
package com.fullsnacke.eimsfuhcmbe.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ErrorDTO {
    private Date timestamp;
    private int status;
    private String path;
    private List<String> error = new ArrayList<>();

    public void addError(String message){
        this.error.add(message);
    }
}
