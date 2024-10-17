package com.fullsnacke.eimsfuhcmbe.exception.handler;

import com.fullsnacke.eimsfuhcmbe.dto.response.ApiResponse;
import com.fullsnacke.eimsfuhcmbe.dto.response.ErrorDTO;
import com.fullsnacke.eimsfuhcmbe.exception.EntityNotFoundException;
import com.fullsnacke.eimsfuhcmbe.exception.ErrorCode;
import com.fullsnacke.eimsfuhcmbe.exception.AuthenticationProcessException;
import com.fullsnacke.eimsfuhcmbe.exception.apierror.ApiError;
import com.fullsnacke.eimsfuhcmbe.exception.repository.customEx.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
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
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.List;

@ControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

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
    ResponseEntity<ApiResponse<?>> handlingCustomException(CustomException exception, HttpServletRequest request){
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

    //NGAN
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    ResponseEntity<ApiResponse> handlingMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception, HttpServletRequest request){
        ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH;
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
    @ExceptionHandler(value = AsyncRequestNotUsableException.class)
    ResponseEntity<ApiResponse> handlingAsyncRequestNotUsableException(AsyncRequestNotUsableException exception, HttpServletRequest request){
        ErrorCode errorCode = ErrorCode.ASYNC_REQUEST_NOT_USABLE;
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

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFound(
            EntityNotFoundException ex) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

}