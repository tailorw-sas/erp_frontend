package com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageMerchantBankAccount;

import com.kynsof.share.core.domain.kafka.entity.ManageBankKafka;
import com.kynsof.share.core.domain.kafka.entity.ManageMerchantBankAccountKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProducerReplicateManageMerchantBankAccountService {
    private final KafkaTemplate<String, Object> producer;

    public ProducerReplicateManageMerchantBankAccountService(KafkaTemplate<String, Object> producer) {
        this.producer = producer;
    }

    @Async
    public void replicate(ManageMerchantBankAccountKafka entity) {

        try {
            this.producer.send("finamer-replicate-manage-merchant-bank-account", entity);
        } catch (Exception ex) {
            Logger.getLogger(ProducerReplicateManageMerchantBankAccountService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}