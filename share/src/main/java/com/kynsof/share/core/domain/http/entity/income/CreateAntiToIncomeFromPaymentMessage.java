package com.kynsof.share.core.domain.http.entity.income;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.kynsof.share.core.domain.http.entity.InvoiceHttp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CreateAntiToIncomeFromPaymentMessage implements ICommandMessage, Serializable {

    private List<InvoiceHttp> incomes;

    public CreateAntiToIncomeFromPaymentMessage(List<InvoiceHttp> incomes) {
        this.incomes = incomes;
    }
}
