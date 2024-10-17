package com.fullsnacke.eimsfuhcmbe.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode{

//    EMAIL_NOT_VERIFIED("Email not found.", HttpStatus.BAD_REQUEST),
//    EMAIL_NOT_FOUND("Your email is not permitted to log in to the system.", HttpStatus.FORBIDDEN),
//    UNAUTHENTICATED("Login failed.", HttpStatus.UNAUTHORIZED),
//    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
//    OVERLAP_SLOT("Exam slot overlaps with existing assignments in the same semester", HttpStatus.CONFLICT),
//    OVERLAP_SLOT_IN_LIST("The exam slot overlaps with another existing slot in the registration list.", HttpStatus.CONFLICT),
//    EXAM_SLOT_NOT_FOUND("Exam slot not found.", HttpStatus.BAD_REQUEST),
//    EXAM_SLOT_SET_EMPTY("Exam slot set is empty or null", HttpStatus.BAD_REQUEST),
//    HTTP_MESSAGE_NOT_READABLE("Failed to read request.", HttpStatus.BAD_REQUEST),
//    AUTHENTICATION_CONTEXT_NOT_FOUND("Authentication context not found or invalid.", HttpStatus.UNAUTHORIZED),
//    AUTHENTICATION_EMAIL_MISSING("Authentication email is missing or empty.", HttpStatus.UNAUTHORIZED),
//    EXAM_SLOT_ALREADY_REGISTERED("Exam slot already registered.", HttpStatus.CONFLICT),
//    SEMESTER_NOT_FOUND("Semester not found.", HttpStatus.BAD_REQUEST),
//    EXAM_SLOT_FULL("Exam slot is full.", HttpStatus.CONFLICT),
//    EXCEEDED_ALLOWED_SLOT("Exceeded allowed slot.", HttpStatus.CONFLICT),
//    REQUEST_EMPTY("Request not found", HttpStatus.BAD_REQUEST),
//    REQUEST_CREATION_FAILED("Failed to create request.", HttpStatus.INTERNAL_SERVER_ERROR),
//    EXAM_SLOT_ID_MISSING("Exam slot ID is missing.", HttpStatus.BAD_REQUEST),
//    REQUEST_TYPE_EMPTY("Request type is empty or null.", HttpStatus.BAD_REQUEST),
//    REASON_EMPTY("Reason is empty or null.", HttpStatus.BAD_REQUEST),
//    NO_REQUEST("No requests have been sent yet.", HttpStatus.NOT_FOUND),
//    DELETE_REGISTRATIONS_FAILED("Failed to delete registrations.", HttpStatus.INTERNAL_SERVER_ERROR),
//    NO_REGISTRATION_FOUND("No registration found.", HttpStatus.NOT_FOUND),
//    FAILD_TO_CLASSIFY_INVIGILATOR("Failed to classify invigilator.", HttpStatus.INTERNAL_SERVER_ERROR),
//    NO_INVIGILATOR_REGISTRATION("No invigilator registration found.", HttpStatus.NOT_FOUND),
//    ASYNC_REQUEST_NOT_USABLE("Request Failed", HttpStatus.INTERNAL_SERVER_ERROR),

    // Authentication and Login
    EMAIL_NOT_VERIFIED("Email not found.", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_FOUND("Your email is not permitted to log in to the system.", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED("Login failed.", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("User not found", HttpStatus.NOT_FOUND),
    AUTHENTICATION_CONTEXT_NOT_FOUND("Authentication context not found or invalid.", HttpStatus.UNAUTHORIZED),
    AUTHENTICATION_EMAIL_MISSING("Authentication email is missing or empty.", HttpStatus.UNAUTHORIZED),

    // Exam Slot Management
    EXAM_SLOT_NOT_FOUND("Exam slot not found.", HttpStatus.BAD_REQUEST),
    EXAM_SLOT_SET_EMPTY("Exam slot set is empty or null", HttpStatus.BAD_REQUEST),
    EXAM_SLOT_ALREADY_REGISTERED("Exam slot already registered.", HttpStatus.CONFLICT),
    EXAM_SLOT_FULL("Exam slot is full.", HttpStatus.CONFLICT),
    EXAM_SLOT_ID_MISSING("Exam slot ID is missing.", HttpStatus.BAD_REQUEST),

    // Exam Slot Registration
    OVERLAP_SLOT("Exam slot overlaps with existing assignments in the same semester", HttpStatus.CONFLICT),
    OVERLAP_SLOT_IN_LIST("The exam slot overlaps with another existing slot in the registration list.", HttpStatus.CONFLICT),
    EXCEEDED_ALLOWED_SLOT("Exceeded allowed slot.", HttpStatus.CONFLICT),

    // Semester Management
    SEMESTER_NOT_FOUND("Semester not found.", HttpStatus.BAD_REQUEST),

    // Request Handling
    REQUEST_EMPTY("Request not found", HttpStatus.BAD_REQUEST),
    REQUEST_CREATION_FAILED("Failed to create request.", HttpStatus.INTERNAL_SERVER_ERROR),
    REQUEST_TYPE_EMPTY("Request type is empty or null.", HttpStatus.BAD_REQUEST),
    REASON_EMPTY("Reason is empty or null.", HttpStatus.BAD_REQUEST),
    NO_REQUEST("No requests have been sent yet.", HttpStatus.NOT_FOUND),
    REQUEST_STATUS_INVALID("Request status is invalid.", HttpStatus.BAD_REQUEST),

    // Registration Management
    DELETE_REGISTRATIONS_FAILED("Failed to delete registrations.", HttpStatus.INTERNAL_SERVER_ERROR),
    NO_REGISTRATION_FOUND("No registration found.", HttpStatus.NOT_FOUND),

    // Invigilator Assignment
    FAILD_TO_CLASSIFY_INVIGILATOR("Failed to classify invigilator.", HttpStatus.INTERNAL_SERVER_ERROR),
    NO_INVIGILATOR_REGISTRATION("No invigilator registration found.", HttpStatus.NOT_FOUND),
    INVIGILATOR_NOT_FOUND("Invigilator not found.", HttpStatus.NOT_FOUND),
    EXCHANGE_INVIGILATORS_FAILED("Failed to exchange invigilators.", HttpStatus.INTERNAL_SERVER_ERROR),

    // HTTP Handling
    HTTP_MESSAGE_NOT_READABLE("Failed to read request.", HttpStatus.BAD_REQUEST),

    // Asynchronous Request Handling
    ASYNC_REQUEST_NOT_USABLE("Request Failed", HttpStatus.INTERNAL_SERVER_ERROR),

    // Method Argument Type Mismatch
    METHOD_ARGUMENT_TYPE_MISMATCH("Method argument type mismatch", HttpStatus.BAD_REQUEST),

    ;

    private String message;
    private HttpStatusCode statusCode;
    @Setter
    private String path;

    ErrorCode(String message, HttpStatusCode statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
