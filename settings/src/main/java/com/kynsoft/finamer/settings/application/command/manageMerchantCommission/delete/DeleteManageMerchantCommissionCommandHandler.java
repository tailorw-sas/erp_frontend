package com.kynsoft.finamer.settings.application.command.manageMerchantCommission.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.vcc.DeleteManageMerchantCommissionKafka;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantCommissionDto;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantCommissionService;
import com.kynsoft.finamer.settings.infrastructure.services.kafka.producer.manageMerchantCommission.ProducerDeleteManageMerchantCommissionService;
import org.springframework.stereotype.Component;

@Component
public class DeleteManageMerchantCommissionCommandHandler implements ICommandHandler<DeleteManageMerchantCommissionCommand> {

    private final IManageMerchantCommissionService service;

    private final ProducerDeleteManageMerchantCommissionService producerService;

    public DeleteManageMerchantCommissionCommandHandler(IManageMerchantCommissionService service, ProducerDeleteManageMerchantCommissionService producerService) {
        this.service = service;
        this.producerService = producerService;
    }

    @Override
    public void handle(DeleteManageMerchantCommissionCommand command) {
        ManageMerchantCommissionDto delete = this.service.findById(command.getId());

        this.service.delete(delete);
        this.producerService.delete(new DeleteManageMerchantCommissionKafka(delete.getId()));
    }

}
