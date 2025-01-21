package com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.insis.domain.dto.InnsistHotelRoomTypeDto;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.rules.innsistTradingCompanyHotel.TradingCompanyHotelRoomTypePrefixExistsRule;
import com.kynsoft.finamer.insis.domain.rules.innsistTradingCompanyHotel.TradingCompanyHotelRoomTypePrefixSizeRule;
import com.kynsoft.finamer.insis.domain.services.IInnsistHotelRoomTypeService;
import com.kynsoft.finamer.insis.domain.services.IManageHotelService;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ReplicateManageHotelKafka;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageHotel.ProducerReplicateManageHotelService;
import org.springframework.stereotype.Component;

@Component
public class UpdateInnsistHotelRoomTypeCommandHandler implements ICommandHandler<UpdateInnsistHotelRoomTypeCommand> {

    private final IInnsistHotelRoomTypeService service;
    private final IManageHotelService manageHotelService;
    private final ProducerReplicateManageHotelService producerReplicateManageHotelService;

    public UpdateInnsistHotelRoomTypeCommandHandler(IInnsistHotelRoomTypeService service,
                                                    IManageHotelService manageHotelService,
                                                    ProducerReplicateManageHotelService producerReplicateManageHotelService){
        this.service = service;
        this.manageHotelService = manageHotelService;
        this.producerReplicateManageHotelService = producerReplicateManageHotelService;
    }

    @Override
    public void handle(UpdateInnsistHotelRoomTypeCommand command) {
        InnsistHotelRoomTypeDto dto = service.findById(command.getId());

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getHotel(), "hotel", "The hotel cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getRoomTypePrefix(), "roomTypePrefix", "Room Type Prefix cannot be null."));
        RulesChecker.checkRule(new TradingCompanyHotelRoomTypePrefixSizeRule(command.getRoomTypePrefix()));

        ManageHotelDto hotelDto = manageHotelService.findById(command.getHotel());
        RulesChecker.checkRule(new TradingCompanyHotelRoomTypePrefixExistsRule(command.getRoomTypePrefix(), hotelDto.getManageTradingCompany().getId(), service));

        ConsumerUpdate update = new ConsumerUpdate();

        dto.setHotel(hotelDto);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setRoomTypePrefix, command.getRoomTypePrefix(), dto.getRoomTypePrefix(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);
        service.update(dto);

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
