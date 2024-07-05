package com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.vcc.DeleteManageMerchantHotelEnrolleKafka;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantHotelEnrolleDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantHotelEnrolleService;
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
