package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.ManageMerchantConfig;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManagerMerchantConfigKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantConfig.create.CreateManageMerchantConfigCommand;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageMerchantConfigService {
    private final IMediator mediator;

    public ConsumerReplicateManageMerchantConfigService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-merchant-config", groupId = "vcc-entity-replica")
    public void listen(ReplicateManagerMerchantConfigKafka entity) {
        try {
            CreateManageMerchantConfigCommand command = new CreateManageMerchantConfigCommand(entity.getId(),entity.getManageMerchant(), entity.getUrl(), entity.getAltUrl(), entity.getSuccessUrl(), entity.getErrorUrl(), entity.getDeclinedUrl(), entity.getMerchantType(), entity.getName(), entity.getMethod(), entity.getInstitutionCode(), entity.getMerchantNumber(), entity.getMerchantTerminal());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageMerchant.ConsumerReplicateManageMerchantService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
