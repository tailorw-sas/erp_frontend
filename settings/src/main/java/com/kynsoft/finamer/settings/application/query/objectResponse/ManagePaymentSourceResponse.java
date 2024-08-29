package com.kynsoft.finamer.settings.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentSourceDto;
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
public class ManagePaymentSourceResponse implements IResponse {

    private UUID id;
    private String code;
    private String description;
    private Status status;
    private String name;
    private Boolean isBank;
    private Boolean expense;

    public ManagePaymentSourceResponse(ManagePaymentSourceDto dto){
        this.id = dto.getId();
        this.code = dto.getCode();
        this.description = dto.getDescription();
        this.status = dto.getStatus();
        this.name = dto.getName();
        this.isBank = dto.getIsBank();
        this.expense = dto.getExpense();
    }
}
