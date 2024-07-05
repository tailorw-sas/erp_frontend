package com.kynsoft.finamer.settings.application.command.managerMerchantHotelEnrolle.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantHotelEnrolleDto;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantHotelEnrolleService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerMerchantHotelEnrolleCommandHandler implements ICommandHandler<DeleteManagerMerchantHotelEnrolleCommand> {

    private final IManageMerchantHotelEnrolleService service;

    public DeleteManagerMerchantHotelEnrolleCommandHandler(IManageMerchantHotelEnrolleService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteManagerMerchantHotelEnrolleCommand command) {
        ManageMerchantHotelEnrolleDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
