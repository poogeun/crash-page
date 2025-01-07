package com.fastcampus.crash.exception;

import com.fastcampus.crash.model.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 이 핸들러가 Rest Api 전역에서 동작하도록 함
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ClientErrorException.class)
  public ResponseEntity<ErrorResponse> handleClientErrorException(ClientErrorException e) {
    return new ResponseEntity<>(new ErrorResponse(e.getStatus(), e.getMessage()), e.getStatus());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    var errorMessage =
        e.getFieldErrors().stream()
            .map(fieldError -> (fieldError.getField() + ": " + fieldError.getDefaultMessage()))
            .toList()
            .toString();

    return new ResponseEntity<>(
        new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage), HttpStatus.BAD_REQUEST);
  }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.BAD_REQUEST, "Required request body is missing"), HttpStatus.BAD_REQUEST);
    }

  // 5xx 서버 에러는 구체적인 내용을 클라이언트에게 알려줄 필요 없기에 500 에러를 리턴하도록 함
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
    return ResponseEntity.internalServerError().build();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    return ResponseEntity.internalServerError().build();
  }
}
