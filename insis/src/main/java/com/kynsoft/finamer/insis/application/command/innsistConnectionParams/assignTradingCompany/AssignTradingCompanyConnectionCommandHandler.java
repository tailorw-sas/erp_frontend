package com.kynsoft.finamer.insis.application.command.innsistConnectionParams.assignTradingCompany;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.dto.InnsistConnectionParamsDto;
import com.kynsoft.finamer.insis.domain.dto.ManageTradingCompanyDto;
import com.kynsoft.finamer.insis.domain.services.IInnsistConnectionParamsService;
import com.kynsoft.finamer.insis.domain.services.IInnsistHotelRoomTypeService;
import com.kynsoft.finamer.insis.domain.services.IManageHotelService;
import com.kynsoft.finamer.insis.domain.services.IManageTradingCompanyService;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ReplicateManageTradingCompanyKafka;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageTradingCompany.ProducerReplicateManageTradingCompanyService;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.tcaConfigurationProperties.ProducerReplicateManageConnectionService;
import org.springframework.stereotype.Component;

@Component
public class AssignTradingCompanyConnectionCommandHandler implements ICommandHandler<AssignTradingCompanyConnectionCommand> {

    private final IManageTradingCompanyService service;
    private final IInnsistConnectionParamsService tradingCompanyService;
    private final ProducerReplicateManageConnectionService producerReplicaTcaConfigurationService;
    private final IManageHotelService hotelService;
    private final IInnsistHotelRoomTypeService tradingCompanyHotelService;
    private final ProducerReplicateManageTradingCompanyService producerReplicateManageTradingCompanyService;

    public AssignTradingCompanyConnectionCommandHandler(IInnsistConnectionParamsService tradingCompanyService,
                                                        IManageTradingCompanyService service,
                                                        IManageHotelService hotelService,
                                                        ProducerReplicateManageConnectionService producerReplicaTcaConfigurationService,
                                                        IInnsistHotelRoomTypeService tradingCompanyHotelService,
                                                        ProducerReplicateManageTradingCompanyService producerReplicateManageTradingCompanyService){
        this.service = service;
        this.tradingCompanyService = tradingCompanyService;
        this.hotelService = hotelService;
        this.producerReplicaTcaConfigurationService = producerReplicaTcaConfigurationService;
        this.tradingCompanyHotelService = tradingCompanyHotelService;
        this.producerReplicateManageTradingCompanyService = producerReplicateManageTradingCompanyService;
    }

    @Override
    public void handle(AssignTradingCompanyConnectionCommand command) {
        ManageTradingCompanyDto tradingCompanyDto = service.findById(command.getTradingCompanyId());
        InnsistConnectionParamsDto connectionParamsDto = tradingCompanyService.findById(command.getConnectionParmId());

        tradingCompanyDto.setInnsistConnectionParams(connectionParamsDto);
        tradingCompanyDto.setHasConnection(true);

        service.update(tradingCompanyDto);

        ReplicateManageTradingCompanyKafka entity = new ReplicateManageTradingCompanyKafka(
                tradingCompanyDto.getId(),
                tradingCompanyDto.getCode(),
                tradingCompanyDto.getCompany(),
                tradingCompanyDto.getInnsistConnectionParams().getId()
        );
        producerReplicateManageTradingCompanyService.create(entity);
    }
}
