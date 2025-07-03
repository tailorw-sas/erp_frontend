package com.kynsoft.finamer.insis.application.command.innsistConnectionParams.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.insis.domain.dto.InnsistConnectionParamsDto;
import com.kynsoft.finamer.insis.domain.rules.innsistConnectionParams.InnsistConnectionParamsHostNameRule;
import com.kynsoft.finamer.insis.domain.rules.innsistConnectionParams.InnsistConnectionParamsPortNumberRule;
import com.kynsoft.finamer.insis.domain.services.IInnsistConnectionParamsService;
import com.kynsoft.finamer.insis.domain.services.utils.IEncryptionService;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ReplicateManageConnectionKafka;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.tcaConfigurationProperties.ProducerReplicateManageConnectionService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

@Component
public class CreateConnectionParamsCommandHandler implements ICommandHandler<CreateConnectionParamsCommand> {

    private final IInnsistConnectionParamsService service;
    private final IEncryptionService encryptionService;
    private final ProducerReplicateManageConnectionService producerReplicaManageConnectionService;

    public CreateConnectionParamsCommandHandler(IInnsistConnectionParamsService service,
                                                IEncryptionService encryptionService,
                                                ProducerReplicateManageConnectionService producerReplicaManageConnectionService){
        this.service = service;
        this.encryptionService = encryptionService;
        this.producerReplicaManageConnectionService = producerReplicaManageConnectionService;
    }

    @Override
    public void handle(CreateConnectionParamsCommand command) {
        InnsistConnectionParamsDto connectionParamsDto = getByIdIfExists(command.getId());
        if(Objects.isNull(connectionParamsDto)){
            createInnsistConnection(command);
        }else{
            updateInnsistConnection(connectionParamsDto, command);
        }

    }

    private InnsistConnectionParamsDto getByIdIfExists(UUID id){
        try{
            return service.findById(id);
        }catch (BusinessNotFoundException ex){
            return null;
        }
    }

    private void createInnsistConnection(CreateConnectionParamsCommand command){
        RulesChecker.checkRule(new InnsistConnectionParamsHostNameRule(command.getHostName()));
        RulesChecker.checkRule(new InnsistConnectionParamsPortNumberRule(command.getPortNumber()));

        InnsistConnectionParamsDto dto = new InnsistConnectionParamsDto(
                command.getId(),
                command.getHostName(),
                command.getPortNumber(),
                command.getDataBaseName(),
                encryptionService.encrypt(command.getUserName()),
                encryptionService.encrypt(command.getPassword()),
                command.getDescription(),
                command.getStatus(),
                false,
                null
        );

        service.create(dto);
        replicateInnsistConnection(dto);
    }

    private void updateInnsistConnection(InnsistConnectionParamsDto dto, CreateConnectionParamsCommand command){
        ConsumerUpdate update = new ConsumerUpdate();

        RulesChecker.checkRule(new InnsistConnectionParamsHostNameRule(command.getHostName()));
        RulesChecker.checkRule(new InnsistConnectionParamsPortNumberRule(command.getPortNumber()));

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setHostName, command.getHostName(), dto.getHostName(), update::setUpdate);
        UpdateIfNotNull.updateInteger(dto::setPortNumber, command.getPortNumber(), dto.getPortNumber(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDatabaseName, command.getDataBaseName(), dto.getDatabaseName(), update::setUpdate);

        updateEncryptedField(dto::setUserName, command.getUserName(), encryptionService.decrypt(dto.getUserName()), update::setUpdate);
        updateEncryptedField(dto::setPassword, command.getPassword(), encryptionService.decrypt(dto.getPassword()), update::setUpdate);

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);

        dto.setUpdatedAt(LocalDateTime.now());

        service.update(dto);
        replicateInnsistConnection(dto);
    }

    private void updateEncryptedField(Consumer<String> setter, String newValue, String oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.isEmpty() && !newValue.equals(oldValue)) {
            setter.accept(encryptionService.encrypt(newValue));
            update.accept(1);
        }
    }

    private void replicateInnsistConnection(InnsistConnectionParamsDto dto){
        ReplicateManageConnectionKafka entity = new ReplicateManageConnectionKafka(
                dto.getId(),
                dto.getHostName(),
                String.valueOf(dto.getPortNumber()),
                dto.getDatabaseName(),
                dto.getUserName(),
                dto.getPassword()
        );
        producerReplicaManageConnectionService.create(entity);
    }
}
