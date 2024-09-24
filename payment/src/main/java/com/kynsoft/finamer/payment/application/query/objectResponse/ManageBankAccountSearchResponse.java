package com.kynsoft.finamer.payment.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageBankAccountDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@AllArgsConstructor
@Getter
@Setter
public class ManageBankAccountSearchResponse implements IResponse {

    private UUID id;
    private String accountNumber;
    private String nameOfBank;
    private ManageHotelSearchResponse manageHotelDto;

    public ManageBankAccountSearchResponse(ManageBankAccountDto dto) {
        this.id = dto.getId();
        this.accountNumber = dto.getAccountNumber();
        this.nameOfBank = dto.getNameOfBank();
        this.manageHotelDto = dto.getManageHotelDto() != null ? new ManageHotelSearchResponse(dto.getManageHotelDto()) : null;
    }

    public ManageBankAccountSearchResponse() {
        this.accountNumber="";
        this.nameOfBank="";
    }
}
