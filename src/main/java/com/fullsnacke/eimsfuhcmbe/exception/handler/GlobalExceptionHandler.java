package com.fullsnacke.eimsfuhcmbe.exception.handler;

import com.fullsnacke.eimsfuhcmbe.dto.response.ApiResponse;
import com.fullsnacke.eimsfuhcmbe.dto.response.ErrorDTO;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.AuthenticationProcessException;
<<<<<<< HEAD

import com.fullsnacke.eimsfuhcmbe.exception.repository.assignment.CustomException;

=======
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
>>>>>>> parent of af25c67 (Merge branch 'develop-2' of https://github.com/ChauVinhTienSinhVien/EIMS-FUHCM-BE into cvt-update-db)
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
    ResponseEntity<ApiResponse> handlingOAuth2AuthenticationProcessException(AuthenticationProcessException exception, HttpServletRequest request) {
        ErrorCode errorCode = exception.getErrorCode();
        if(errorCode.getPath() == null){
            errorCode.setPath(request.getRequestURI());
        }
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getStatusCode().value())
                        .message(errorCode.getMessage())
                        .path(errorCode.getPath())
                        .build());
    }

    //NGAN
    @ExceptionHandler(value = CustomException.class)
<<<<<<< HEAD
    ResponseEntity<ApiResponse> handlingInvigilatorAssignException(CustomException exception, HttpServletRequest request){
=======
    ResponseEntity<ApiResponse> handlingCustomException(CustomException exception, HttpServletRequest request){
>>>>>>> parent of af25c67 (Merge branch 'develop-2' of https://github.com/ChauVinhTienSinhVien/EIMS-FUHCM-BE into cvt-update-db)
        ErrorCode errorCode = exception.getErrorCode();
        if(errorCode.getPath() == null){
            errorCode.setPath(request.getServletPath());
        }
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getStatusCode().value())
                        .message(errorCode.getMessage())
                        .path(errorCode.getPath())
                        .build());
    }

    //NGAN
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorCode errorCode = ErrorCode.HTTP_MESSAGE_NOT_READABLE;

        if(errorCode.getPath() == null){
            errorCode.setPath(((ServletWebRequest) request).getRequest().getServletPath());
        }

        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getStatusCode().value())
                        .message(errorCode.getMessage())
                        .path(errorCode.getPath())
                        .build());
    }
}