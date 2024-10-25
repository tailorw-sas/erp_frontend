package com.kynsoft.finamer.creditcard.application.query.manageBankReconciliation;

import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantBankAccountDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MerchantBankAccountToReconciliationResponse {


    private UUID id;
    private String accountNumber;
    private String bankCode;
    private String bankName;
    private String description;

    public MerchantBankAccountToReconciliationResponse(ManageMerchantBankAccountDto dto){
        this.id = dto.getId();
        this.accountNumber = dto.getAccountNumber();
        this.bankCode = dto.getManageBank() != null ? dto.getManageBank().getCode() : "";
        this.bankName = dto.getManageBank() != null ? dto.getManageBank().getName() : "";
        this.description = dto.getDescription();
    }
}
