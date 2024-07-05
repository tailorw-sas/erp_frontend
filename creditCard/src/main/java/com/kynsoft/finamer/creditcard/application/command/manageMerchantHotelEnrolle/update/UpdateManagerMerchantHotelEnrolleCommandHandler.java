package com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantHotelEnrolleDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantHotelEnrolleService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManagerMerchantHotelEnrolleCommandHandler implements ICommandHandler<UpdateManagerMerchantHotelEnrolleCommand> {

    private final IManageMerchantHotelEnrolleService service;

    public UpdateManagerMerchantHotelEnrolleCommandHandler(IManageMerchantHotelEnrolleService service) {
        this.service = service;
    }

    @Override
    public void handle(UpdateManagerMerchantHotelEnrolleCommand command) {
        ManageMerchantHotelEnrolleDto test = this.service.findById(command.getId());
        this.service.update(test);
    }
}
