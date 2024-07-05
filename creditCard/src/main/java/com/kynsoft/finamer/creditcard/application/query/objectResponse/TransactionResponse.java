package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.MethodType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionResponse implements IResponse {

    private Long id;
    private ManageMerchantDto merchant;
    private MethodType methodType;
    private ManageHotelDto hotel;
    private ManageAgencyDto agency;
    private ManageLanguageDto language;
    private Double amount;
    private LocalDate checkIn;
    private String reservationNumber;
    private String referenceNumber;
    private String hotelContactEmail;
    private String guestName;
    private String email;

    private String enrolleCode;
    private String cardNumber;
    private ManageCreditCardTypeDto creditCardType;
    private Double commission;
    private ManageTransactionStatusDto status;
    private TransactionBasicResponse parent;
    private LocalDate transactionDate;
    private Long transactionId;

    public TransactionResponse(TransactionDto dto){
        this.id = dto.getId();
        this.merchant = dto.getMerchant() != null ? dto.getMerchant() : null;
        this.methodType = dto.getMethodType();
        this.hotel = dto.getHotel() != null ? dto.getHotel() : null;
        this.agency = dto.getAgency() != null ? dto.getAgency() : null;
        this.language = dto.getLanguage() != null ? dto.getLanguage() : null;
        this.amount = dto.getAmount();
        this.checkIn = dto.getCheckIn();
        this.reservationNumber = dto.getReservationNumber();
        this.referenceNumber = dto.getReferenceNumber();
        this.hotelContactEmail = dto.getHotelContactEmail();
        this.guestName = dto.getGuestName();
        this.email = dto.getEmail();

        this.enrolleCode = dto.getEnrolleCode();
        this.cardNumber = dto.getCardNumber();
        this.creditCardType = dto.getCreditCardType();
        this.commission = dto.getCommission();
        this.status = dto.getStatus();
        this.parent = dto.getParent() != null ? new TransactionBasicResponse(
                dto.getParent().getId(), dto.getParent().getTransactionDate(),
                dto.getParent().getCheckIn()
        ) : null;
        this.transactionDate = dto.getTransactionDate();
    }
}
