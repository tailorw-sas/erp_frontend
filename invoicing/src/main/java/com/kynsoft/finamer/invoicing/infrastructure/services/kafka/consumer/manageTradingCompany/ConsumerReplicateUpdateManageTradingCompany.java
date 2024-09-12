package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageTradingCompany;

import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageTradingCompanyKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageTradingCompanies.update.UpdateManageTradingCompaniesCommand;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateUpdateManageTradingCompany {

    private final IMediator mediator;

    public ConsumerReplicateUpdateManageTradingCompany(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-update-manage-trading-company", groupId = "invoicing-entity-replica")
    public void listen(UpdateManageTradingCompanyKafka objKafka) {
        try {

            UpdateManageTradingCompaniesCommand command = new UpdateManageTradingCompaniesCommand(
                    objKafka.getId(),
                    objKafka.isApplyInvoice(),objKafka.getCif(),objKafka.getAddress(),objKafka.getCompany());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateUpdateManageTradingCompany.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
