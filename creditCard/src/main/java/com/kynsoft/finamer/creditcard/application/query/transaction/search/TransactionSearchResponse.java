package com.kynsoft.finamer.creditcard.application.query.transaction.search;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.CustomCodeDescriptionStatusResponse;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageAgencyResponse;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageCreditCardTypeResponse;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.TransactionHotelSearchResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageVCCTransactionTypeDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
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
public class TransactionSearchResponse implements IResponse {

    private Long id;
    private TransactionHotelSearchResponse hotel;
    private CustomCodeDescriptionStatusResponse merchant;
    private Double amount;
    private LocalDate checkIn;
    private String referenceNumber;
    private String enrolleCode;
    private String cardNumber;
    private ManageCreditCardTypeResponse creditCardType;
    private Double commission;
    private ManageSearchTransactionStatusResponse status;
    private TransactionSearchParentResponse parent;
    private Double netAmount;
    private Boolean permitRefund;
    private MethodType methodType;
    private boolean manual;
    private boolean adjustment;
    private ManageAgencyResponse agency;
    private ManageVCCTransactionTypeDto categoryType;
    private ManageVCCTransactionTypeDto subCategoryType;

    public TransactionSearchResponse(TransactionDto dto){
        this.id = dto.getId();
        this.hotel = dto.getHotel() != null ? new TransactionHotelSearchResponse(dto.getHotel()) : null;
        this.merchant = dto.getMerchant() != null ? new CustomCodeDescriptionStatusResponse(dto.getMerchant().getId(), dto.getMerchant().getCode(), dto.getMerchant().getDescription(), dto.getMerchant().getStatus()) : null;
        this.amount = dto.getAmount();
        this.checkIn = dto.getCheckIn();
        this.referenceNumber = dto.getReferenceNumber();
        this.enrolleCode = dto.getEnrolleCode();
        this.cardNumber = dto.getCardNumber();
        this.creditCardType = dto.getCreditCardType() != null ? new ManageCreditCardTypeResponse(dto.getCreditCardType()) : null;
        this.commission = dto.getCommission();
        this.status = dto.getStatus() != null ? new ManageSearchTransactionStatusResponse(dto.getStatus()) :  null;
        this.parent = dto.getParent() != null ? new TransactionSearchParentResponse(dto.getParent().getId()) : null;
        this.netAmount = dto.getNetAmount();
        this.permitRefund = dto.getPermitRefund();
        this.methodType = dto.getMethodType();
        this.manual = dto.isManual();
        this.adjustment = dto.isAdjustment();
        this.agency = dto.getAgency() != null ? new ManageAgencyResponse(dto.getAgency()) : null;
        this.categoryType = dto.getTransactionCategory();
        this.subCategoryType = dto.getTransactionSubCategory();
    }
}
