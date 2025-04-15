package com.human.tapMMO.exception;

import com.human.tapMMO.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(NotFoundException.class)
//    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(NotFoundException e) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(new ApiResponse<>(false, e.getMessage(), null));
//    }
//
//    @ExceptionHandler(ForbiddenException.class)
//    public ResponseEntity<ApiResponse<Void>> handleForbiddenException(ForbiddenException e) {
//        return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                .body(new ApiResponse<>(false, e.getMessage(), null));
//    }
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(new ApiResponse<>(false, e.getMessage(), null));
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception e) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(new ApiResponse<>(false, "Internal server error: " + e.getMessage(), null));
//    }


    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoHandlerFoundException ex) {
        Map<String, Object> errorBody = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Not Found",
                "message", "Запрашиваемый ресурс не найден",
                "path", ex.getRequestURL()
        );
        return new ResponseEntity<>(errorBody, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        Map<String, Object> errorBody = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error", "Internal Server Error",
                "message", ex.getMessage()
        );
        return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNoSuchElementException(Exception ex) {
        Map<String, Object> errorBody = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Элемент не найден",
                "message", ex.getMessage()
        );
        return new ResponseEntity<>(errorBody, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(Exception ex) {
        Map<String, Object> errorBody = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.UNAUTHORIZED.value(),
                "error", "Ошибка авторизации",
                "message", ex.getMessage()
        );
        return new ResponseEntity<>(errorBody, HttpStatus.UNAUTHORIZED);
    }
}

