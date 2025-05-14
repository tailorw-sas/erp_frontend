package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.payment;

import com.kynsoft.finamer.invoicing.domain.services.IPaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerReplicatePaymentService {

    private final IPaymentService paymentService;

    public ConsumerReplicatePaymentService(IPaymentService paymentService){
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "finamer-replicate-payment", groupId = "invoicing-entity-replica")
    public void listen(){

    }
}
