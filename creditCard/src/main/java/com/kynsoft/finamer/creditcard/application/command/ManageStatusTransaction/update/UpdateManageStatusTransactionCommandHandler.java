package com.kynsoft.finamer.creditcard.application.command.ManageStatusTransaction.update;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantConfigService;
import com.kynsoft.finamer.creditcard.domain.services.IManageStatusTransactionService;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import com.kynsoft.finamer.creditcard.infrastructure.services.CardnetJobServiceImpl;
import com.kynsoft.finamer.creditcard.infrastructure.services.ManageCreditCardTypeServiceImpl;
import com.kynsoft.finamer.creditcard.infrastructure.services.ManageTransactionStatusServiceImpl;
import com.kynsoft.finamer.creditcard.infrastructure.services.TransactionPaymentLogsService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UpdateManageStatusTransactionCommandHandler implements ICommandHandler<UpdateManageStatusTransactionCommand> {

    private final ITransactionService transactionService;
    private final IManageStatusTransactionService statusTransactionService;
    private final IManageMerchantConfigService merchantConfigService;
    private final ManageCreditCardTypeServiceImpl creditCardTypeService;
    private final ManageTransactionStatusServiceImpl transactionStatusService;
    private final CardnetJobServiceImpl cardnetJobService;
    private final TransactionPaymentLogsService transactionPaymentLogsService;

    public UpdateManageStatusTransactionCommandHandler(ITransactionService transactionService, IManageStatusTransactionService statusTransactionService, IManageMerchantConfigService merchantConfigService,
                                                       ManageCreditCardTypeServiceImpl creditCardTypeService, ManageTransactionStatusServiceImpl transactionStatusService, CardnetJobServiceImpl cardnetJobService,
                                                       TransactionPaymentLogsService transactionPaymentLogsService) {

        this.transactionService = transactionService;
        this.statusTransactionService = statusTransactionService;
        this.merchantConfigService = merchantConfigService;
        this.creditCardTypeService = creditCardTypeService;
        this.transactionStatusService = transactionStatusService;
        this.cardnetJobService = cardnetJobService;
        this.transactionPaymentLogsService = transactionPaymentLogsService;
    }
    @Override
    public void handle(UpdateManageStatusTransactionCommand command) {
        //Obtener toda la informacion necesaria para los updates
        CardnetJobDto cardnetJobDto = statusTransactionService.findBySession(command.getSession());
        TransactionDto transactionDto = transactionService.findByUuid(cardnetJobDto.getTransactionId());
        ManagerMerchantConfigDto merchantConfigDto = merchantConfigService.findByMerchantID(transactionDto.getMerchant().getId());
        String url = merchantConfigDto.getAltUrl()+"/"+cardnetJobDto.getSession()+"?sk"+cardnetJobDto.getSessionKey();

        //Obtener la informacion del pago
        Mono<String> dataTransactionSuccess = statusTransactionService.dataTransactionSuccess(url);

        ManageCreditCardTypeDto creditCardTypeDto = creditCardTypeService.findByFirstDigit(Character.getNumericValue(merchantConfigDto.getMerchantNumber().charAt(0)));
        ManageTransactionStatusDto transactionStatusDto = transactionStatusService.findByETransactionStatus();
        TransactionPaymentLogsDto transactionPaymentLogsDto = transactionPaymentLogsService.findByTransactionId(transactionDto.getTransactionUuid());

        //Comenzar a actualizar lo referente a la transaccion en las diferntes tablas
        //1- Actualizar data in vcc_transaction
        transactionDto.setCardNumber("123");
        transactionDto.setReferenceNumber("123");
        transactionDto.setCreditCardType(creditCardTypeDto);
        transactionDto.setStatus(transactionStatusDto);
        transactionDto.setStatus(transactionStatusDto);
        this.transactionService.update(transactionDto);

        //2- Actualizar data in vcc_cardnet_job
        cardnetJobDto.setIsProcessed(true);
        this.cardnetJobService.update(cardnetJobDto);

        //3- Actualizar vcc_transaction_payment_logs columna merchant_respose en vcc_transaction
        transactionPaymentLogsDto.setMerchantResponse(dataTransactionSuccess.toString());
        transactionPaymentLogsDto.setIsProcessed(true);
        this.transactionPaymentLogsService.update(transactionPaymentLogsDto);

    }

}
