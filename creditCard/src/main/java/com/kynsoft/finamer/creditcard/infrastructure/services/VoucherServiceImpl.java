package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.services.*;
import com.kynsoft.finamer.creditcard.infrastructure.utils.CreditCardUploadAttachmentUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.UUID;

@Service
public class VoucherServiceImpl implements IVoucherService {

    private final IPdfVoucherService pdfVoucherService;
    private final CreditCardUploadAttachmentUtil creditCardUploadAttachmentUtil;
    private final IManageAttachmentTypeService attachmentTypeService;
    private final IManageResourceTypeService resourceTypeService;
    private final IAttachmentService attachmentService;

    public VoucherServiceImpl(IPdfVoucherService pdfVoucherService, CreditCardUploadAttachmentUtil creditCardUploadAttachmentUtil, IManageAttachmentTypeService attachmentTypeService, IManageResourceTypeService resourceTypeService, IAttachmentService attachmentService) {
        this.pdfVoucherService = pdfVoucherService;
        this.creditCardUploadAttachmentUtil = creditCardUploadAttachmentUtil;
        this.attachmentTypeService = attachmentTypeService;
        this.resourceTypeService = resourceTypeService;
        this.attachmentService = attachmentService;
    }

    @Override
    public void createAndUploadAndAttachTransactionVoucher(TransactionDto transactionDto, ManagerMerchantConfigDto merchantConfigDto, String employee) {
        try {
            byte[] attachment = this.pdfVoucherService.generatePdf(transactionDto, merchantConfigDto);
            int attachNumber = transactionDto.getAttachments() != null ? transactionDto.getAttachments().size()+1 : 1;
            String filename = "transaction_"+transactionDto.getId()+"_attach_"+attachNumber+".pdf";
            String file = "";
            LinkedHashMap<String, String> response = this.creditCardUploadAttachmentUtil.uploadAttachmentContent(filename, attachment);
            file = response.get("url");
            this.createAttachment(transactionDto, file, filename, employee);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAttachment(TransactionDto transactionDto, String file, String filename, String employee){
        ManageAttachmentTypeDto attachmentTypeDto = this.attachmentTypeService.findByDefault();
        ResourceTypeDto resourceTypeDto = this.resourceTypeService.findByVcc();
        this.attachmentService.create(new AttachmentDto(
                UUID.randomUUID(),
                0L,
                filename,
                file,
                "Automatic voucher generated. Date "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+".",
                attachmentTypeDto,
                transactionDto,
                employee,
                null,
                null,
                resourceTypeDto,
                null
        ));
    }
}
