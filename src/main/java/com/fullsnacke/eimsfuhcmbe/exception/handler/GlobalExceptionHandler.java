package com.fullsnacke.eimsfuhcmbe.exception.handler;

import com.fullsnacke.eimsfuhcmbe.dto.response.ApiResponse;
import com.fullsnacke.eimsfuhcmbe.dto.response.ErrorDTO;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.AuthenticationProcessException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class) //Handles one or some specific types of exceptions
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody // Indicates the method return value should be bound to the web request body
    public ErrorDTO handleGenericException(HttpServletRequest request, Exception exception ){
        ErrorDTO errorDTO = new ErrorDTO();

        errorDTO.setTimestamp(new Date());
        errorDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDTO.addError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        errorDTO.setPath(request.getServletPath());

        LOGGER.error(exception.getMessage(), exception);

        return errorDTO;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTimestamp(new Date());
        errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDTO.setPath(((ServletWebRequest) request).getRequest().getServletPath());

        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        fieldErrors.forEach(fieldError -> {
            errorDTO.addError(fieldError.getDefaultMessage());
        });

        return new ResponseEntity<>(errorDTO, headers, status);
    }

    //NGAN
    @ExceptionHandler(value = AuthenticationProcessException.class)
    ResponseEntity<ApiResponse> handlingOAuth2AuthenticationProcessException(AuthenticationProcessException exception){
        ErrorCode errorCode = exception.getErrorCode();
        System.out.println("Not found have been here");
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getStatusCode().value())
                        .message(errorCode.getMessage())
                        .build());
    }
    //NGAN
//    @ExceptionHandler(value = NoResourceFoundException.class)
//    private void handlingNoResourceFoundException(NoResourceFoundException exception){
//        ErrorCode errorCode = ErrorCode.LOGIN_PAGE_NOT_FOUND;
//
//        return ResponseEntity
//                .status(errorCode.getStatusCode())
//                .body(ApiResponse.builder()
//                        .code(errorCode.getStatusCode().value())
//                        .message(errorCode.getMessage())
//                        .build());
//    }



}