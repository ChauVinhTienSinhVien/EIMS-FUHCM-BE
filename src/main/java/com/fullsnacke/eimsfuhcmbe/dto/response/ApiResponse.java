package com.fullsnacke.eimsfuhcmbe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse <T> {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    int code = HttpStatus.OK.value();
    String message;
    T result;
    List<T> listResult;
}