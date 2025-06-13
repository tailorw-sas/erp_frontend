package com.kynsoft.finamer.invoicing.application.command.income.create.antiToIncome;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.application.command.income.create.CreateIncomeMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateAntiToIncomeMessage implements ICommandMessage {

    private List<CreateIncomeMessage> createIncomeMessages;

    public CreateAntiToIncomeMessage(List<CreateIncomeMessage> createIncomeMessages){
        this.createIncomeMessages = createIncomeMessages;
    }
}
