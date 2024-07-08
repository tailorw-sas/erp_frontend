package com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantHotelEnrolleDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageHotelService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantHotelEnrolleService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManagerMerchantHotelEnrolleCommandHandler implements ICommandHandler<UpdateManagerMerchantHotelEnrolleCommand> {

    private final IManageMerchantHotelEnrolleService service;

    private final IManageMerchantService merchantService;

    private final IManageHotelService hotelService;

    public UpdateManagerMerchantHotelEnrolleCommandHandler(IManageMerchantHotelEnrolleService service, IManageMerchantService merchantService, IManageHotelService hotelService) {
        this.service = service;
        this.merchantService = merchantService;
        this.hotelService = hotelService;
    }

    @Override
    public void handle(UpdateManagerMerchantHotelEnrolleCommand command) {
        ManageMerchantHotelEnrolleDto test = this.service.findById(command.getId());
        ManageMerchantDto merchantDto = this.merchantService.findById(command.getManagerMerchant());
        ManageHotelDto hotelDto = this.hotelService.findById(command.getManagerHotel());
        test.setManageMerchant(merchantDto);
        test.setManageHotel(hotelDto);
        test.setEnrolle(command.getEnrolle());
        this.service.update(test);
    }
}
