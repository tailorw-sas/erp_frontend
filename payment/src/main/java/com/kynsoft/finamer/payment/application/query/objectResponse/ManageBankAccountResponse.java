package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageBankAccountDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageBankAccountResponse implements IResponse {

    private UUID id;
    private String accountNumber;
    private String status;

    public ManageBankAccountResponse(ManageBankAccountDto dto) {
        this.id = dto.getId();
        this.accountNumber = dto.getAccountNumber();
        this.status = dto.getStatus();
    }

}
