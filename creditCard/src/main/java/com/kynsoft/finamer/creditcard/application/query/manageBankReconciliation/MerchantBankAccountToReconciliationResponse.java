package com.kynsoft.finamer.creditcard.application.query.manageBankReconciliation;

import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantBankAccountDto;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class MerchantBankAccountToReconciliationResponse {


    private UUID id;
    private String accountNumber;
    private String description;
    private Set<MerchantToBankReconciliationResponse> managerMerchant;
    private ManageBankToBankReconciliationResponse manageBank;

    public MerchantBankAccountToReconciliationResponse(ManageMerchantBankAccountDto dto){
        this.id = dto.getId();
        this.accountNumber = dto.getAccountNumber();
        this.description = dto.getDescription();
        this.managerMerchant = dto.getManagerMerchant() != null ? dto.getManagerMerchant().stream().map(MerchantToBankReconciliationResponse::new).collect(Collectors.toSet()) : null;
        this.manageBank = dto.getManageBank() != null ? new ManageBankToBankReconciliationResponse(dto.getManageBank()) : null;
    }
}
