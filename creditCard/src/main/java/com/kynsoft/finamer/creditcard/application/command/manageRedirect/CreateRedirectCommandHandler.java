package com.kynsoft.finamer.creditcard.application.command.manageRedirect;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.finamer.creditcard.domain.dto.MerchantRedirectResponse;
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

        MerchantRedirectResponse merchantRedirectResponse = formPaymentService.redirectToMerchant(transactionDto, command.getManageMerchantResponse().getMerchantConfigResponse());

        //Obtener la data que viene del FormServiceImpl y dividirla en merchantRequest([0]) y Map ([1])
//        String[] dataForm = split(command.getResult());
        TransactionPaymentLogsDto dto = this.formPaymentService.findByTransactionId(transactionDto.getTransactionUuid());

        if (dto == null) {
            formPaymentService.create(new TransactionPaymentLogsDto(
                    UUID.randomUUID(), transactionDto.getTransactionUuid(), merchantRedirectResponse.getLogData(), null, false, transactionDto.getId())
            );
        } else {
            dto.setMerchantRequest(merchantRedirectResponse.getLogData());
            this.formPaymentService.update(dto);
        }
        command.setResult(merchantRedirectResponse.getRedirectForm());
    }
}


