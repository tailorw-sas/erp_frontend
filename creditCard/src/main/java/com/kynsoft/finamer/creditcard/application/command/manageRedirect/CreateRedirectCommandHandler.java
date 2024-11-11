package com.kynsoft.finamer.creditcard.application.command.manageRedirect;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionPaymentLogsDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Method;
import com.kynsoft.finamer.creditcard.domain.services.IFormPaymentService;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateRedirectCommandHandler implements ICommandHandler<CreateRedirectCommand> {
    private final ITransactionService transactionService;

    private final IFormPaymentService formPaymentService;

    public CreateRedirectCommandHandler(ITransactionService transactionService, IFormPaymentService formPaymentService) {
        this.transactionService = transactionService;
        this.formPaymentService = formPaymentService;
    }

    @Override
    public void handle(CreateRedirectCommand command) {
        TransactionDto transactionDto = transactionService.findById(command.getRequestDto().getTransactionId());
        // No procesar transacciones completadas
        if (!transactionDto.getStatus().isSentStatus() && !transactionDto.getStatus().isDeclinedStatus()) {
            throw new BusinessException(DomainErrorMessage.MANAGE_TRANSACTION_ALREADY_PROCESSED, DomainErrorMessage.MANAGE_TRANSACTION_ALREADY_PROCESSED.getReasonPhrase());
        }

        command.setResult(formPaymentService.redirectToMerchant(transactionDto, command.getManageMerchantResponse().getMerchantConfigResponse()).getBody());

        //Obtener la data que viene del FormServiceImpl y dividirla en merchantRequest([0]) y Map ([1])
        String[] dataForm = split(command.getResult());
        TransactionPaymentLogsDto dto = this.formPaymentService.findByTransactionId(transactionDto.getTransactionUuid());

        if(dto == null ) {
            if(command.getManageMerchantResponse().getMerchantConfigResponse().getMethod().equals(Method.AZUL.toString()))
            {formPaymentService.create(new TransactionPaymentLogsDto(
                    UUID.randomUUID(), transactionDto.getTransactionUuid(), dataForm[0], null, false, transactionDto.getId())
            );}
            if(command.getManageMerchantResponse().getMerchantConfigResponse().getMethod().equals(Method.CARDNET.toString())){
                formPaymentService.create(new TransactionPaymentLogsDto(
                        UUID.randomUUID(), transactionDto.getTransactionUuid(), dataForm[1], null, false, transactionDto.getId())
                );}
        }
        else{
            if(command.getManageMerchantResponse().getMerchantConfigResponse().getMethod().equals(Method.AZUL.toString())) {
              dto.setMerchantRequest(dataForm[0]);
              this.formPaymentService.update(dto);}
            else{
              dto.setMerchantRequest(dataForm[1]);
              this.formPaymentService.update(dto);}
        }
        command.setResult(dataForm[0]);
    }
    public String[] split(String response){
        String[] spliter = response.split("\\{", 2);
        spliter[1] = "{"+spliter[1];
        return spliter;
    }
}


