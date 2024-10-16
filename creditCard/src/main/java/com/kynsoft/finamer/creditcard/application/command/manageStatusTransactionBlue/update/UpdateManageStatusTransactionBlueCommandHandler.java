package com.kynsoft.finamer.creditcard.application.command.manageStatusTransactionBlue.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import com.kynsoft.finamer.creditcard.infrastructure.services.*;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageStatusTransactionBlueCommandHandler implements ICommandHandler<UpdateManageStatusTransactionBlueCommand> {

    private final ITransactionService transactionService;
    private final ManageCreditCardTypeServiceImpl creditCardTypeService;
    private final ManageTransactionStatusServiceImpl transactionStatusService;
    private final TransactionPaymentLogsService transactionPaymentLogsService;
    private final ManageMerchantCommissionServiceImpl merchantCommissionService;
    public UpdateManageStatusTransactionBlueCommandHandler(ITransactionService transactionService, ManageCreditCardTypeServiceImpl creditCardTypeService,
                                                           ManageTransactionStatusServiceImpl transactionStatusService,
                                                           TransactionPaymentLogsService transactionPaymentLogsService,
                                                           ManageMerchantCommissionServiceImpl merchantCommissionService) {
        this.transactionService = transactionService;
        this.creditCardTypeService = creditCardTypeService;
        this.transactionStatusService = transactionStatusService;
        this.transactionPaymentLogsService = transactionPaymentLogsService;
        this.merchantCommissionService = merchantCommissionService;
    }

    @Override
    public void handle(UpdateManageStatusTransactionBlueCommand command) {
        TransactionDto transactionDto = transactionService.findById(command.getRequest().getOrderNumber());
        ManageCreditCardTypeDto creditCardTypeDto = creditCardTypeService.findByFirstDigit(Character.getNumericValue(command.getRequest().getCardNumber().charAt(0)));
        ManageTransactionStatusDto transactionStatusDto = transactionStatusService.findByMerchantResponseStatus(command.getRequest().getStatus());
        TransactionPaymentLogsDto transactionPaymentLogsDto = transactionPaymentLogsService.findByTransactionId(transactionDto.getTransactionUuid());
        double commission = merchantCommissionService.calculateCommission(transactionDto.getAmount(), transactionDto.getMerchant().getId(), creditCardTypeDto.getId());

        //Comenzar a actualizar lo referente a la transaccion en las diferntes tablas
        //1- Actualizar data in vcc_transaction
        transactionDto.setCardNumber(command.getRequest().getCardNumber());
        transactionDto.setCreditCardType(creditCardTypeDto);
        transactionDto.setCommission(commission);
        transactionDto.setStatus(transactionStatusDto);
        this.transactionService.update(transactionDto);

        //3- Actualizar vcc_transaction_payment_logs columna merchant_respose en vcc_transaction
        transactionPaymentLogsDto.setMerchantResponse(command.getRequest().getMerchantResponse());
        transactionPaymentLogsDto.setIsProcessed(true);
        this.transactionPaymentLogsService.update(transactionPaymentLogsDto);

    }
}
