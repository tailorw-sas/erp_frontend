package com.kynsof.share.config;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(IMediator.class)
@ComponentScan(basePackages = "com.kynsof.share")
public class ShareAutoConfiguration {
}
