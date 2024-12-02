package com.kynsoft.finamer.creditcard.application.command.transaction.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTransactionRequest {

    private UUID agency;
    private UUID language;
    private LocalDateTime checkIn;
    private String reservationNumber;
    private String referenceNumber;
    private String hotelContactEmail;
    private UUID transactionStatus;
    private Double amount;
    private String guestName;
    private String email;
}
