package com.kynsoft.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleResponse {
    private String message;
    private String status;
    private Long timestamp;

    // Constructor de conveniencia para mensajes simples
    public SimpleResponse(String message) {
        this.message = message;
        this.status = "SUCCESS";
        this.timestamp = System.currentTimeMillis();
    }

    // Métodos estáticos para creación rápida
    public static SimpleResponse success(String message) {
        return SimpleResponse.builder()
                .message(message)
                .status("SUCCESS")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static SimpleResponse info(String message) {
        return SimpleResponse.builder()
                .message(message)
                .status("INFO")
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
