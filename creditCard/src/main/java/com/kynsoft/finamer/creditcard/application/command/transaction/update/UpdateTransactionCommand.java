package com.kynsoft.finamer.creditcard.application.command.transaction.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateTransactionCommand implements ICommand {

    private Long id;
    private UUID agency;
    private UUID language;
    private LocalDateTime checkIn;
    private String reservationNumber;
    private String referenceNumber;
    private String hotelContactEmail;

    public static UpdateTransactionCommand fromRequest(UpdateTransactionRequest request, Long id){
        return new UpdateTransactionCommand(
                id, request.getAgency(), request.getLanguage(),
                request.getCheckIn(), request.getReservationNumber(),
                request.getReferenceNumber(), request.getHotelContactEmail()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateTransactionMessage(id);
    }
}
