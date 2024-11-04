package com.kynsoft.finamer.creditcard.application.command.transaction.manual;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.MethodType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateManualTransactionRequest {

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
    private UUID merchantCurrency;
}
