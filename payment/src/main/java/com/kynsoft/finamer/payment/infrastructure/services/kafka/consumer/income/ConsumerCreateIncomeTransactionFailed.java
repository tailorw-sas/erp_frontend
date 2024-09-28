package com.kynsoft.finamer.payment.infrastructure.services.kafka.consumer.income;

import com.kynsof.share.core.domain.kafka.entity.CreateIncomeTransactionFailedKafka;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerCreateIncomeTransactionFailed {
    private Logger logger = LoggerFactory.getLogger(CreateIncomeTransactionFailedKafka.class);
    private final IPaymentDetailService paymentDetailService;

    public ConsumerCreateIncomeTransactionFailed(IPaymentDetailService paymentDetailService) {
        this.paymentDetailService = paymentDetailService;
    }

    @KafkaListener(topics = "finamer-income-transaction-failed", groupId = "income-entity-replica")
    public void listen(CreateIncomeTransactionFailedKafka objKafka) {
        this.rollBackApplyDeposit(objKafka);
    }

    private void rollBackApplyDeposit(CreateIncomeTransactionFailedKafka objKafka) {
//        PaymentDetailDto paymentDetailDto = paymentDetailService.findById(objKafka.getRelatedPaymentDetail());
//        PaymentDetailDto parentDetailDto = paymentDetailService.findByGenId(paymentDetailDto.)
//        paymentDetailService.delete(paymentDetailDto);
        //TODO hacer el roll back cuando de error, averiguar las relaciones en payment
        logger.error("Rollback to applyDeposit ");

    }
}
