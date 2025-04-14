package com.immfly.storeapi.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                List.of(ex.getMessage()),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ErrorResponse> handleOutOfStock(OutOfStockException ex) {
        ErrorResponse error = new ErrorResponse(
                List.of(ex.getMessage()),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderAlreadyFinishedException.class)
    public ResponseEntity<ErrorResponse> handleOrderAlreadyFinished(OrderAlreadyFinishedException ex) {
        ErrorResponse error = new ErrorResponse(
                List.of(ex.getMessage()),
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PaymentGatewayException.class)
    public ResponseEntity<ErrorResponse> handlePaymentGateway(PaymentGatewayException ex) {
        ErrorResponse error = new ErrorResponse(
                List.of(ex.getMessage()),
                HttpStatus.BAD_GATEWAY.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(PaymentStatusNullException.class)
    public ResponseEntity<ErrorResponse> handlePaymentStatusNull(PaymentStatusNullException ex) {
        ErrorResponse error = new ErrorResponse(
                List.of(ex.getMessage()),
                HttpStatus.BAD_GATEWAY.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(UnsupportedPaymentGatewayException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedPaymentGateway(UnsupportedPaymentGatewayException ex) {
        ErrorResponse error = new ErrorResponse(
                List.of(ex.getMessage()),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCategoryHierarchyException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCategoryHierarchy(InvalidCategoryHierarchyException ex) {
        ErrorResponse error = new ErrorResponse(
                List.of(ex.getMessage()),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryDeletionException.class)
    public ResponseEntity<ErrorResponse> handleCategoryDeletion(CategoryDeletionException ex) {
        ErrorResponse error = new ErrorResponse(
                List.of(ex.getMessage()),
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCategoryAlreadyExistsException(CategoryAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(
                List.of(ex.getMessage()),
                HttpStatus.CONFLICT.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        ErrorResponse error = new ErrorResponse(
                validationErrors,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                List.of("Internal Server Error: " + ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
