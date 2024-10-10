package com.kynsof.audit.application.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration(proxyBeanMethods = false)
@ComponentScan(basePackages = "com.kynsof.audit")
@EnableJpaAuditing
public class AuditShareAutoConfiguration {
}
