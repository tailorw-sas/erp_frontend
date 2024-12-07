package com.kynsoft.finamer.settings.application.command.manageTradingCompanies.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateManageTradingCompanyKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.ManageCityStateDto;
import com.kynsoft.finamer.settings.domain.dto.ManageTradingCompaniesDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerCountryDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageCityStateService;
import com.kynsoft.finamer.settings.domain.services.IManageTradingCompaniesService;
import com.kynsoft.finamer.settings.domain.services.IManagerCountryService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageTradigCompany.ProducerReplicateManageTradingCompanyService;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Consumer;

@Component
public class UpdateManageTradingCompaniesCommandHandler implements ICommandHandler<UpdateManageTradingCompaniesCommand> {

    private final IManageTradingCompaniesService service;

    private final IManagerCountryService countryService;

    private final IManageCityStateService cityStateService;

    private final ProducerReplicateManageTradingCompanyService producerReplicateManageTradingCompanyService;

    public UpdateManageTradingCompaniesCommandHandler(IManageTradingCompaniesService service,
            IManagerCountryService countryService,
            IManageCityStateService cityStateService,
            ProducerReplicateManageTradingCompanyService producerReplicateManageTradingCompanyService) {
        this.service = service;
        this.countryService = countryService;
        this.cityStateService = cityStateService;
        this.producerReplicateManageTradingCompanyService = producerReplicateManageTradingCompanyService;
    }

    @Override
    public void handle(UpdateManageTradingCompaniesCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Trading Companies ID cannot be null."));

        ManageTradingCompaniesDto dto = service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setDescription, command.getDescription(), dto.getDescription(), update::setUpdate);
        updateStatus(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setCompany, command.getCompany(), dto.getCompany(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setCif, command.getCif(), dto.getCif(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setAddress, command.getAddress(), dto.getAddress(), update::setUpdate);
        updateCountry(dto::setCountry, command.getCountry(), dto.getCountry().getId(), update::setUpdate);
        updateCityState(dto::setCityState, command.getCityState(), dto.getCityState().getId(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setCity, command.getCity(), dto.getCity(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setZipCode, command.getZipCode(), dto.getZipCode(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setInnsistCode, command.getInnsistCode(), dto.getInnsistCode(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(dto::setIsApplyInvoice, command.getIsApplyInvoice(), dto.getIsApplyInvoice(), update::setUpdate);

        if (update.getUpdate() > 0) {
            this.service.update(dto);
            this.producerReplicateManageTradingCompanyService
                    .create(new ReplicateManageTradingCompanyKafka(dto.getId(), dto.getCode(),
                            command.getIsApplyInvoice(), dto.getCif(), dto.getAddress(), dto.getCompany()));
        }
    }

    private boolean updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateLong(Consumer<Long> setter, Long newValue, Long oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateCountry(Consumer<ManagerCountryDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManagerCountryDto countryDto = countryService.findById(newValue);
            setter.accept(countryDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateCityState(Consumer<ManageCityStateDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageCityStateDto cityStateDto = cityStateService.findById(newValue);
            setter.accept(cityStateDto);
            update.accept(1);

            return true;
        }
        return false;
    }
}
