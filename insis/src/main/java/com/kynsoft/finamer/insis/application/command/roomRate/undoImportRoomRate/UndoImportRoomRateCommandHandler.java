package com.kynsoft.finamer.insis.application.command.roomRate.undoImportRoomRate;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsoft.finamer.insis.domain.dto.BookingDto;
import com.kynsoft.finamer.insis.domain.dto.RoomRateDto;
import com.kynsoft.finamer.insis.domain.services.IBookingService;
import com.kynsoft.finamer.insis.domain.services.IRoomRateService;
import com.kynsoft.finamer.insis.infrastructure.model.enums.BookingStatus;
import com.kynsoft.finamer.insis.infrastructure.model.enums.RoomRateStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UndoImportRoomRateCommandHandler implements ICommandHandler<UndoImportRoomRateCommand> {

    private final IRoomRateService roomRateService;

    public UndoImportRoomRateCommandHandler(IRoomRateService roomRateService){
        this.roomRateService = roomRateService;
    }


    @Override
    public void handle(UndoImportRoomRateCommand command) {

        List<RoomRateDto> roomRates = roomRateService.findAllByInvoiceId(command.getInvoiceId());

        roomRates.forEach(roomRate -> {
            roomRate.setUpdatedAt(LocalDateTime.now());
            roomRate.setStatus(RoomRateStatus.PENDING);
        });

        roomRateService.updateMany(roomRates);
    }
}
