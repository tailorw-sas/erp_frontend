package com.kynsof.identity.application.command.business.create;

import com.kynsof.identity.domain.dto.BusinessDto;
import com.kynsof.identity.domain.dto.GeographicLocationDto;
import com.kynsof.identity.domain.dto.enumType.EBusinessStatus;
import com.kynsof.identity.domain.interfaces.service.IBusinessService;
import com.kynsof.identity.domain.interfaces.service.IGeographicLocationService;
import com.kynsof.identity.domain.rules.business.BusinessNameMustBeUniqueRule;
import com.kynsof.identity.domain.rules.business.BusinessRucCheckingNumberOfCharactersRule;
import com.kynsof.identity.domain.rules.business.BusinessRucMustBeUniqueRule;
import com.kynsof.identity.infrastructure.services.kafka.producer.business.ProducerCreateBusinessEventService;
import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.producer.s3.ProducerSaveFileEventService;
import com.kynsof.share.utils.ConfigureTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateBusinessCommandHandler implements ICommandHandler<CreateBusinessCommand> {

    private final IBusinessService service;

    @Autowired
    private ProducerSaveFileEventService saveFileEventService;

    @Autowired
    private IGeographicLocationService geographicLocationService;

    @Autowired
    private ProducerCreateBusinessEventService createBusinessEventService;

    public CreateBusinessCommandHandler(IBusinessService service) {
        this.service = service;
    }

    @Override
    public void handle(CreateBusinessCommand command) {

        RulesChecker.checkRule(new BusinessRucCheckingNumberOfCharactersRule(command.getRuc()));
        RulesChecker.checkRule(new BusinessRucMustBeUniqueRule(this.service, command.getRuc(), command.getId()));
        RulesChecker.checkRule(new BusinessNameMustBeUniqueRule(this.service, command.getName(), command.getId()));

        GeographicLocationDto location = this.geographicLocationService.findById(command.getGeographicLocation());
       // UUID idLogo = UUID.randomUUID();

        BusinessDto create = new BusinessDto(
                command.getId(),
                command.getName(),
                command.getLatitude(),
                command.getLongitude(),
                command.getDescription(),
                command.getImage(),
                command.getRuc(),
                EBusinessStatus.ACTIVE,
                location,
                command.getAddress()
        );

        create.setCreateAt(ConfigureTimeZone.getTimeZone());
        service.create(create);
        createBusinessEventService.create(create);

//        if (command.getImage() != null) {
//            FileKafka fileSave = new FileKafka(idLogo, "identity", UUID.randomUUID().toString(),
//                    command.getImage());
//            saveFileEventService.create(fileSave);
//        }

    }
}
