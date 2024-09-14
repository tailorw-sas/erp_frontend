package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.ManageMerchantConfig;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageMerchantConfigKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantConfig.update.UpdateManageMerchantConfigCommand;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Method;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManagerMerchantConfigService {
    private final IMediator mediator;

    public ConsumerUpdateManagerMerchantConfigService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-merchant-config", groupId = "vcc-entity-replica")
    public void listen(UpdateManageMerchantConfigKafka objKafka) {
        try {
            UpdateManageMerchantConfigCommand command = new UpdateManageMerchantConfigCommand(objKafka.getId(),objKafka.getManageMerchantDto(), objKafka.getUrl(),objKafka.getAltUrl(), objKafka.getSuccessUrl(), objKafka.getErrorUrl(),objKafka.getDeclinedUrl(), objKafka.getMerchantType(),
                    objKafka.getName(), Method.valueOf(objKafka.getMethod()),objKafka.getInstitutionCode(),objKafka.getMerchantNumber(),objKafka.getMerchantTerminal());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerUpdateManagerMerchantConfigService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
