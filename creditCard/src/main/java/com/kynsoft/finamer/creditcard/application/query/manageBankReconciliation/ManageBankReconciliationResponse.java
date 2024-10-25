package com.kynsoft.finamer.creditcard.application.query.manageBankReconciliation;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageBankReconciliationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManageBankReconciliationResponse implements IResponse {

    private Long reconciliationId;
    private HotelToBankReconciliationResponse hotel;
    private MerchantBankAccountToReconciliationResponse merchantBankAccount;
    private Double amount;
    private Double detailsAmount;
    private LocalDateTime paidDate;
    private String remark;
    private StatusToReconcileResponse reconcileStatus;

    public ManageBankReconciliationResponse(ManageBankReconciliationDto dto){
        this.reconciliationId = dto.getReconciliationId();
        this.hotel = dto.getHotel() != null ? new HotelToBankReconciliationResponse(dto.getHotel()) : null;
        this.merchantBankAccount = dto.getMerchantBankAccount() != null ? new MerchantBankAccountToReconciliationResponse(dto.getMerchantBankAccount()) : null;
        this.amount = dto.getAmount();
        this.detailsAmount = dto.getDetailsAmount();
        this.paidDate = dto.getPaidDate();
        this.remark = dto.getRemark();
        this.reconcileStatus = dto.getReconcileStatus() != null ? new StatusToReconcileResponse(dto.getReconcileStatus()): null;
    }
}
