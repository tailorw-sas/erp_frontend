package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantBankAccountDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageMerchantBankAccountResponse implements IResponse {

    private UUID id;
    private ManageMerchantResponse managerMerchant;
    private ManagerBankResponse manageBank;
    private String accountNumber;
    private String description;
    private Status status;
    private Set<CreditCardTypeToMerchantBankAccountResponse> creditCardTypes = new HashSet<>();

    public ManageMerchantBankAccountResponse(ManageMerchantBankAccountDto dto) {
        this.id = dto.getId();
        this.managerMerchant = dto.getManagerMerchant() != null ? new ManageMerchantResponse(dto.getManagerMerchant()) : null;
        this.manageBank = dto.getManageBank() != null ? new ManagerBankResponse(dto.getManageBank()) : null;
        this.accountNumber = dto.getAccountNumber();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        for (ManageCreditCardTypeDto creditCardType : dto.getCreditCardTypes()) {
            this.creditCardTypes.add(new CreditCardTypeToMerchantBankAccountResponse(creditCardType));
        }
    }    
}
