package com.kynsoft.finamer.creditcard.application.command.resendPost;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.services.IFormPaymentService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantConfigService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantHotelEnrolleService;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Transactional
public class ResendPostCommandHandler implements ICommandHandler<ResendPostCommand> {


    private final ITransactionService transactionService;
    private final IManageMerchantConfigService merchantConfigService;
    private final IFormPaymentService formPaymentService;
    private final IManageMerchantHotelEnrolleService merchantHotelEnrolleService;

    public ResendPostCommandHandler(ITransactionService transactionService,
                                    IManageMerchantConfigService merchantConfigService,
                                    IFormPaymentService formPaymentService,
                                    IManageMerchantHotelEnrolleService merchantHotelEnrolleService) {
        this.transactionService = transactionService;
        this.merchantConfigService = merchantConfigService;
        this.formPaymentService = formPaymentService;
        this.merchantHotelEnrolleService = merchantHotelEnrolleService;
    }

    @Override
    @Transactional
    public void handle(ResendPostCommand command) {
        TransactionDto transactionDto = transactionService.findById(command.getId());
        if (!transactionDto.getStatus().isSentStatus() && !transactionDto.getStatus().isDeclinedStatus()) {
            throw new BusinessException(DomainErrorMessage.MANAGE_TRANSACTION_ALREADY_PROCESSED, DomainErrorMessage.MANAGE_TRANSACTION_ALREADY_PROCESSED.getReasonPhrase());
        }
        ManagerMerchantConfigDto merchantConfigDto = merchantConfigService.findByMerchantID(transactionDto.getMerchant().getId());
        ManageMerchantHotelEnrolleDto merchantHotelEnrolleDto = this.merchantHotelEnrolleService.findByForeignIds(transactionDto.getMerchant().getId(), transactionDto.getHotel().getId(), transactionDto.getMerchantCurrency().getManagerCurrency().getId());

        MerchantRedirectResponse merchantRedirectResponse = formPaymentService.redirectToMerchant(transactionDto, merchantConfigDto, merchantHotelEnrolleDto);

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
