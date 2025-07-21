package com.kynsoft.finamer.insis.application.command.innsistConnectionParams.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.insis.domain.dto.InnsistConnectionParamsDto;
import com.kynsoft.finamer.insis.domain.rules.innsistConnectionParams.InnsistConnectionParamsHostNameRule;
import com.kynsoft.finamer.insis.domain.rules.innsistConnectionParams.InnsistConnectionParamsPortNumberRule;
import com.kynsoft.finamer.insis.domain.services.IInnsistConnectionParamsService;
import com.kynsoft.finamer.insis.domain.services.IInnsistHotelRoomTypeService;
import com.kynsoft.finamer.insis.domain.services.IManageHotelService;
import com.kynsoft.finamer.insis.domain.services.utils.IEncryptionService;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ReplicateManageConnectionKafka;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.tcaConfigurationProperties.ProducerReplicateManageConnectionService;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.tcaConfigurationProperties.ProducerUpdateTcaConnectionPropertiesService;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class UpdateConnectionParamsCommandHandler implements ICommandHandler<UpdateConnectionParamsCommand> {

    private final IInnsistConnectionParamsService service;
    private final IEncryptionService encryptionService;
    private final ProducerUpdateTcaConnectionPropertiesService producerUpdateTcaConnectionService;
    private final IInnsistHotelRoomTypeService tradingCompanyHotelService;
    private final IManageHotelService hotelService;
    private final ProducerReplicateManageConnectionService producerReplicateManageConnectionService;

    public UpdateConnectionParamsCommandHandler(IInnsistConnectionParamsService service, IEncryptionService encryptionService,
                                                ProducerUpdateTcaConnectionPropertiesService producerUpdateTcaConfigurationService,
                                                IInnsistHotelRoomTypeService tradingCompanyHotelService,
                                                IManageHotelService hotelService,
                                                ProducerReplicateManageConnectionService producerReplicateManageConnectionService){
        this.service = service;
        this.encryptionService = encryptionService;
        this.producerUpdateTcaConnectionService = producerUpdateTcaConfigurationService;
        this.tradingCompanyHotelService = tradingCompanyHotelService;
        this.hotelService = hotelService;
        this.producerReplicateManageConnectionService = producerReplicateManageConnectionService;
    }

    @Override
    public void handle(UpdateConnectionParamsCommand command) {
        InnsistConnectionParamsDto dto = service.findById(command.getId());
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

        dto.setUpdatedAt(command.getUpdatedAt());

        service.update(dto);

        ReplicateManageConnectionKafka entity = new ReplicateManageConnectionKafka(
                dto.getId(),
                dto.getHostName(),
                Integer.toString(dto.getPortNumber()),
                dto.getDatabaseName(),
                dto.getUserName(),
                dto.getPassword()
        );
        producerReplicateManageConnectionService.create(entity);
    }

    private boolean updateEncryptedField(Consumer<String> setter, String newValue, String oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.isEmpty() && !newValue.equals(oldValue)) {
            setter.accept(encryptionService.encrypt(newValue));
            update.accept(1);

            return true;
        }
        return false;
    }
}
