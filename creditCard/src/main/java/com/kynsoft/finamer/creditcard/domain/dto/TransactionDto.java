package com.kynsoft.finamer.creditcard.domain.dto;

import com.kynsoft.finamer.creditcard.domain.dtoEnum.MethodType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TransactionDto {

    private Long id;
    private UUID transactionUuid;
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
    private TransactionDto parent;
    private LocalDate transactionDate;
    private ManageVCCTransactionTypeDto transactionCategory;
    private ManageVCCTransactionTypeDto transactionSubCategory;
    private Double netAmount;
    private Boolean permitRefund;
    private ManagerMerchantCurrencyDto merchantCurrency;
    private boolean manual;
    private LocalDateTime paymentDate;

    //uso -> toAggregateParent
    public TransactionDto(
            Long id, UUID transactionUuid, LocalDate checkIn, String reservationNumber,
            String referenceNumber, LocalDate transactionDate) {

        this.id = id;
        this.transactionUuid = transactionUuid;
        this.checkIn = checkIn;
        this.reservationNumber = reservationNumber;
        this.referenceNumber = referenceNumber;
        this.transactionDate = transactionDate;
    }

    //uso -> manual transaction, refund transaction
    public TransactionDto(
            UUID transactionUuid, ManageMerchantDto merchant, MethodType methodType, ManageHotelDto hotel,
            ManageAgencyDto agency, ManageLanguageDto language, Double amount,
            LocalDate checkIn, String reservationNumber, String referenceNumber,
            String hotelContactEmail, String guestName, String email, String enrolleCode,
            String cardNumber, ManageCreditCardTypeDto creditCardType, Double commission,
            ManageTransactionStatusDto status, TransactionDto parent,
            ManageVCCTransactionTypeDto transactionCategory,
            ManageVCCTransactionTypeDto transactionSubCategory, Double netAmount, Boolean permitRefund,
            ManagerMerchantCurrencyDto merchantCurrency, boolean manual) {
        this.transactionUuid = transactionUuid;
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
        this.enrolleCode = enrolleCode;
        this.cardNumber = cardNumber;
        this.creditCardType = creditCardType;
        this.commission = commission;
        this.status = status;
        this.parent = parent;
        this.transactionCategory = transactionCategory;
        this.transactionSubCategory = transactionSubCategory;
        this.netAmount = netAmount;
        this.permitRefund = permitRefund;
        this.merchantCurrency = merchantCurrency;
        this.manual = manual;
    }

    //uso -> adjustment transaction
    public TransactionDto(
            UUID transactionUuid, ManageAgencyDto agency, ManageVCCTransactionTypeDto transactionCategory,
            ManageVCCTransactionTypeDto transactionSubCategory, Double amount,
            String reservationNumber, String referenceNumber, ManageTransactionStatusDto status,
            Double commission, LocalDate checkIn, Double netAmount,
            LocalDate transactionDate, Boolean permitRefund) {
        this.transactionUuid = transactionUuid;
        this.agency = agency;
        this.transactionCategory = transactionCategory;
        this.transactionSubCategory = transactionSubCategory;
        this.amount = amount;
        this.reservationNumber = reservationNumber;
        this.referenceNumber = referenceNumber;
        this.status = status;
        this.commission = commission;
        this.checkIn = checkIn;
        this.netAmount = netAmount;
        this.transactionDate = transactionDate;
        this.permitRefund = permitRefund;
    }
}
