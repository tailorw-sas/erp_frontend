package com.kynsof.share.core.domain.http.entity.income;

import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import java.io.Serializable;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CreateIncomeFromPaymentMessage implements ICommandMessage, Serializable {

    private UUID id;
    private Long invoiceId;
    private String invoiceNumber;

    public CreateIncomeFromPaymentMessage(UUID id, Long invoiceId, String invoiceNumber) {
        this.id = id;
        this.invoiceId = invoiceId;
        this.invoiceNumber = deleteHotelInfo(invoiceNumber);
    }

    private String deleteHotelInfo(String input) {
        return input.replaceAll("-(.*?)-", "-");
    }

}
