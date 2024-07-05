package com.kynsoft.finamer.creditcard.application.command.manualTransaction.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.MethodType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateManualTransactionCommand implements ICommand {

    private Long id;
    private UUID merchant;
    private MethodType methodType;
    private UUID hotel;
    private UUID agency;
    private UUID language;
    private Double amount;
    private LocalDate checkIn;
    private String reservationNumber;
    private String referenceNumber;
    private String hotelContactEmail;
    private String guestName;
    private String email;

    public CreateManualTransactionCommand(
            UUID merchant, MethodType methodType, UUID hotel, UUID agency, UUID language,
            Double amount, LocalDate checkIn, String reservationNumber, String referenceNumber,
            String hotelContactEmail, String guestName, String email) {

        this.merchant = merchant;
        this.methodType = methodType;
        this.hotel = hotel;
        this.agency = agency;
        this.language = language;
        this.amount = amount;
        this.checkIn = checkIn;
        this.reservationNumber = reservationNumber;
        this.referenceNumber = referenceNumber;
        this.hotelContactEmail = hotelContactEmail;
        this.guestName = guestName;
        this.email = email;
    }

    public static CreateManualTransactionCommand fromRequest(CreateManualTransactionRequest request){
        return new CreateManualTransactionCommand(
                request.getMerchant(), request.getMethodType(), request.getHotel(),
                request.getAgency(), request.getLanguage(), request.getAmount(),
                request.getCheckIn(), request.getReservationNumber(),
                request.getReferenceNumber(), request.getHotelContactEmail(),
                request.getGuestName(), request.getEmail()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManualTransactionMessage(id);
    }
}
