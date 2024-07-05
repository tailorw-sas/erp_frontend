package com.kynsoft.finamer.settings.application.command.managerMerchantHotelEnrolle.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.vcc.DeleteManageMerchantHotelEnrolleKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantHotelEnrolleDto;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantHotelEnrolleService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageMerchantHotelEnrolle.ProducerDeleteManageMerchantHotelEnrolleService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManagerMerchantHotelEnrolleCommandHandler implements ICommandHandler<DeleteManagerMerchantHotelEnrolleCommand> {

    private final IManageMerchantHotelEnrolleService service;

    private final ProducerDeleteManageMerchantHotelEnrolleService producerDeleteService;

    public DeleteManagerMerchantHotelEnrolleCommandHandler(IManageMerchantHotelEnrolleService service, ProducerDeleteManageMerchantHotelEnrolleService producerDeleteService) {
        this.service = service;
        this.producerDeleteService = producerDeleteService;
    }

    @Override
    public void handle(DeleteManagerMerchantHotelEnrolleCommand command) {
        ManageMerchantHotelEnrolleDto delete = this.service.findById(command.getId());

        service.delete(delete);
        this.producerDeleteService.delete(new DeleteManageMerchantHotelEnrolleKafka(delete.getId()));
    }

}
