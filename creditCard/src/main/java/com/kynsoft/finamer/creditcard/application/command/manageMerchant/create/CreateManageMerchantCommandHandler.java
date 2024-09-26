package com.kynsoft.finamer.creditcard.application.command.manageMerchant.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageMerchantResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dto.PaymentRequestDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Method;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.domain.services.IFormService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import com.kynsoft.finamer.creditcard.domain.services.IManagerB2BPartnerService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManagerB2BPartnerDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CreateManageMerchantCommandHandler implements ICommandHandler<CreateManageMerchantCommand> {

    private final IManageMerchantService service;
    private final IFormService formService;
    private final IManagerB2BPartnerService serviceB2BPartner;

    public CreateManageMerchantCommandHandler(IManageMerchantService service, IManagerB2BPartnerService serviceB2BPartner, IFormService formService) {
        this.service = service;
        this.serviceB2BPartner = serviceB2BPartner;
        this.formService = formService;
    }


    @Override
    public void handle(CreateManageMerchantCommand command) {
        ManagerB2BPartnerDto managerB2BPartnerDto = this.serviceB2BPartner.findById(command.getB2bPartner());
        service.create(new ManageMerchantDto(
                command.getId(),
                command.getCode(),
                command.getDescription(),
                managerB2BPartnerDto,
                command.getDefaultm(),
                Status.valueOf(command.getStatus())
        ));
    }

    public ResponseEntity<?> redirect(ManageMerchantResponse response, PaymentRequestDto requestDto, String tokenService) {
        ResponseEntity<String> result = null;
        if (response.getMerchantConfigResponse().getMethod().equals(Method.AZUL.toString())) {
            result = formService.redirectToBlueMerchant(response, requestDto);
        }
        if (response.getMerchantConfigResponse().getMethod().equals(Method.CARDNET.toString())) {
            result = formService.redirectToCardNetMerchant(response, requestDto);
        }
        return ResponseEntity.ok(result);
    }

}