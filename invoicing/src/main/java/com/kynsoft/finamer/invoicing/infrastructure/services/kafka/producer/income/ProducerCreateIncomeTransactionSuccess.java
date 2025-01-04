package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.income;

import com.kynsof.share.core.domain.kafka.entity.CreateIncomeTransactionSuccessKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerCreateIncomeTransactionSuccess {

    private final KafkaTemplate<String, Object> producer;

    public ProducerCreateIncomeTransactionSuccess(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    public void create(CreateIncomeTransactionSuccessKafka entity) {
        try {
            this.producer.send("finamer-create-income-transaction-success", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerCreateIncomeTransactionSuccess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
