package com.kynsoft.finamer.insis.application.command.roomRate.createGrouped;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.insis.application.command.roomRate.create.CreateRoomRateCommand;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateGroupedRatesCommand implements ICommand {

    private UUID id;
    private String hotel;
    private LocalDate invoiceDate;
    private String reservationCode;
    private String couponNumber;
    private List<CreateRoomRateCommand> roomRateCommandList;

    public CreateGroupedRatesCommand(UUID id,
                                     String hotel,
                                     LocalDate invoiceDate,
                                     String reservationNumber,
                                     String couponNumber,
                                     List<CreateRoomRateCommand> roomRateCommandList){
        this.id = id;
        this.hotel = hotel;
        this.invoiceDate = invoiceDate;
        this.reservationCode = reservationNumber;
        this.couponNumber = couponNumber;
        this.roomRateCommandList = roomRateCommandList;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateGroupedRatesMessage();
    }
}
