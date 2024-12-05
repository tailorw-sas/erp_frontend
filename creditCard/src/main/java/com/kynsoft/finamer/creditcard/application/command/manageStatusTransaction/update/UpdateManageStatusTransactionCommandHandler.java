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
import com.kynsoft.finamer.creditcard.infrastructure.utils.CreditCardUploadAttachmentUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
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
    private final ITransactionStatusHistoryService transactionStatusHistoryService;
    private final IPdfVoucherService pdfVoucherService;
    private final CreditCardUploadAttachmentUtil creditCardUploadAttachmentUtil;
    private final IManageAttachmentTypeService attachmentTypeService;
    private final IManageResourceTypeService resourceTypeService;

    public UpdateManageStatusTransactionCommandHandler(ITransactionService transactionService, IManageStatusTransactionService statusTransactionService, IManageMerchantConfigService merchantConfigService,
                                                       ManageCreditCardTypeServiceImpl creditCardTypeService, ManageTransactionStatusServiceImpl transactionStatusService, CardNetJobServiceImpl cardnetJobService,
                                                       TransactionPaymentLogsService transactionPaymentLogsService, ManageMerchantCommissionServiceImpl merchantCommissionService, IParameterizationService parameterizationService, IProcessErrorLogService processErrorLogService, ITransactionStatusHistoryService transactionStatusHistoryService, IPdfVoucherService pdfVoucherService, CreditCardUploadAttachmentUtil creditCardUploadAttachmentUtil, IManageAttachmentTypeService attachmentTypeService, IManageResourceTypeService resourceTypeService) {

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
        this.transactionStatusHistoryService = transactionStatusHistoryService;
        this.pdfVoucherService = pdfVoucherService;
        this.creditCardUploadAttachmentUtil = creditCardUploadAttachmentUtil;
        this.attachmentTypeService = attachmentTypeService;
        this.resourceTypeService = resourceTypeService;
    }

    @Override
    public void handle(UpdateManageStatusTransactionCommand command) {
        //Obtener toda la informacion necesaria para los updates
        CardnetJobDto cardnetJobDto = statusTransactionService.findBySession(command.getSession());
        TransactionDto transactionDto = transactionService.findByUuid(cardnetJobDto.getTransactionId());
        if (!transactionDto.getStatus().isSentStatus() && !transactionDto.getStatus().isDeclinedStatus()) {
            throw new BusinessException(DomainErrorMessage.MANAGE_TRANSACTION_ALREADY_PROCESSED, DomainErrorMessage.MANAGE_TRANSACTION_ALREADY_PROCESSED.getReasonPhrase());
        }
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

            //Obtener estado de la transacción correspondiente dado el responseCode del merchant

            CardNetResponseStatus pairedStatus = CardNetResponseStatus.valueOfCode(transactionResponse.getResponseCode());
            ManageTransactionStatusDto transactionStatusDto = transactionStatusService.findByMerchantResponseStatus(pairedStatus.transactionStatus());

            // Solo calcular la comission si es Received
            if (transactionStatusDto.isReceivedStatus()) {
                ParameterizationDto parameterizationDto = this.parameterizationService.findActiveParameterization();
                //si no encuentra la parametrization que agarre 2 decimales por defecto
                int decimals = parameterizationDto != null ? parameterizationDto.getDecimals() : 2;

                double commission= 0.0;
                try {
                    commission = merchantCommissionService.calculateCommission(transactionDto.getAmount(), transactionDto.getMerchant().getId(), creditCardTypeDto.getId(), transactionDto.getCheckIn().toLocalDate(), decimals);
                } catch (Exception e) {
                    ProcessErrorLogDto processErrorLogDto = new ProcessErrorLogDto();
                    processErrorLogDto.setSession(cardnetJobDto.getSession());
                    processErrorLogDto.setTransactionId(cardnetJobDto.getTransactionId());
                    processErrorLogDto.setError(e.getMessage());
                    this.processErrorLogService.create(processErrorLogDto);
                }
                //independientemente del valor de la commission el netAmount tiene dos decimales
                double netAmount = BankerRounding.round(transactionDto.getAmount() - commission, 2);
                transactionDto.setCommission(commission);
                transactionDto.setNetAmount(netAmount);
                transactionDto.setTransactionDate(LocalDateTime.now());
            }

            transactionResponse.setMerchantStatus(pairedStatus.toDTO());
            TransactionPaymentLogsDto transactionPaymentLogsDto = transactionPaymentLogsService.findByTransactionId(transactionDto.getTransactionUuid());

            // 1- Actualizar data en vcc_transaction
            transactionDto.setCardNumber(transactionResponse.getCreditCardNumber());
//            transactionDto.setReferenceNumber(transactionResponse.getRetrievalReferenceNumber());
            transactionDto.setCreditCardType(creditCardTypeDto);
            transactionDto.setPaymentDate(LocalDateTime.now());
            if (!transactionStatusDto.equals(transactionDto.getStatus())){
                transactionDto.setStatus(transactionStatusDto);
                this.transactionStatusHistoryService.create(transactionDto, command.getEmployee());
            }
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
            transactionService.sendTransactionConfirmationVoucherEmail(transactionDto, merchantConfigDto);
            createAndUploadVoucher(transactionDto, merchantConfigDto, command.getEmployee());
            command.setResult(transactionResponse);
        } else {
            throw new BusinessException(DomainErrorMessage.VCC_TRANSACTION_RESULT_CARDNET_ERROR, DomainErrorMessage.VCC_TRANSACTION_RESULT_CARDNET_ERROR.getReasonPhrase());
        }
    }

    private void createAndUploadVoucher(TransactionDto transactionDto, ManagerMerchantConfigDto merchantConfigDto, String employee){
        try {
            byte[] attachment = this.pdfVoucherService.generatePdf(transactionDto, merchantConfigDto);
            String filename = "transaction_"+transactionDto.getId()+".pdf";
            String file = "";
            LinkedHashMap<String, String> response = this.creditCardUploadAttachmentUtil.uploadAttachmentContent(filename, attachment);
            file = response.get("url");
            this.attachVoucherToTransaction(transactionDto, file, filename, employee);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void attachVoucherToTransaction(TransactionDto transactionDto, String file, String filename, String employee){
        List<AttachmentDto> attachments = transactionDto.getAttachments() != null ? transactionDto.getAttachments() : new ArrayList<>();
        ManageAttachmentTypeDto attachmentTypeDto = this.attachmentTypeService.findByDefault();
        ResourceTypeDto resourceTypeDto = this.resourceTypeService.findByVcc();
        AttachmentDto newAttachment = new AttachmentDto(
                UUID.randomUUID(),
                0L,
                filename,
                file,
                "",
                attachmentTypeDto,
                null,
                employee,
                null,
                null,
                resourceTypeDto,
                null
        );
        attachments.add(newAttachment);
        transactionDto.setAttachments(attachments);
        this.transactionService.update(transactionDto);
    }
}
