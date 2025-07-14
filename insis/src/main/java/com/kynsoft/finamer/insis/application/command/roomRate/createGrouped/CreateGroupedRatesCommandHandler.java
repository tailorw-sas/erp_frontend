package com.kynsoft.finamer.insis.application.command.roomRate.createGrouped;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.application.services.roomRate.create.CreateRoomRateRequest;
import com.kynsoft.finamer.insis.application.services.roomRate.create.CreateRoomRatesService;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BatchType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class CreateGroupedRatesCommandHandler implements ICommandHandler<CreateGroupedRatesCommand> {

    private final CreateRoomRatesService createRoomRatesService;

    public CreateGroupedRatesCommandHandler(CreateRoomRatesService createRoomRatesService){
        this.createRoomRatesService = createRoomRatesService;
    }

    @Override
    public void handle(CreateGroupedRatesCommand command) {
        List<CreateRoomRateRequest> createRoomRateList = command.getRoomRateCommandList().stream()
                .map(CreateRoomRateRequest::new)
                .collect(Collectors.toList());

        this.createRoomRatesService.createRoomRates(
                command.getId(),
                command.getHotel(),
                command.getInvoiceDate(),
                BatchType.convertToBatchType(command.getBatchType()),
                createRoomRateList
        );
    }

}
