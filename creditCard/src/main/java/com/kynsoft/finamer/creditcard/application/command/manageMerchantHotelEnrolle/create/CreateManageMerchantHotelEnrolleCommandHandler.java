package com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantHotelEnrolleDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageHotelService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantHotelEnrolleService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

@Component
public class CreateManageMerchantHotelEnrolleCommandHandler implements ICommandHandler<CreateManageMerchantHotelEnrolleCommand> {

    private final IManageMerchantHotelEnrolleService serviceMerchantHotelEnrolle;

    private final IManageHotelService hotelService;

    private final IManageMerchantService merchantService;

    public CreateManageMerchantHotelEnrolleCommandHandler(IManageMerchantHotelEnrolleService serviceMerchantHotelEnrolle, IManageHotelService hotelService, IManageMerchantService merchantService) {
        this.serviceMerchantHotelEnrolle = serviceMerchantHotelEnrolle;
        this.hotelService = hotelService;
        this.merchantService = merchantService;
    }

    @Override
    public void handle(CreateManageMerchantHotelEnrolleCommand command) {
        ManageMerchantDto manageMerchantDto = merchantService.findById(command.getManageMerchant());
        ManageHotelDto hotelDto = hotelService.findById(command.getManageHotel());

        serviceMerchantHotelEnrolle.create(new ManageMerchantHotelEnrolleDto(
                                                command.getId(),
                                                manageMerchantDto,
                                                hotelDto,
                                                command.getEnrolle()));
    }
}
