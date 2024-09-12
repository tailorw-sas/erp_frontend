package com.kynsoft.finamer.invoicing.infrastructure.services.kafka.consumer.manageTradingCompany;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.kynsof.share.core.domain.kafka.entity.ReplicateManageTradingCompanyKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;

import com.kynsoft.finamer.invoicing.application.command.manageTradingCompanies.create.CreateManageTradingCompaniesCommand;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ConsumerReplicateManageTradingCompany {

    private final IMediator mediator;

    public ConsumerReplicateManageTradingCompany(IMediator mediator) {

        this.mediator = mediator;
    }

    @KafkaListener(topics = "finamer-replicate-manage-trading-company", groupId = "invoicing-entity-replica")
    public void listen(ReplicateManageTradingCompanyKafka objKafka) {
        try {

            CreateManageTradingCompaniesCommand command = new CreateManageTradingCompaniesCommand(objKafka.getId(),
                    objKafka.getCode(), objKafka.isApplyInvoice(),objKafka.getCif(),objKafka.getAddress(),objKafka.getCompany());
            mediator.send(command);
        } catch (Exception ex) {
            Logger.getLogger(ConsumerReplicateManageTradingCompany.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
