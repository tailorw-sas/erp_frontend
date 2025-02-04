package com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.delete;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.dto.InnsistHotelRoomTypeDto;
import com.kynsoft.finamer.insis.domain.services.IInnsistHotelRoomTypeService;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.DeleteTcaConfigurationPropertiesKafka;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.tcaConfigurationProperties.ProducerDeleteTcaConfigurationService;
import org.springframework.stereotype.Component;

@Component
public class DeleteTradingCompanyHotelCommandHandler implements ICommandHandler<DeleteTradingCompanyHotelCommand> {

    private final IInnsistHotelRoomTypeService service;
    private final ProducerDeleteTcaConfigurationService producerDeleteTcaConfigurationService;


    public DeleteTradingCompanyHotelCommandHandler(IInnsistHotelRoomTypeService service,
                                                   ProducerDeleteTcaConfigurationService producerDeleteTcaConfigurationService){
        this.service = service;
        this.producerDeleteTcaConfigurationService = producerDeleteTcaConfigurationService;
    }

    @Override
    public void handle(DeleteTradingCompanyHotelCommand command) {
        InnsistHotelRoomTypeDto dto = service.findById(command.getId());
        service.delete(dto);

        DeleteTcaConfigurationPropertiesKafka deleteConfiguration = new DeleteTcaConfigurationPropertiesKafka(dto.getId());
        producerDeleteTcaConfigurationService.delete(deleteConfiguration);
    }
}
