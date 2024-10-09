package com.kynsoft.finamer.invoicing.application.command.manageAgencyContact.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyContactDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRegionDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyContactService;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageRegionService;
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

    public UpdateManageAgencyContactCommandHandler(IManageAgencyContactService agencyContactService, IManageAgencyService agencyService, IManageRegionService regionService, IManageHotelService hotelService) {
        this.agencyContactService = agencyContactService;
        this.agencyService = agencyService;
        this.regionService = regionService;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(UpdateManageAgencyContactCommand command) {

        ManageAgencyContactDto dto = this.agencyContactService.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        updateAgency(dto::setManageAgency, command.getManageAgency(), dto.getManageAgency().getId(), update::setUpdate);
        updateRegion(dto::setManageRegion, command.getManageRegion(), dto.getManageRegion().getId(), update::setUpdate);
        updateHotels(dto::setManageHotel, command.getManageHotel(), dto.getManageHotel().stream().map(ManageHotelDto::getId).collect(Collectors.toList()), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setEmailContact, command.getEmailContact(), dto.getEmailContact(), update::setUpdate);

        if(update.getUpdate() > 0){
            this.agencyContactService.update(dto);
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
