package com.fullsnacke.eimsfuhcmbe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ApiResponse <T> {
    Timestamp timestamp;
    int code = 200;
    String message;
    T result;
    List<T> listResult;

    public ApiResponse() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public void addResult(T result){
        this.listResult.add(result);
    }

//    public ApiResponse(int code, String message, T result, List<T> listResult) {
//        this();
//        this.code = code;
//        this.message = message;
//        this.result = result;
//        this.listResult = listResult;
//    }
}