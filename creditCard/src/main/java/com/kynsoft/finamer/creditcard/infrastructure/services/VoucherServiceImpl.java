package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.services.*;
import com.kynsoft.finamer.creditcard.infrastructure.utils.CreditCardUploadAttachmentUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Service
public class VoucherServiceImpl implements IVoucherService {

    private final IPdfVoucherService pdfVoucherService;
    private final CreditCardUploadAttachmentUtil creditCardUploadAttachmentUtil;
    private final IManageAttachmentTypeService attachmentTypeService;
    private final IManageResourceTypeService resourceTypeService;
    private final ITransactionService transactionService;

    public VoucherServiceImpl(IPdfVoucherService pdfVoucherService, CreditCardUploadAttachmentUtil creditCardUploadAttachmentUtil, IManageAttachmentTypeService attachmentTypeService, IManageResourceTypeService resourceTypeService, ITransactionService transactionService) {
        this.pdfVoucherService = pdfVoucherService;
        this.creditCardUploadAttachmentUtil = creditCardUploadAttachmentUtil;
        this.attachmentTypeService = attachmentTypeService;
        this.resourceTypeService = resourceTypeService;
        this.transactionService = transactionService;
    }

    @Override
    public void createAndUploadAndAttachTransactionVoucher(TransactionDto transactionDto, ManagerMerchantConfigDto merchantConfigDto, String employee) {
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
