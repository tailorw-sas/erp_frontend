package com.kynsoft.finamer.settings.application.command.manageAgencyContact.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ManageAgencyContactKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.settings.domain.dto.*;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyContactService;
import com.kynsoft.finamer.settings.domain.services.IManageAgencyService;
import com.kynsoft.finamer.settings.domain.services.IManageHotelService;
import com.kynsoft.finamer.settings.domain.services.IManageRegionService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageAgencyContact.ProducerUpdateManageAgencyContactService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class UpdateManageAgencyContactCommandHandler implements ICommandHandler<UpdateManageAgencyContactCommand> {

    private final IManageAgencyContactService agencyContactService;

    private final IManageAgencyService agencyService;

    private final IManageRegionService regionService;

    private final IManageHotelService hotelService;

    private final ProducerUpdateManageAgencyContactService producer;

    public UpdateManageAgencyContactCommandHandler(IManageAgencyContactService agencyContactService, IManageAgencyService agencyService, IManageRegionService regionService, IManageHotelService hotelService, ProducerUpdateManageAgencyContactService producer) {
        this.agencyContactService = agencyContactService;
        this.agencyService = agencyService;
        this.regionService = regionService;
        this.hotelService = hotelService;
        this.producer = producer;
    }

    @Override
    public void handle(UpdateManageAgencyContactCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Manage Agency Contact Status ID cannot be null."));

        ManageAgencyContactDto dto = this.agencyContactService.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        updateAgency(dto::setManageAgency, command.getManageAgency(), dto.getManageAgency().getId(), update::setUpdate);
        updateRegion(dto::setManageRegion, command.getManageRegion(), dto.getManageRegion().getId(), update::setUpdate);
        updateHotels(dto::setManageHotel, command.getManageHotel(), dto.getManageHotel().stream().map(ManageHotelDto::getId).collect(Collectors.toList()), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setEmailContact, command.getEmailContact(), dto.getEmailContact(), update::setUpdate);

        if(update.getUpdate() > 0){
            this.agencyContactService.update(dto);
            this.producer.update(new ManageAgencyContactKafka(
                    command.getId(), command.getManageAgency(), command.getManageRegion(),
                    command.getManageHotel(), command.getEmailContact()
            ));
        }
    }

    private void updateAgency(Consumer<ManageAgencyDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageAgencyDto entity = this.agencyService.findById(newValue);
            setter.accept(entity);
            update.accept(1);
        }
    }

    private void updateRegion(Consumer<ManageRegionDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageRegionDto entity = this.regionService.findById(newValue);
            setter.accept(entity);
            update.accept(1);
        }
    }

    private boolean updateHotels(Consumer<List<ManageHotelDto>> setter, List<UUID> newValue, List<UUID> oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            List<ManageHotelDto> manageInvoiceStatusDtoList = this.hotelService.findByIds(newValue);
            setter.accept(manageInvoiceStatusDtoList);
            update.accept(1);

            return true;
        }
        return false;
    }
}
