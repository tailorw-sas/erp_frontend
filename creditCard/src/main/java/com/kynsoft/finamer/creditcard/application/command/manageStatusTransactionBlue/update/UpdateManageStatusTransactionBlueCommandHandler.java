package com.kynsoft.finamer.creditcard.application.command.manageStatusTransactionBlue.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import com.kynsoft.finamer.creditcard.infrastructure.services.CardNetJobServiceImpl;
import com.kynsoft.finamer.creditcard.infrastructure.services.ManageCreditCardTypeServiceImpl;
import com.kynsoft.finamer.creditcard.infrastructure.services.ManageTransactionStatusServiceImpl;
import com.kynsoft.finamer.creditcard.infrastructure.services.TransactionPaymentLogsService;
import org.springframework.stereotype.Component;

@Component
public class UpdateManageStatusTransactionBlueCommandHandler implements ICommandHandler<UpdateManageStatusTransactionBlueCommand> {

    private final ITransactionService transactionService;
    private final ManageCreditCardTypeServiceImpl creditCardTypeService;
    private final ManageTransactionStatusServiceImpl transactionStatusService;
    private final CardNetJobServiceImpl cardnetJobService;
    private final TransactionPaymentLogsService transactionPaymentLogsService;
    public UpdateManageStatusTransactionBlueCommandHandler(ITransactionService transactionService, ManageCreditCardTypeServiceImpl creditCardTypeService,
                                                           ManageTransactionStatusServiceImpl transactionStatusService, CardNetJobServiceImpl cardnetJobService,
                                                           TransactionPaymentLogsService transactionPaymentLogsService){
        this.transactionService = transactionService;
        this.creditCardTypeService = creditCardTypeService;
        this.transactionStatusService = transactionStatusService;
        this.cardnetJobService = cardnetJobService;
        this.transactionPaymentLogsService = transactionPaymentLogsService;
    }

    @Override
    public void handle(UpdateManageStatusTransactionBlueCommand command) {
        TransactionDto transactionDto = transactionService.findById(command.getOrderNumber());
        ManageCreditCardTypeDto creditCardTypeDto = creditCardTypeService.findByFirstDigit(Character.getNumericValue(command.getCardNumber().charAt(0)));
        ManageTransactionStatusDto transactionStatusDto = transactionStatusService.findByETransactionStatus();
        CardnetJobDto cardnetJobDto = cardnetJobService.findByTransactionId(transactionDto.getTransactionUuid());
        TransactionPaymentLogsDto transactionPaymentLogsDto = transactionPaymentLogsService.findByTransactionId(transactionDto.getTransactionUuid());


        //Comenzar a actualizar lo referente a la transaccion en las diferntes tablas
        //1- Actualizar data in vcc_transaction
        transactionDto.setCardNumber(command.getCardNumber());
        transactionDto.setCreditCardType(creditCardTypeDto);
        transactionDto.setStatus(transactionStatusDto);
        this.transactionService.update(transactionDto);

        //2- Actualizar data in vcc_cardnet_job
        cardnetJobDto.setIsProcessed(true);
        this.cardnetJobService.update(cardnetJobDto);

        //3- Actualizar vcc_transaction_payment_logs columna merchant_respose en vcc_transaction
        transactionPaymentLogsDto.setMerchantResponse(command.getMerchantResponse());
        transactionPaymentLogsDto.setIsProcessed(true);
        this.transactionPaymentLogsService.update(transactionPaymentLogsDto);

    }
}
