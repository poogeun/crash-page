package com.fastcampus.crash.exception;

import org.springframework.http.HttpStatus;

// 4xx 클라이언트 요청을 처리할 때 발생하는 에러를 담당
public class ClientErrorException extends RuntimeException {

    private final HttpStatus status;

    // 구체적인 에러 코드와 에러 메세지를 받는 생성자
    public ClientErrorException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
