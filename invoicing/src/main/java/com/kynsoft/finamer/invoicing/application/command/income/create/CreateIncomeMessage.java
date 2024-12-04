package com.kynsoft.finamer.invoicing.application.command.income.create;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateIncomeMessage implements ICommandMessage {

    private UUID id;
    private Long invoiceId;
    private String invoiceNumber;

    public CreateIncomeMessage(UUID id, Long invoiceId, String invoiceNumber) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.invoiceNumber = deleteHotelInfo(invoiceNumber);
    }

    private String deleteHotelInfo(String input) {
        return input.replaceAll("-(.*?)-", "-");
    }

}
