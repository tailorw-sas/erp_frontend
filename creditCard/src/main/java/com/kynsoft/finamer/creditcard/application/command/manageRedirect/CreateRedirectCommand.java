package com.kynsoft.finamer.creditcard.application.command.manageRedirect;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageMerchantResponse;
import com.kynsoft.finamer.creditcard.domain.dto.PaymentRequestDto;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateRedirectCommand implements ICommand {

    private PaymentRequestDto requestDto;
    private ManageMerchantResponse manageMerchantResponse;
    private String result;

    @Override
    public ICommandMessage getMessage() {
        return new CreateRedirectCommandMessage(result);
    }
}

