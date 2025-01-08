package com.kynsoft.finamer.insis.application.command.innsistConnectionParams.unassingTradingCompany;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.dto.ManageTradingCompanyDto;
import com.kynsoft.finamer.insis.domain.services.IManageTradingCompanyService;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ReplicateManageTradingCompanyKafka;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageTradingCompany.ProducerReplicateManageTradingCompanyService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class UnassingTradingCompanyConnectionCommandHandler implements ICommandHandler<UnassingTradingCompanyConnectionCommand> {

    private final IManageTradingCompanyService service;
    private final ProducerReplicateManageTradingCompanyService producerReplicateManageTradingCompanyService;

    public UnassingTradingCompanyConnectionCommandHandler(IManageTradingCompanyService service,
                                                          ProducerReplicateManageTradingCompanyService producerReplicateManageTradingCompanyService){
        this.service = service;
        this.producerReplicateManageTradingCompanyService = producerReplicateManageTradingCompanyService;
    }
    @Override
    public void handle(UnassingTradingCompanyConnectionCommand command) {
        ManageTradingCompanyDto tradingCompany = service.findById(command.getTradingCompanyId());

        tradingCompany.setInnsistConnectionParams(null);
        tradingCompany.setHasConnection(false);

        service.update(tradingCompany);

        replicateManageTradingCompany(tradingCompany);
    }

    private void replicateManageTradingCompany(ManageTradingCompanyDto dto){
        ReplicateManageTradingCompanyKafka entity = new ReplicateManageTradingCompanyKafka(
                dto.getId(),
                dto.getCode(),
                dto.getCompany(),
                null
        );
        producerReplicateManageTradingCompanyService.create(entity);
    }
}
