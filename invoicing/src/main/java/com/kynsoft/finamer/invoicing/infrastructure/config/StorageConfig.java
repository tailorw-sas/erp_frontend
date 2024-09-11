package com.kynsoft.finamer.invoicing.infrastructure.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class StorageConfig {
    @Value("${upload.location}")
    private String uploadLocation;
}
