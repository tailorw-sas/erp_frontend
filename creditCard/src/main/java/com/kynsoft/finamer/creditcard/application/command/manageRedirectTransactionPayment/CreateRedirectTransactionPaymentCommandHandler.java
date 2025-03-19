package com.kynsoft.finamer.creditcard.application.command.manageRedirectTransactionPayment;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Method;
import com.kynsoft.finamer.creditcard.domain.services.IFormPaymentService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantConfigService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantHotelEnrolleService;
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
    private final IManageMerchantHotelEnrolleService merchantHotelEnrolleService;

    public CreateRedirectTransactionPaymentCommandHandler(IFormPaymentService formPaymentService,
                                                          TokenService tokenService,
                                                          ITransactionService transactionService,
                                                          IManageMerchantConfigService merchantConfigService,
                                                          IManageMerchantHotelEnrolleService merchantHotelEnrolleService) {
        this.formPaymentService = formPaymentService;
        this.tokenService = tokenService;
        this.transactionService = transactionService;
        this.merchantConfigService = merchantConfigService;
        this.merchantHotelEnrolleService = merchantHotelEnrolleService;
    }

    @Override
    public void handle(CreateRedirectTransactionPaymentCommand command) {
        try {
            Claims claims = tokenService.validateToken(command.getToken());
            TransactionDto transactionDto = transactionService.findByUuid(UUID.fromString(claims.get("transactionUuid").toString()));
            // No procesar transacciones completadas
            if (!transactionDto.getStatus().isSentStatus() && !transactionDto.getStatus().isDeclinedStatus()) {
                throw new BusinessException(DomainErrorMessage.MANAGE_TRANSACTION_ALREADY_PROCESSED, DomainErrorMessage.MANAGE_TRANSACTION_ALREADY_PROCESSED.getReasonPhrase());
            }

            ManagerMerchantConfigDto merchantConfigDto = merchantConfigService.findByMerchantID(transactionDto.getMerchant().getId());
            ManageMerchantHotelEnrolleDto merchantHotelEnrolleDto = this.merchantHotelEnrolleService.findByForeignIds(merchantConfigDto.getId(), transactionDto.getHotel().getId(), transactionDto.getMerchantCurrency().getId());

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
        } catch (io.jsonwebtoken.security.SignatureException ex) {
            throw new BusinessException(DomainErrorMessage.VCC_INVALID_PAYMENT_LINK, DomainErrorMessage.VCC_INVALID_PAYMENT_LINK.getReasonPhrase());
        }
    }
}

