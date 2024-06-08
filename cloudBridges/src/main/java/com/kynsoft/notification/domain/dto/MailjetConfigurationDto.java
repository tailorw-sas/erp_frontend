package com.kynsoft.notification.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailjetConfigurationDto {
    private UUID id;
    private String mailjetApiKey;
    private String mailjetApiSecret;
    private String fromEmail;
    private String fromName;
    private LocalDateTime createdAt;

}