package com.kynsoft.finamer.creditcard.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
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
    private ManagerBankResponse manageBank;
    private ManageHotelResponse manageHotel;
    private ManagerAccountTypeResponse manageAccountType;
    private String description;

    public ManageBankAccountResponse(ManageBankAccountDto dto){
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.accountNumber = dto.getAccountNumber();
        this.manageBank = dto.getManageBank() != null ? new ManagerBankResponse(dto.getManageBank()) : null;
        this.manageHotel = dto.getManageHotel() != null ? new ManageHotelResponse(dto.getManageHotel()) : null;
        this.manageAccountType = dto.getManageAccountType() != null ? new ManagerAccountTypeResponse(dto.getManageAccountType()) : null;
        this.description = dto.getDescription();
    }
}
