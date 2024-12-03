package com.kynsoft.finamer.creditcard.application.command.manageStatusTransactionBlue.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.services.*;
import com.kynsoft.finamer.creditcard.infrastructure.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UpdateManageStatusTransactionBlueCommandHandler implements ICommandHandler<UpdateManageStatusTransactionBlueCommand> {

    private final ITransactionService transactionService;
    private final ManageCreditCardTypeServiceImpl creditCardTypeService;
    private final ManageTransactionStatusServiceImpl transactionStatusService;
    private final TransactionPaymentLogsService transactionPaymentLogsService;
    private final ManageMerchantCommissionServiceImpl merchantCommissionService;
    private final IParameterizationService parameterizationService;
    private final IProcessErrorLogService processErrorLogService;
    private final ITransactionStatusHistoryService transactionStatusHistoryService;
    private final IManageMerchantConfigService merchantConfigService;

    public UpdateManageStatusTransactionBlueCommandHandler(ITransactionService transactionService, ManageCreditCardTypeServiceImpl creditCardTypeService,
                                                           ManageTransactionStatusServiceImpl transactionStatusService,
                                                           TransactionPaymentLogsService transactionPaymentLogsService,
                                                           ManageMerchantCommissionServiceImpl merchantCommissionService, IParameterizationService parameterizationService, IProcessErrorLogService processErrorLogService, ITransactionStatusHistoryService transactionStatusHistoryService, IManageMerchantConfigService merchantConfigService) {
        this.transactionService = transactionService;
        this.creditCardTypeService = creditCardTypeService;
        this.transactionStatusService = transactionStatusService;
        this.transactionPaymentLogsService = transactionPaymentLogsService;
        this.merchantCommissionService = merchantCommissionService;
        this.parameterizationService = parameterizationService;
        this.processErrorLogService = processErrorLogService;
        this.transactionStatusHistoryService = transactionStatusHistoryService;
        this.merchantConfigService = merchantConfigService;
    }

    @Override
    public void handle(UpdateManageStatusTransactionBlueCommand command) {
        TransactionDto transactionDto = transactionService.findById(command.getRequest().getOrderNumber());
        if (!transactionDto.getStatus().isSentStatus() && !transactionDto.getStatus().isDeclinedStatus()) {
            throw new BusinessException(DomainErrorMessage.MANAGE_TRANSACTION_ALREADY_PROCESSED, DomainErrorMessage.MANAGE_TRANSACTION_ALREADY_PROCESSED.getReasonPhrase());
        }
        ManageCreditCardTypeDto creditCardTypeDto = creditCardTypeService.findByFirstDigit(Character.getNumericValue(command.getRequest().getCardNumber().charAt(0)));
        ManageTransactionStatusDto transactionStatusDto = transactionStatusService.findByMerchantResponseStatus(command.getRequest().getStatus());
        TransactionPaymentLogsDto transactionPaymentLogsDto = transactionPaymentLogsService.findByTransactionId(transactionDto.getTransactionUuid());
        ParameterizationDto parameterizationDto = this.parameterizationService.findActiveParameterization();

        //si no encuentra la parametrization que agarre 2 decimales por defecto
        int decimals = parameterizationDto != null ? parameterizationDto.getDecimals() : 2;

        double commission= 0.0;
        try {
            commission = merchantCommissionService.calculateCommission(transactionDto.getAmount(), transactionDto.getMerchant().getId(), creditCardTypeDto.getId(), transactionDto.getCheckIn().toLocalDate(), decimals);
        } catch (Exception e) {
            ProcessErrorLogDto processErrorLogDto = new ProcessErrorLogDto();
            processErrorLogDto.setTransactionId(transactionDto.getTransactionUuid());
            processErrorLogDto.setError(e.getMessage());
            this.processErrorLogService.create(processErrorLogDto);
        }
        //independientemente del valor de la commission el netAmount tiene dos decimales
        double netAmount = BankerRounding.round(transactionDto.getAmount() - commission, 2);

        //Comenzar a actualizar lo referente a la transaccion en las diferntes tablas
        //1- Actualizar data in vcc_transaction
        transactionDto.setCardNumber(command.getRequest().getCardNumber());
        transactionDto.setCreditCardType(creditCardTypeDto);
        transactionDto.setCommission(commission);
        transactionDto.setNetAmount(netAmount);
        transactionDto.setPaymentDate(command.getRequest().getPaymentDate());
        transactionDto.setStatus(transactionStatusDto);
        if (transactionStatusDto.isReceivedStatus()){
            transactionDto.setTransactionDate(LocalDateTime.now());
        }
        this.transactionService.update(transactionDto);
        this.transactionStatusHistoryService.create(new TransactionStatusHistoryDto(
                UUID.randomUUID(),
                transactionDto,
                "The transaction change to "+transactionStatusDto.getCode() + "-" +transactionStatusDto.getName()+".",
                null,
                command.getEmployee(),
                transactionStatusDto
        ));
        //3- Actualizar vcc_transaction_payment_logs columna merchant_respose en vcc_transaction
        transactionPaymentLogsDto.setMerchantResponse(command.getRequest().getMerchantResponse());
        transactionPaymentLogsDto.setIsProcessed(true);
        this.transactionPaymentLogsService.update(transactionPaymentLogsDto);

        ManagerMerchantConfigDto merchantConfigDto = merchantConfigService.findByMerchantID(transactionDto.getMerchant().getId());
        //Enviar correo (voucher) de confirmacion a las personas implicadas
        transactionService.sendTransactionConfirmationVoucherEmail(transactionDto, merchantConfigDto);

    }
}
