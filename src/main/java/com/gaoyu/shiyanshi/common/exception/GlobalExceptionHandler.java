package com.gaoyu.shiyanshi.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

/**
 * @Author：高宇
 * @Description：
 * @Data Created in 2026-10-16-16:58
 * @Modified By:
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Mono<String> handle(Exception e) {
        log.error("请求异常", e);
        return Mono.just("服务异常：" + e.getMessage());
    }
}