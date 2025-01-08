package com.kynsoft.finamer.insis.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "encryption")
@Data
public class EncryptionProperties {
    private String secretKey;
    private String algoritm;
    private String mode;
    private String padding;
}
