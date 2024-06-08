package com.kynsof.identity.application.command.business.update;

import com.kynsof.identity.domain.dto.BusinessDto;
import com.kynsof.identity.domain.interfaces.service.IBusinessService;
import com.kynsof.identity.domain.interfaces.service.IGeographicLocationService;
import com.kynsof.identity.domain.rules.business.BusinessNameMustBeUniqueRule;
import com.kynsof.identity.domain.rules.business.BusinessRucMustBeUniqueRule;
import com.kynsof.identity.infrastructure.services.kafka.producer.business.ProducerUpdateBusinessEventService;
import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.producer.s3.ProducerDeleteFileEventService;
import com.kynsof.share.core.domain.kafka.producer.s3.ProducerSaveFileEventService;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.UpdateIfNotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UpdateBusinessCommandHandler implements ICommandHandler<UpdateBusinessCommand> {

    private final IBusinessService service;

    private final ProducerUpdateBusinessEventService updateBusinessEventService;

    public UpdateBusinessCommandHandler(IBusinessService service, ProducerUpdateBusinessEventService updateBusinessEventService) {
        this.service = service;
        this.updateBusinessEventService = updateBusinessEventService;
    }

    @Override
    public void handle(UpdateBusinessCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Business ID cannot be null."));

        BusinessDto updateBusiness = this.service.getById(command.getId());

        //Guardo el id del logo actual, para si cambia, mandar a elimianrlo al S3.
        String idLogoDelete = updateBusiness.getLogo();
        UpdateIfNotNull.updateIfNotNull(updateBusiness::setRuc, command.getRuc());
       // RulesChecker.checkRule(new BusinessRucCheckingNumberOfCharactersRule(command.getRuc()));
        RulesChecker.checkRule(new BusinessRucMustBeUniqueRule(this.service, command.getRuc(), command.getId()));

        UpdateIfNotNull.updateIfNotNull(updateBusiness::setDescription, command.getDescription());
        UpdateIfNotNull.updateIfNotNull(updateBusiness::setStatus, command.getStatus());

        UpdateIfNotNull.updateIfNotNull(updateBusiness::setName, command.getName());
        RulesChecker.checkRule(new BusinessNameMustBeUniqueRule(this.service, command.getName(), command.getId()));

        UpdateIfNotNull.updateIfNotNull(updateBusiness::setAddress, command.getAddress());
        UpdateIfNotNull.updateIfNotNull(updateBusiness::setLogo, command.getLogo());

        service.update(updateBusiness);

        //Lanza el evento de integracion
        updateBusinessEventService.update(updateBusiness);
    }

}
