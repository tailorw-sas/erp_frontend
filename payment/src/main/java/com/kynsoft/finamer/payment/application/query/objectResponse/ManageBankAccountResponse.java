package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@AllArgsConstructor
@Getter
@Setter
public class ManageBankAccountResponse implements IResponse {

    private UUID id;
    private String accountNumber;
    private String status;
    private String nameOfBank;
    private ManageHotelDto manageHotelDto;

    public ManageBankAccountResponse(ManageBankAccountDto dto) {
        this.id = dto.getId();
        this.accountNumber = dto.getAccountNumber();
        this.status = dto.getStatus();
        this.nameOfBank = dto.getNameOfBank();
        this.manageHotelDto = dto.getManageHotelDto();
    }

    public ManageBankAccountResponse() {
        this.accountNumber="";
        this.nameOfBank="";
        this.status="";
    }
}
