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
    private ManageMerchantResponse merchant;
    private MethodType methodType;
    private ManageHotelResponse hotel;
    private ManageAgencyResponse agency;
    private ManageLanguageResponse language;
    private Double amount;
    private LocalDate checkIn;
    private String reservationNumber;
    private String referenceNumber;
    private String hotelContactEmail;
    private String guestName;
    private String email;

    private String enrolleCode;
    private String cardNumber;
    private ManageCreditCardTypeResponse creditCardType;
    private Double commission;
    private ManageTransactionStatusResponse status;
    private TransactionBasicResponse parent;
    private LocalDate transactionDate;
    private ManageVCCTransactionTypeResponse transactionCategory;
    private ManageVCCTransactionTypeResponse transactionSubCategory;
    private Double netAmount;

    public TransactionResponse(TransactionDto dto){
        this.id = dto.getId();
        this.merchant = dto.getMerchant() != null ? new ManageMerchantResponse(dto.getMerchant()) : null;
        this.methodType = dto.getMethodType();
        this.hotel = dto.getHotel() != null ? new ManageHotelResponse(dto.getHotel()) : null;
        this.agency = dto.getAgency() != null ? new ManageAgencyResponse(dto.getAgency()) : null;
        this.language = dto.getLanguage() != null ? new ManageLanguageResponse(dto.getLanguage()) : null;
        this.amount = dto.getAmount();
        this.checkIn = dto.getCheckIn();
        this.reservationNumber = dto.getReservationNumber();
        this.referenceNumber = dto.getReferenceNumber();
        this.hotelContactEmail = dto.getHotelContactEmail();
        this.guestName = dto.getGuestName();
        this.email = dto.getEmail();

        this.enrolleCode = dto.getEnrolleCode();
        this.cardNumber = dto.getCardNumber();
        this.creditCardType = dto.getCreditCardType() != null ? new ManageCreditCardTypeResponse(dto.getCreditCardType()) : null;
        this.commission = dto.getCommission();
        this.status = dto.getStatus() != null ? new ManageTransactionStatusResponse(dto.getStatus()) :  null;
        this.parent = dto.getParent() != null ? new TransactionBasicResponse(
                dto.getParent().getId(), dto.getParent().getTransactionDate(),
                dto.getParent().getCheckIn()
        ) : null;
        this.transactionDate = dto.getTransactionDate();
        this.transactionCategory = dto.getTransactionCategory() != null ? new ManageVCCTransactionTypeResponse(dto.getTransactionCategory()) : null;
        this.transactionSubCategory = dto.getTransactionSubCategory() != null ? new ManageVCCTransactionTypeResponse(dto.getTransactionSubCategory()) : null;
        this.netAmount = dto.getNetAmount();
    }
}
