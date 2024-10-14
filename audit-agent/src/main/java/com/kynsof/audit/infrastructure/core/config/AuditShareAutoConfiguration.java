package com.kynsof.audit.infrastructure.core.config;

import com.kynsof.audit.infrastructure.listener.AuditEntityListener;
import com.kynsof.audit.infrastructure.service.AuditLoader;
import com.kynsof.audit.infrastructure.service.kafka.ProducerAuditEventService;
import com.kynsof.audit.infrastructure.service.kafka.ProducerAuditRegisterEventService;
import com.kynsof.audit.infrastructure.utils.SpringContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@ConditionalOnClass(AuditEntityListener.class)
public class AuditShareAutoConfiguration {

    @Autowired
    private KafkaTemplate<String,Object> kafkaTemplate;

    @Primary
    @Bean
    public SpringContext getSpringContext(){
        return new SpringContext();
    }

    @Bean
    public ProducerAuditRegisterEventService getProducerAuditRegisterEventService(){
        return new ProducerAuditRegisterEventService(kafkaTemplate);
    }

    @Bean
    public ProducerAuditEventService getProducerAuditEventService(){
        return new ProducerAuditEventService(kafkaTemplate);
    }

    @Bean
    public AuditLoader getAuditLoader(){
        return new AuditLoader(getProducerAuditRegisterEventService());
    }
}
