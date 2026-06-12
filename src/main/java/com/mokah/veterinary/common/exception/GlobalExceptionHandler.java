package com.mokah.veterinary.common.exception;

import com.mokah.veterinary.common.dto.ApiErrorResponse;
import com.mokah.veterinary.features.conditionsbypet.exception.ConditionByPetExistsException;
import com.mokah.veterinary.features.diagnosisbystudies.exception.DiagnosisByStudyExistsException;
import com.mokah.veterinary.features.studiesbyvisit.exception.StudyByVisitExistsException;
import com.mokah.veterinary.features.veterinarians.exception.VeterinarianEmailExistsException;
import com.mokah.veterinary.features.veterinarians.exception.VeterinarianLicenseExistsException;
import com.mokah.veterinary.features.veterinarians.exception.VeterinarianPhoneExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid request body");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        String error = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError ->
                        fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildResponse(HttpStatus.BAD_REQUEST, error);
    }

    @ExceptionHandler({
            AppointmentOverlapException.class,
            AppointmentNotConfirmedException.class,
            BusinessRuleException.class,
            InvalidAppointmentTimeException.class,
            InvalidDateException.class,

            VeterinarianEmailExistsException.class,
            VeterinarianLicenseExistsException.class,
            VeterinarianPhoneExistsException.class,

            DiagnosisByStudyExistsException.class,
            ConditionByPetExistsException.class,
            StudyByVisitExistsException.class,

            OwnerDniExistsException.class,
            OwnerByPetExistsException.class,

            BranchNameExistsException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBusinessExceptions(RuntimeException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(HttpStatus status, String message) {
        ApiErrorResponse error = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );
        return new ResponseEntity<>(error, status);
    }
}