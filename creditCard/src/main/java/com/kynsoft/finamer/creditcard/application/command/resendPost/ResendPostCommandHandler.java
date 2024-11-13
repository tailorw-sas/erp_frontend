package com.kynsoft.finamer.creditcard.application.command.resendPost;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.creditcard.domain.dto.MerchantRedirectResponse;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.services.IFormPaymentService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantConfigService;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ResendPostCommandHandler implements ICommandHandler<ResendPostCommand> {


    private final ITransactionService transactionService;
    private final IManageMerchantConfigService merchantConfigService;
    private final IFormPaymentService formPaymentService;

    public ResendPostCommandHandler(ITransactionService transactionService, IManageMerchantConfigService merchantConfigService, IFormPaymentService formPaymentService) {

        this.transactionService = transactionService;
        this.merchantConfigService = merchantConfigService;
        this.formPaymentService = formPaymentService;
    }

    @Override
    @Transactional
    public void handle(ResendPostCommand command) {
        TransactionDto transactionDto = transactionService.findById(command.getId());
        if (!transactionDto.getStatus().isSentStatus() && !transactionDto.getStatus().isDeclinedStatus()) {
            throw new BusinessException(DomainErrorMessage.MANAGE_TRANSACTION_ALREADY_PROCESSED, DomainErrorMessage.MANAGE_TRANSACTION_ALREADY_PROCESSED.getReasonPhrase());
        }
        ManagerMerchantConfigDto merchantConfigDto = merchantConfigService.findByMerchantID(transactionDto.getMerchant().getId());
        MerchantRedirectResponse merchantRedirectResponse = formPaymentService.redirectToMerchant(transactionDto, merchantConfigDto);
        command.setResult(merchantRedirectResponse.getRedirectForm());
    }

}
