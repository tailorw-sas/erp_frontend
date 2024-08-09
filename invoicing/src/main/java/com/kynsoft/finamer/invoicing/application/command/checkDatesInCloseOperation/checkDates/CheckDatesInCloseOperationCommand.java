package com.kynsoft.finamer.invoicing.application.command.checkDatesInCloseOperation.checkDates;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CheckDatesInCloseOperationCommand implements ICommand {

    private UUID hotelId;
    private List<LocalDate> dates;

    public static CheckDatesInCloseOperationCommand fromRequest(CheckDatesInCloseOperationRequest request){
        return new CheckDatesInCloseOperationCommand(
                request.getHotelId(), request.getDates()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CheckDatesInCloseOperationMessage();
    }
}
