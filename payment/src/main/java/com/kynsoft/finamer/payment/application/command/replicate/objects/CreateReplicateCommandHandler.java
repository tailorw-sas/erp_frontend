package com.kynsoft.finamer.payment.application.command.replicate.objects;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentResourceTypeKafka;
import com.kynsoft.finamer.payment.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.payment.domain.services.IManageResourceTypeService;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.resourceType.ProducerReplicateResourceTypeService;

import org.springframework.stereotype.Component;

@Component
public class CreateReplicateCommandHandler implements ICommandHandler<CreateReplicateCommand> {

   private final IManageResourceTypeService resourceTypeService;
   private final ProducerReplicateResourceTypeService producerReplicateResourceTypeService;

   



    public CreateReplicateCommandHandler(IManageResourceTypeService resourceTypeService,  ProducerReplicateResourceTypeService producerReplicateResourceTypeService) {
    this.resourceTypeService = resourceTypeService;
    this.producerReplicateResourceTypeService = producerReplicateResourceTypeService;
}





    @Override
    public void handle(CreateReplicateCommand command) {
        for (ObjectEnum object : command.getObjects()) {
            switch (object) {
               
                case MANAGE_RESOURCE_TYPE -> {
                    for (ResourceTypeDto paymentSourceDto : this.resourceTypeService.findAllToReplicate()) {
                        this.producerReplicateResourceTypeService.create(new ReplicatePaymentResourceTypeKafka(paymentSourceDto.getId(), paymentSourceDto.getCode(), paymentSourceDto.getName()));
                    }
                }
                
                default -> System.out.println("Número inválido. Por favor, intenta de nuevo con un número del 1 al 7.");
            }
        }
    }
}
