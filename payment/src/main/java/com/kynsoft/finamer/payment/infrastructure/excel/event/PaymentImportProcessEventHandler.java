package com.kynsoft.finamer.payment.infrastructure.excel.event;

import com.kynsoft.finamer.payment.domain.dtoEnum.ImportProcessStatus;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class PaymentImportProcessEventHandler implements ApplicationListener<PaymentImportProcessEvent> {

    private final RedisTemplate<String,String> redisTemplate;

    public PaymentImportProcessEventHandler(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onApplicationEvent(PaymentImportProcessEvent event) {
        String importProcessId= (String) event.getSource();
        String status = redisTemplate.opsForValue().get(importProcessId);
        if (Objects.nonNull(status) &&  ImportProcessStatus.RUNNING.name().equals(status)){
            redisTemplate.opsForValue().set(importProcessId,ImportProcessStatus.FINISHED.name(),Duration.of(30, ChronoUnit.MINUTES));
        }else {
            redisTemplate.opsForValue().set(importProcessId, ImportProcessStatus.RUNNING.name(), Duration.of(30, ChronoUnit.MINUTES));
        }
    }
}
