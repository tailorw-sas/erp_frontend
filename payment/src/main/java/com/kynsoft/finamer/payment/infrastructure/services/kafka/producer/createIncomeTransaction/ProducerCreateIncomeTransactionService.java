package com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.createIncomeTransaction;

import com.kynsof.share.core.domain.kafka.entity.CreateIncomeTransactionKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerCreateIncomeTransactionService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerCreateIncomeTransactionService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    public void create(CreateIncomeTransactionKafka entity) {
        try {
             this.producer.send("finamer-create-income-transaction", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerCreateIncomeTransactionService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}