package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.income;

import com.kynsof.share.core.domain.kafka.entity.CreateIncomeTransactionFailedKafka;
import com.kynsof.share.core.domain.kafka.entity.CreateIncomeTransactionKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerCreateIncomeTransactionFailed {
    private final KafkaTemplate<String, Object> producer;

    public ProducerCreateIncomeTransactionFailed(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }
    public void create(CreateIncomeTransactionFailedKafka entity) {
        try {
            this.producer.send("finamer-income-transaction-failed", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerCreateIncomeTransactionFailed.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
