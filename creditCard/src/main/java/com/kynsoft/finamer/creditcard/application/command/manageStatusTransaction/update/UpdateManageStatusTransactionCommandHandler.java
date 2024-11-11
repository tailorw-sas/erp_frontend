package com.kynsoft.finamer.creditcard.application.command.manageStatusTransaction.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.CardNetTransactionDataResponse;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.CardNetResponseStatus;
import com.kynsoft.finamer.creditcard.domain.services.*;
import com.kynsoft.finamer.creditcard.infrastructure.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Component
public class UpdateManageStatusTransactionCommandHandler implements ICommandHandler<UpdateManageStatusTransactionCommand> {

    private final ITransactionService transactionService;
    private final IManageStatusTransactionService statusTransactionService;
    private final IManageMerchantConfigService merchantConfigService;
    private final ManageCreditCardTypeServiceImpl creditCardTypeService;
    private final ManageTransactionStatusServiceImpl transactionStatusService;
    private final CardNetJobServiceImpl cardnetJobService;
    private final TransactionPaymentLogsService transactionPaymentLogsService;
    private final ManageMerchantCommissionServiceImpl merchantCommissionService;
    private final IParameterizationService parameterizationService;
    private final IProcessErrorLogService processErrorLogService;

    public UpdateManageStatusTransactionCommandHandler(ITransactionService transactionService, IManageStatusTransactionService statusTransactionService, IManageMerchantConfigService merchantConfigService,
                                                       ManageCreditCardTypeServiceImpl creditCardTypeService, ManageTransactionStatusServiceImpl transactionStatusService, CardNetJobServiceImpl cardnetJobService,
                                                       TransactionPaymentLogsService transactionPaymentLogsService, ManageMerchantCommissionServiceImpl merchantCommissionService, IParameterizationService parameterizationService, IProcessErrorLogService processErrorLogService) {

        this.transactionService = transactionService;
        this.statusTransactionService = statusTransactionService;
        this.merchantConfigService = merchantConfigService;
        this.creditCardTypeService = creditCardTypeService;
        this.transactionStatusService = transactionStatusService;
        this.cardnetJobService = cardnetJobService;
        this.transactionPaymentLogsService = transactionPaymentLogsService;
        this.merchantCommissionService = merchantCommissionService;
        this.parameterizationService = parameterizationService;
        this.processErrorLogService = processErrorLogService;
    }

    @Override
    public void handle(UpdateManageStatusTransactionCommand command) {
        //Obtener toda la informacion necesaria para los updates
        CardnetJobDto cardnetJobDto = statusTransactionService.findBySession(command.getSession());
        TransactionDto transactionDto = transactionService.findByUuid(cardnetJobDto.getTransactionId());
        ManagerMerchantConfigDto merchantConfigDto = merchantConfigService.findByMerchantID(transactionDto.getMerchant().getId());
        String url = merchantConfigDto.getAltUrl() + "/" + cardnetJobDto.getSession() + "?sk=" + cardnetJobDto.getSessionKey();

        //Obtener la informacion del pago
        CompletableFuture<CardNetTransactionDataResponse> futureTransactionResponse = statusTransactionService
                .dataTransactionSuccess(url)
                .toFuture();
        CardNetTransactionDataResponse transactionResponse = null;
        try {
            transactionResponse = futureTransactionResponse.get();  // Obtener de forma bloqueante si es necesario
        } catch (Exception e) {
            throw new BusinessException(DomainErrorMessage.VCC_TRANSACTION_RESULT_CARDNET_ERROR, DomainErrorMessage.VCC_TRANSACTION_RESULT_CARDNET_ERROR.getReasonPhrase());
        }

        if (transactionResponse != null) {
            // Realizar las actualizaciones dependientes de la respuesta
            ManageCreditCardTypeDto creditCardTypeDto = creditCardTypeService.findByFirstDigit(
                    Character.getNumericValue(transactionResponse.getCreditCardNumber().charAt(0))
            );
            ParameterizationDto parameterizationDto = this.parameterizationService.findActiveParameterization();

            //si no encuentra la parametrization que agarre 2 decimales por defecto
            int decimals = parameterizationDto != null ? parameterizationDto.getDecimals() : 2;

            double commission= 0.0;
            try {
                commission = merchantCommissionService.calculateCommission(transactionDto.getAmount(), transactionDto.getMerchant().getId(), creditCardTypeDto.getId(), transactionDto.getCheckIn(), decimals);
            } catch (Exception e) {
                ProcessErrorLogDto processErrorLogDto = new ProcessErrorLogDto();
                processErrorLogDto.setSession(cardnetJobDto.getSession());
                processErrorLogDto.setTransactionId(cardnetJobDto.getTransactionId());
                processErrorLogDto.setError(e.getMessage());
                this.processErrorLogService.create(processErrorLogDto);
            }
            //independientemente del valor de la commission el netAmount tiene dos decimales
            double netAmount = BankerRounding.round(transactionDto.getAmount() - commission, 2);

            //Obtener estado de la transacción correspondiente dado el responseCode del merchant
            CardNetResponseStatus pairedStatus = CardNetResponseStatus.valueOfCode(transactionResponse.getResponseCode());
            ManageTransactionStatusDto transactionStatusDto = transactionStatusService.findByMerchantResponseStatus(pairedStatus.transactionStatus());
            transactionResponse.setMerchantStatus(pairedStatus.toDTO());

            TransactionPaymentLogsDto transactionPaymentLogsDto = transactionPaymentLogsService.findByTransactionId(transactionDto.getTransactionUuid());

            // 1- Actualizar data en vcc_transaction
            transactionDto.setCardNumber(transactionResponse.getCreditCardNumber());
//            transactionDto.setReferenceNumber(transactionResponse.getRetrievalReferenceNumber());
            transactionDto.setCreditCardType(creditCardTypeDto);
            transactionDto.setStatus(transactionStatusDto);
            transactionDto.setCommission(commission);
            transactionDto.setNetAmount(netAmount);
            transactionDto.setPaymentDate(LocalDateTime.now());

            // Guardar la transacción y continuar con las otras operaciones
            transactionService.update(transactionDto);

            // 2- Actualizar data en vcc_cardnet_job
            cardnetJobDto.setIsProcessed(true);
            cardnetJobService.update(cardnetJobDto);

            // 3- Actualizar vcc_transaction_payment_logs con la respuesta del merchant
            transactionPaymentLogsDto.setMerchantResponse(transactionResponse.toString());
            transactionPaymentLogsDto.setIsProcessed(true);
            transactionPaymentLogsService.update(transactionPaymentLogsDto);

            //Enviar correo (voucher) de confirmacion a las personas implicadas
            transactionService.sendTransactionConfirmationVoucherEmail(transactionDto);

            command.setResult(transactionResponse);
        } else {
            throw new BusinessException(DomainErrorMessage.VCC_TRANSACTION_RESULT_CARDNET_ERROR, DomainErrorMessage.VCC_TRANSACTION_RESULT_CARDNET_ERROR.getReasonPhrase());
        }
    }

}
