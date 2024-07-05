package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.settings.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerAccountTypeDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerBankDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
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
    private Status status;
    private String accountNumber;
    private ManagerBankDto manageBank;
    private ManageHotelDto manageHotel;
    private ManagerAccountTypeDto manageAccountType;
    private String description;

    public ManageBankAccountResponse(ManageBankAccountDto dto){
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.accountNumber = dto.getAccountNumber();
        this.manageBank = dto.getManageBank();
        this.manageHotel = dto.getManageHotel();
        this.manageAccountType = dto.getManageAccountType();
        this.description = dto.getDescription();
    }
}
