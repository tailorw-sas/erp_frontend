package com.kynsoft.finamer.insis.application.command.manageTradingCompany.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.insis.domain.dto.InnsistConnectionParamsDto;
import com.kynsoft.finamer.insis.domain.dto.ManageTradingCompanyDto;
import com.kynsoft.finamer.insis.domain.services.IManageTradingCompanyService;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ReplicateManageTradingCompanyKafka;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageTradingCompany.ProducerReplicateManageTradingCompanyService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Component
public class CreateTradingCompanyCommandHandler implements ICommandHandler<CreateTradingCompanyCommand> {

    private final IManageTradingCompanyService service;
    private final ProducerReplicateManageTradingCompanyService producerReplicateManageTradingCompanyService;

    public CreateTradingCompanyCommandHandler(IManageTradingCompanyService service,
                                              ProducerReplicateManageTradingCompanyService producerReplicateManageTradingCompanyService){
        this.service = service;
        this.producerReplicateManageTradingCompanyService = producerReplicateManageTradingCompanyService;
    }

    @Override
    public void handle(CreateTradingCompanyCommand command) {
        ManageTradingCompanyDto tradingCompanyDto = getTradingCompanyIfExists(command.getId());
        if(Objects.isNull(tradingCompanyDto)){
            createTradingCompany(command);
        }else{
            updateTradingCompany(tradingCompanyDto, command);
        }
    }

    private ManageTradingCompanyDto getTradingCompanyIfExists(UUID id){
        try{
            return service.findById(id);
        }catch (BusinessNotFoundException ex){
            return null;
        }
    }

    private void createTradingCompany(CreateTradingCompanyCommand command){
        ManageTradingCompanyDto dto = new ManageTradingCompanyDto(
                command.getId(),
                command.getCode(),
                command.getCompany(),
                command.getInnsistCode(),
                command.getStatus(),
                null,
                null,
                false);
        service.create(dto);
        replicateTradingCompany(dto);
    }

    private void updateTradingCompany(ManageTradingCompanyDto tradingCompanyDto, CreateTradingCompanyCommand command){
        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(tradingCompanyDto::setInnsistCode, command.getInnsistCode(), tradingCompanyDto.getInnsistCode(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(tradingCompanyDto::setCompany, command.getCompany(), tradingCompanyDto.getCompany(), update::setUpdate);

        tradingCompanyDto.setUpdatedAt(LocalDateTime.now());

        service.update(tradingCompanyDto);
        replicateTradingCompany(tradingCompanyDto);
    }

    private void replicateTradingCompany(ManageTradingCompanyDto dto){
        ReplicateManageTradingCompanyKafka entity = new ReplicateManageTradingCompanyKafka(
                dto.getId(),
                dto.getCode(),
                dto.getCompany(),
                Objects.nonNull(dto.getInnsistConnectionParams()) ? dto.getInnsistConnectionParams().getId() : null
        );
        producerReplicateManageTradingCompanyService.create(entity);
    }
}
