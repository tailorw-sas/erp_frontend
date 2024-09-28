package com.kynsoft.finamer.creditcard.application.command.manageRedirect;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Method;
import com.kynsoft.finamer.creditcard.domain.services.IFormService;
import org.springframework.stereotype.Component;

@Component
public class CreateRedirectCommandHandler implements ICommandHandler<CreateRedirectCommand> {

    private final IFormService formService;

    public CreateRedirectCommandHandler(IFormService formService) {
        this.formService = formService;
    }

    @Override
    public void handle(CreateRedirectCommand command) {
        if (command.getManageMerchantResponse().getMerchantConfigResponse().getMethod().equals(Method.AZUL.toString())) {
            command.setResult(formService.redirectToBlueMerchant(command.getManageMerchantResponse(), command.getRequestDto()).getBody());
        }
        if (command.getManageMerchantResponse().getMerchantConfigResponse().getMethod().equals(Method.CARDNET.toString())) {
            command.setResult(formService.redirectToCardNetMerchant(command.getManageMerchantResponse(), command.getRequestDto()).getBody());
        }
    }
}
