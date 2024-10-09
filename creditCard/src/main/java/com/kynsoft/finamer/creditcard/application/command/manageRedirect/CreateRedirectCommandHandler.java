package com.kynsoft.finamer.creditcard.application.command.manageRedirect;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionPaymentLogsDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Method;
import com.kynsoft.finamer.creditcard.domain.services.IFormPaymentService;
import com.kynsoft.finamer.creditcard.domain.services.IFormService;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateRedirectCommandHandler implements ICommandHandler<CreateRedirectCommand> {
    private final IFormService formService;
    private final ITransactionService transactionService;

    private final IFormPaymentService formPaymentService;

    public CreateRedirectCommandHandler(IFormService formService, ITransactionService transactionService, IFormPaymentService formPaymentService) {
        this.formService = formService;
        this.transactionService = transactionService;
        this.formPaymentService = formPaymentService;
    }

    @Override
    public void handle(CreateRedirectCommand command) {
        if (command.getManageMerchantResponse().getMerchantConfigResponse().getMethod().equals(Method.AZUL.toString())) {
            command.setResult(formService.redirectToBlueMerchant(command.getManageMerchantResponse(), command.getRequestDto()).getBody());
        }
        if (command.getManageMerchantResponse().getMerchantConfigResponse().getMethod().equals(Method.CARDNET.toString())) {
            command.setResult(formService.redirectToCardNetMerchant(command.getManageMerchantResponse(), command.getRequestDto()).getBody());
        }

        TransactionDto transactionDto = transactionService.findById(command.getRequestDto().getTransactionId());

        //Obtener la data que viene del FormServiceImpl y dividirla en merchantRequest([0]) y Map ([1])
        String[] dataForm = split(command.getResult());
        TransactionPaymentLogsDto dto = this.formPaymentService.findByTransactionId(transactionDto.getTransactionUuid());

        if(dto == null ) {
            if(command.getManageMerchantResponse().getMerchantConfigResponse().getMethod().equals(Method.AZUL.toString()))
            {formPaymentService.create(new TransactionPaymentLogsDto(
                    UUID.randomUUID(), transactionDto.getTransactionUuid(), dataForm[0], null)
            );}
            if(command.getManageMerchantResponse().getMerchantConfigResponse().getMethod().equals(Method.CARDNET.toString())){
                formPaymentService.create(new TransactionPaymentLogsDto(
                        UUID.randomUUID(), transactionDto.getTransactionUuid(), dataForm[1], null)
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


