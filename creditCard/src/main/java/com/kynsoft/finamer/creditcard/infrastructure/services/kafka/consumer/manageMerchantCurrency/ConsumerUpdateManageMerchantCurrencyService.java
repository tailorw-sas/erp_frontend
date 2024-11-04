package com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageMerchantCurrency;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageMerchantCurrencyKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantCurrency.create.CreateManageMerchantCurrencyCommand;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantCurrency.update.UpdateManagerMerchantCurrencyCommand;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerUpdateManageMerchantCurrencyService {
    private final IMediator mediator;

    public ConsumerUpdateManageMerchantCurrencyService(IMediator mediator) {
        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-merchant-currency", groupId = "vcc-entity-replica")
    public void listen(ReplicateManageMerchantCurrencyKafka entity) {
        try {
            UpdateManagerMerchantCurrencyCommand command = new UpdateManagerMerchantCurrencyCommand(
                    entity.getId(), entity.getManagerMerchant(), entity.getManagerCurrency(),
                    entity.getValue(), entity.getDescription(), Status.valueOf(entity.getStatus()));
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(com.kynsoft.finamer.creditcard.infrastructure.services.kafka.consumer.manageMerchant.ConsumerReplicateManageMerchantService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
