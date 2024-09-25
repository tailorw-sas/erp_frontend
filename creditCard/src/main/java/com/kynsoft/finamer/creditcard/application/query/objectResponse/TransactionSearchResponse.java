package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
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
    private Double amount;
    private LocalDate checkIn;
    private String referenceNumber;
    private String enrolleCode;
    private String cardNumber;
    private ManageCreditCardTypeResponse creditCardType;
    private Double commission;
    private ManageTransactionStatusResponse status;
    private TransactionBasicResponse parent;
    private Double netAmount;
    private Boolean permitRefund;




    public TransactionSearchResponse(TransactionDto dto){
        this.id = dto.getId();
        this.hotel = dto.getHotel() != null ? new TransactionHotelSearchResponse(dto.getHotel()) : null;
        this.amount = dto.getAmount();
        this.checkIn = dto.getCheckIn();
        this.referenceNumber = dto.getReferenceNumber();
        this.enrolleCode = dto.getEnrolleCode();
        this.cardNumber = dto.getCardNumber();
        this.creditCardType = dto.getCreditCardType() != null ? new ManageCreditCardTypeResponse(dto.getCreditCardType()) : null;
        this.commission = dto.getCommission();
        this.status = dto.getStatus() != null ? new ManageTransactionStatusResponse(dto.getStatus()) :  null;
        this.parent = dto.getParent() != null ? new TransactionBasicResponse(
                dto.getParent().getId(), dto.getParent().getTransactionDate(),
                dto.getParent().getCheckIn()
        ) : null;
        this.netAmount = dto.getNetAmount();
        this.permitRefund = dto.getPermitRefund();
    }
}
