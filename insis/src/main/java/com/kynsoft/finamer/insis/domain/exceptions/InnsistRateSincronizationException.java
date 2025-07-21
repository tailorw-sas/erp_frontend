package com.kynsoft.finamer.insis.domain.exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class InnsistRateSincronizationException extends RuntimeException{

    public InnsistRateSincronizationException(UUID processId, String message){
        super(formatErrorMessage(processId, message));
    }

    private static String formatErrorMessage(UUID processId, String mensaje) {
        return String.format("Process ID [%s] - %s failed at %s", processId, mensaje, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
