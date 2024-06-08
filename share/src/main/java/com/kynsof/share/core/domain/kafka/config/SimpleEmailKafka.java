package com.kynsof.share.core.domain.kafka.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleEmailKafka {
    private String toEmail;
    private String subject;
    private String message;
}