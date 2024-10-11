package com.kynsoft.finamer.settings.application.command.manageAgency.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateManageAgencyKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.*;
import com.kynsoft.finamer.settings.domain.rules.manageAgency.ManageAgencyDefaultMustBeUniqueRule;
import com.kynsoft.finamer.settings.domain.services.*;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageAgency.ProducerUpdateManageAgencyService;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateManageAgencyCommandHandler implements ICommandHandler<UpdateManageAgencyCommand> {

    private final IManageAgencyService service;
    private final IManagerCountryService countryService;
    private final IManageCityStateService cityStateService;
    private final IManageAgencyTypeService agencyTypeService;
    private final IManagerB2BPartnerService managerB2BPartnerService;
    private final IManagerClientService managerClientService;
    private final ProducerUpdateManageAgencyService producerUpdateManageAgencyService;

    public UpdateManageAgencyCommandHandler(IManageAgencyService service,
                                            IManagerCountryService countryService,
                                            IManageCityStateService cityStateService,
                                            IManageAgencyTypeService agencyTypeService,
                                            IManagerB2BPartnerService managerB2BPartnerService,
                                            IManagerClientService managerClientService,
                                            ProducerUpdateManageAgencyService producerUpdateManageAgencyService) {
        this.service = service;
        this.countryService = countryService;
        this.cityStateService = cityStateService;
        this.agencyTypeService = agencyTypeService;
        this.managerB2BPartnerService = managerB2BPartnerService;
        this.managerClientService = managerClientService;
        this.producerUpdateManageAgencyService = producerUpdateManageAgencyService;
    }

    @Override
    public void handle(UpdateManageAgencyCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Agency Type ID cannot be null."));
        if(command.getIsDefault()) {
            RulesChecker.checkRule(new ManageAgencyDefaultMustBeUniqueRule(this.service, command.getId()));
        }
        ManageAgencyDto dto = service.findById(command.getId());
        ConsumerUpdate update = new ConsumerUpdate();

        updateFields(dto, command, update);
        updateRelationships(dto, command, update);

        if (update.getUpdate() > 0) {
            service.update(dto);
            producerUpdateManageAgencyService.update(new UpdateManageAgencyKafka(dto.getId(), dto.getName(), dto.getClient().getId(), 
                    dto.getBookingCouponFormat(), command.getStatus().name(),
                    dto.getAgencyType().getId(),dto.getCif(), dto.getAddress(),
                    Objects.nonNull(dto.getSentB2BPartner())?dto.getSentB2BPartner().getId():null,
                    Objects.nonNull(dto.getCityState())?dto.getCityState().getId():null,
                    Objects.nonNull(dto.getCountry())?dto.getCountry().getId():null,
                    dto.getMailingAddress(),
                    dto.getZipCode(),
                    dto.getCity(),
                    dto.getCreditDay(),
                    dto.getAutoReconcile()
            ));
        }
    }

    private void updateFields(ManageAgencyDto dto, UpdateManageAgencyCommand command, ConsumerUpdate update) {
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setCif, command.getCif(), dto.getCif(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setAgencyAlias, command.getAgencyAlias(), dto.getAgencyAlias(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setAudit, command.getAudit(), dto.getAudit(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setZipCode, command.getZipCode(), dto.getZipCode(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setAddress, command.getAddress(), dto.getAddress(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setMailingAddress, command.getMailingAddress(), dto.getMailingAddress(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setPhone, command.getPhone(), dto.getPhone(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setAlternativePhone, command.getAlternativePhone(), dto.getAlternativePhone(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setEmail, command.getEmail(), dto.getEmail(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setAlternativeEmail, command.getAlternativeEmail(), dto.getAlternativeEmail(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setContactName, command.getContactName(), dto.getContactName(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setAutoReconcile, command.getAutoReconcile(), dto.getAutoReconcile(), update::setUpdate);
        UpdateIfNotNull.updateInteger(dto::setCreditDay, command.getCreditDay(), dto.getCreditDay(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setRfc, command.getRfc(), dto.getRfc(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setValidateCheckout, command.getValidateCheckout(), dto.getValidateCheckout(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setBookingCouponFormat, command.getBookingCouponFormat(), dto.getBookingCouponFormat(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setCity, command.getCity(), dto.getCity(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setIsDefault, command.getIsDefault(), dto.getIsDefault(), update::setUpdate);
    }

    private void updateRelationships(ManageAgencyDto dto, UpdateManageAgencyCommand command, ConsumerUpdate update) {
        updateEntity(dto::setAgencyType, command.getAgencyType(), dto.getAgencyType().getId(), agencyTypeService::findById, update::setUpdate);
        updateEntity(dto::setCityState, command.getCityState(), dto.getCityState().getId(), cityStateService::findById, update::setUpdate);
        updateEntity(dto::setClient, command.getClient(), dto.getClient() != null ? dto.getClient().getId() : null, managerClientService::findById, update::setUpdate);
        updateEntity(dto::setCountry, command.getCountry(), dto.getCountry().getId(), countryService::findById, update::setUpdate);
        updateEntity(dto::setSentB2BPartner, command.getSentB2BPartner(), dto.getSentB2BPartner().getId(), managerB2BPartnerService::findById, update::setUpdate);
        updateEnum(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        updateEnum(dto::setGenerationType, command.getGenerationType(), dto.getGenerationType(), update::setUpdate);
        updateEnum(dto::setSentFileFormat, command.getSentFileFormat(), dto.getSentFileFormat(), update::setUpdate);
    }

    private <T> void updateEntity(Consumer<T> setter, UUID newValue, UUID oldValue, EntityFinder<T> finder, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            T entity = finder.findById(newValue);
            setter.accept(entity);
            update.accept(1);
        }
    }

    private <T extends Enum<T>> void updateEnum(Consumer<T> setter, T newValue, T oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
        }
    }

    @FunctionalInterface
    private interface EntityFinder<T> {
        T findById(UUID id);
    }
}