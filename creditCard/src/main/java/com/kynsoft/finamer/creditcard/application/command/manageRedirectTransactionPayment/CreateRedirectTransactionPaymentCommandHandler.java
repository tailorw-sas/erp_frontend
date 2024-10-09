package com.kynsoft.finamer.creditcard.application.command.manageRedirectTransactionPayment;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionPaymentLogsDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.MethodType;
import com.kynsoft.finamer.creditcard.domain.services.IFormPaymentService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantConfigService;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import com.kynsoft.finamer.creditcard.infrastructure.services.TokenService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class CreateRedirectTransactionPaymentCommandHandler implements ICommandHandler<CreateRedirectTransactionPaymentCommand> {

    private final IFormPaymentService formPaymentService;
    private final TokenService tokenService;
    private final ITransactionService transactionService;
    private final IManageMerchantConfigService merchantConfigService;

    public CreateRedirectTransactionPaymentCommandHandler(IFormPaymentService formPaymentService, TokenService tokenService, ITransactionService transactionService, IManageMerchantConfigService merchantConfigService) {
        this.formPaymentService = formPaymentService;
        this.tokenService = tokenService;
        this.transactionService = transactionService;
        this.merchantConfigService = merchantConfigService;
    }

    @Override
    public void handle(CreateRedirectTransactionPaymentCommand command) {

        Claims claims = tokenService.validateToken(command.getToken());
        TransactionDto transactionDto = transactionService.findByUuid(UUID.fromString(claims.get("transactionUuid").toString()));
        ManagerMerchantConfigDto merchantConfigDto = merchantConfigService.findByMerchantID(transactionDto.getMerchant().getId());
        command.setResult(formPaymentService.redirectToLink(transactionDto, merchantConfigDto).getBody());

          TransactionPaymentLogsDto dto = this.formPaymentService.findByTransactionId(transactionDto.getTransactionUuid());
          if(dto == null) {
            formPaymentService.create(new TransactionPaymentLogsDto(
                    UUID.randomUUID(), transactionDto.getTransactionUuid(), command.getResult(), null)
            );}
           else{
                dto.setMerchantRequest(command.getResult());
                this.formPaymentService.update(dto);
           }

    }
}

