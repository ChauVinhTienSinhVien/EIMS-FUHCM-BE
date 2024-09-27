package com.fullsnacke.eimsfuhcmbe.exception;

import com.fullsnacke.eimsfuhcmbe.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    /*Xử lý exception trong lúc login
    Ví dụ:
    - Email bị sai, hoặc không tìm thấy
    - Login với email đúng nhưng mà không tồn tại trong db
    * */
    @ExceptionHandler(value = OAuth2AuthenticationProcessException.class)
    ResponseEntity<ApiResponse> handlingOAuth2AuthenticationProcessException(OAuth2AuthenticationProcessException exception){
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(exception.getErrorCode());
        apiResponse.setMessage(exception.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }


}
