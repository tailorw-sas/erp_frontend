package com.kynsoft.finamer.settings.application.command.manageAgency.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageAgencyKafka;
import com.kynsoft.finamer.settings.domain.dto.*;
import com.kynsoft.finamer.settings.domain.rules.manageAgency.ManageAgencyCodeMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageAgency.ManageAgencyCodeSizeRule;
import com.kynsoft.finamer.settings.domain.rules.manageAgency.ManageAgencyDefaultMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.rules.manageAgency.ManageAgencyNameMustBeNull;
import com.kynsoft.finamer.settings.domain.services.*;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageAgency.ProducerReplicateManageAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateManageAgencyCommandHandler implements ICommandHandler<CreateManageAgencyCommand> {

    private final IManageAgencyService service;
    private final IManagerCountryService countryService;

    private final IManageCityStateService cityStateService;

    private final IManageAgencyTypeService agencyTypeService;

    private final IManagerB2BPartnerService managerB2BPartnerService;
    private final IManagerClientService managerClientService;
    private final ProducerReplicateManageAgencyService producerReplicateManageAgencyService;

    @Autowired
    public CreateManageAgencyCommandHandler(IManageAgencyService service, IManagerCountryService countryService,
            IManageCityStateService cityStateService, IManageAgencyTypeService agencyTypeService,
            IManagerB2BPartnerService managerB2BPartnerService, IManagerClientService managerClientService,
            ProducerReplicateManageAgencyService producerReplicateManageAgencyService) {
        this.service = service;
        this.countryService = countryService;
        this.cityStateService = cityStateService;
        this.agencyTypeService = agencyTypeService;
        this.managerB2BPartnerService = managerB2BPartnerService;
        this.managerClientService = managerClientService;
        this.producerReplicateManageAgencyService = producerReplicateManageAgencyService;
    }

    @Override
    public void handle(CreateManageAgencyCommand command) {
        RulesChecker.checkRule(new ManageAgencyCodeSizeRule(command.getCode()));
        RulesChecker.checkRule(new ManageAgencyNameMustBeNull(command.getName()));
        RulesChecker.checkRule(new ManageAgencyCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));
        if(command.getIsDefault()) {
            RulesChecker.checkRule(new ManageAgencyDefaultMustBeUniqueRule(this.service, command.getId()));
        }
        ManageAgencyTypeDto agencyTypeDto = this.agencyTypeService.findById(command.getAgencyType());
        ManagerCountryDto countryDto = this.countryService.findById(command.getCountry());
        ManageCityStateDto cityStateDto = this.cityStateService.findById(command.getCityState());
        ManagerB2BPartnerDto b2BPartnerDto = this.managerB2BPartnerService.findById(command.getSentB2BPartner());
        ManageClientDto clientDto = this.managerClientService.findById(command.getClient());

        service.create(new ManageAgencyDto(
                command.getId(),
                command.getCode(),
                command.getStatus(),
                command.getName(),
                command.getCif(), command.getAgencyAlias(), command.getAudit(), command.getZipCode(),
                command.getAddress(), command.getMailingAddress(), command.getPhone(), command.getAlternativePhone(),
                command.getEmail(), command.getAlternativeEmail(), command.getContactName(), command.getAutoReconcile(),
                command.getCreditDay(), command.getRfc(), command.getValidateCheckout(),
                command.getBookingCouponFormat(), command.getDescription(), command.getCity(),
                command.getGenerationType(), command.getSentFileFormat(),
                agencyTypeDto,
                clientDto,
                b2BPartnerDto,
                countryDto,
                cityStateDto,
                command.getIsDefault()));

        this.producerReplicateManageAgencyService.create(new ReplicateManageAgencyKafka(command.getId(),
                command.getCode(), command.getName(), command.getClient(), command.getBookingCouponFormat(), 
                command.getStatus().name(), command.getGenerationType().name(), command.getAgencyType(),command.getCif(),command.getAddress(),
                command.getSentB2BPartner(),
                command.getCityState(),
                command.getCountry(),
                command.getMailingAddress()
        ));
    }
}
