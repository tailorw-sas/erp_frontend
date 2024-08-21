package com.kynsoft.finamer.payment.infrastructure.excel.event.process;

import com.kynsoft.finamer.payment.domain.dto.PaymentImportStatusDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentImportProcessStatus;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportProcessStatusEntity;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportProcessStatusRepository;
import io.jsonwebtoken.lang.Assert;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Component
public class PaymentImportProcessEventHandler implements ApplicationListener<PaymentImportProcessEvent> {

    private final PaymentImportProcessStatusRepository statusRepository;

    public PaymentImportProcessEventHandler(PaymentImportProcessStatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @Override
    public void onApplicationEvent(PaymentImportProcessEvent event) {
        PaymentImportStatusDto paymentImportStatusDto = event.getPaymentImportStatusDto();
        PaymentImportStatusDto save =statusRepository.findByImportProcessId(paymentImportStatusDto.getImportProcessId())
                .map(PaymentImportProcessStatusEntity::toAggregate).orElse(null);
        if (Objects.nonNull(save)) {
            save.setStatus(paymentImportStatusDto.getStatus());
            save.setHasError(paymentImportStatusDto.isHasError());
            save.setExceptionMessage(paymentImportStatusDto.getExceptionMessage());
            statusRepository.save(save.toAggregate());
        }else {
            statusRepository.save(paymentImportStatusDto.toAggregate());
        }

    }
}
