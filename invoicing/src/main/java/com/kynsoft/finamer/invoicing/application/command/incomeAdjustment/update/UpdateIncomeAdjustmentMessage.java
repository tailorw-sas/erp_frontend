package com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.update;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.IncomeResponse;
import com.kynsoft.finamer.invoicing.domain.dto.IncomeDto;
import lombok.Getter;

import lombok.Setter;

@Getter
@Setter
public class UpdateIncomeAdjustmentMessage implements ICommandMessage {

    private final IncomeResponse income;

    public UpdateIncomeAdjustmentMessage(IncomeDto dto) {
        this.income = new IncomeResponse(dto);
    }

}
