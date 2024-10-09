package com.kynsoft.finamer.invoicing.application.command.manageAgencyContact.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ManageAgencyContactKafka;
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

@Component
public class CreateManageAgencyContactCommandHandler implements ICommandHandler<CreateManageAgencyContactCommand> {

    private final IManageAgencyContactService agencyContactService;

    private final IManageAgencyService agencyService;

    private final IManageRegionService regionService;

    private final IManageHotelService hotelService;

    public CreateManageAgencyContactCommandHandler(IManageAgencyContactService agencyContactService, IManageAgencyService agencyService, IManageRegionService regionService, IManageHotelService hotelService) {
        this.agencyContactService = agencyContactService;
        this.agencyService = agencyService;
        this.regionService = regionService;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(CreateManageAgencyContactCommand command) {
        ManageAgencyDto agencyDto = this.agencyService.findById(command.getManageAgency());
        ManageRegionDto regionDto = this.regionService.findById(command.getManageRegion());
        List<ManageHotelDto> hotelDtoList = this.hotelService.findByIds(command.getManageHotel());

        ManageAgencyContactDto manageAgencyContactDto = this.agencyContactService.create(
                new ManageAgencyContactDto(
                command.getId(), agencyDto, regionDto, hotelDtoList, command.getEmailContact()
            )
        );
    }
}
