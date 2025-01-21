package com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.insis.domain.dto.InnsistConnectionParamsDto;
import com.kynsoft.finamer.insis.domain.dto.InnsistHotelRoomTypeDto;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.rules.innsistConnectionParams.InnsistConnectionParamsWithTradingCompanyIdRule;
import com.kynsoft.finamer.insis.domain.rules.innsistTradingCompanyHotel.TradingCompanyHotelRoomTypePrefixExistsRule;
import com.kynsoft.finamer.insis.domain.rules.innsistTradingCompanyHotel.TradingCompanyHotelRoomTypePrefixSizeRule;
import com.kynsoft.finamer.insis.domain.services.IInnsistConnectionParamsService;
import com.kynsoft.finamer.insis.domain.services.IInnsistHotelRoomTypeService;
import com.kynsoft.finamer.insis.domain.services.IManageHotelService;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ReplicateManageHotelKafka;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageHotel.ProducerReplicateManageHotelService;
import org.springframework.stereotype.Component;

@Component
public class CreateInnsistHotelRoomTypeCommandHandler implements ICommandHandler<CreateInnsistHotelRoomTypeCommand> {

    private final IInnsistHotelRoomTypeService service;
    private final IManageHotelService hotelService;
    private final IInnsistConnectionParamsService connectionParamsService;
    private final ProducerReplicateManageHotelService producerReplicateManageHotelService;

    public CreateInnsistHotelRoomTypeCommandHandler(IInnsistHotelRoomTypeService service,
                                                    IManageHotelService hotelService,
                                                    IInnsistConnectionParamsService connectionParamsService,
                                                    ProducerReplicateManageHotelService producerReplicateManageHotelService){
        this.service = service;
        this.hotelService = hotelService;
        this.connectionParamsService = connectionParamsService;
        this.producerReplicateManageHotelService = producerReplicateManageHotelService;
    }

    @Override
    public void handle(CreateInnsistHotelRoomTypeCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getHotel(), "hotel", "The hotel cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getRoomTypePrefix(), "roomTypePrefix", "Room Type Prefix cannot be null."));
        RulesChecker.checkRule(new TradingCompanyHotelRoomTypePrefixSizeRule(command.getRoomTypePrefix()));

        ManageHotelDto hotelDto = hotelService.findById(command.getHotel());
        RulesChecker.checkRule(new TradingCompanyHotelRoomTypePrefixExistsRule(command.getRoomTypePrefix(), hotelDto.getManageTradingCompany().getId(), service));

        InnsistConnectionParamsDto connectionParamsDto = connectionParamsService.findByTradingCompany(hotelDto.getManageTradingCompany().getId());
        RulesChecker.checkRule(new InnsistConnectionParamsWithTradingCompanyIdRule(connectionParamsDto));

        InnsistHotelRoomTypeDto dto = new InnsistHotelRoomTypeDto(
                command.getId(),
                hotelDto,
                command.getRoomTypePrefix(),
                command.getStatus(),
                null,
                false
        );
        service.create(dto);

        replicateManageHotel(hotelDto, dto);
    }

    private void replicateManageHotel(ManageHotelDto dto, InnsistHotelRoomTypeDto hotelRoomTypeDto){
        ReplicateManageHotelKafka entity = new ReplicateManageHotelKafka(
                dto.getId(),
                dto.getCode(),
                dto.getName(),
                hotelRoomTypeDto.getRoomTypePrefix(),
                dto.getManageTradingCompany().getId()
        );
        producerReplicateManageHotelService.create(entity);
    }
}
