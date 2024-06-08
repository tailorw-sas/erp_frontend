package com.kynsof.share.core.infrastructure.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.lang.reflect.Method;

@ControllerAdvice
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(AsyncExceptionHandler.class);

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        logger.error(
                "Unexpected asynchronus exception at: " + method.getDeclaringClass().getName() + "." + method.getName(),
                ex);
    }
}
