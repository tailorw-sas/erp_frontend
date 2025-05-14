package com.kynsoft.finamer.payment.domain.core.payment;

import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import com.kynsoft.finamer.payment.domain.dtoEnum.ImportType;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public class ProcessCreatePayment {

    private final ManagePaymentSourceDto paymentSourceDto;
    private final ManagePaymentStatusDto paymentStatusDto;
    private final OffsetDateTime transactionDate;
    private final ManageClientDto clientDto;
    private final ManageAgencyDto agencyDto;
    private final ManageHotelDto hotelDto;
    private final ManageBankAccountDto bankAccountDto;
    private final ManagePaymentAttachmentStatusDto attachmentStatusDto;
    private final Double amount;
    private final String remark;
    private final String reference;

    public ProcessCreatePayment(ManagePaymentSourceDto paymentSourceDto,
                                ManagePaymentStatusDto paymentStatusDto,
                                OffsetDateTime transactionDate,
                                ManageClientDto clientDto,
                                ManageAgencyDto agencyDto,
                                ManageHotelDto hotelDto,
                                ManageBankAccountDto bankAccountDto,
                                ManagePaymentAttachmentStatusDto attachmentStatusDto,
                                Double amount,
                                String remark,
                                String reference){
        this.paymentSourceDto = paymentSourceDto;
        this.paymentStatusDto = paymentStatusDto;
        this.transactionDate = transactionDate;
        this.clientDto = clientDto;
        this.agencyDto = agencyDto;
        this.hotelDto = hotelDto;
        this.bankAccountDto = bankAccountDto;
        this.attachmentStatusDto = attachmentStatusDto;
        this.amount = amount;
        this.remark = remark;
        this.reference = reference;
    }

    public PaymentDto create(){
        PaymentDto paymentDto = this.getPayment(this.paymentSourceDto,
                this.transactionDate,
                this.paymentStatusDto,
                this.clientDto,
                this.agencyDto,
                this.hotelDto,
                this.bankAccountDto,
                this.attachmentStatusDto,
                this.amount,
                this.remark,
                this.reference);

//        paymentDto.setCreateByCredit(true);
//        paymentDto.setImportType(ImportType.AUTOMATIC);
//        paymentDto.setHasAttachment(true);
        //paymentDto.setHasDetailTypeDeposit(false);

        return paymentDto;
    }

    private PaymentDto getPayment(ManagePaymentSourceDto paymentSourceDto,
                                  OffsetDateTime transactionDate,
                                  ManagePaymentStatusDto paymentStatusDto,
                                  ManageClientDto clientDto,
                                  ManageAgencyDto agencyDto,
                                  ManageHotelDto hotelDto,
                                  ManageBankAccountDto bankAccountDto,
                                  ManagePaymentAttachmentStatusDto attachmentStatusDto,
                                  Double amount,
                                  String remark,
                                  String reference){
        return new PaymentDto(
                UUID.randomUUID(),
                Long.MIN_VALUE,
                Status.ACTIVE,
                paymentSourceDto,
                reference,//invoiceDto.getInvoiceNumber().toString(),
                transactionDate.toLocalDate(),
                paymentStatusDto,
                clientDto,
                agencyDto,
                hotelDto,
                bankAccountDto,
                attachmentStatusDto,
                amount,
                amount,
                0.0,
                0.0,
                0.0,
                0.0,
                amount,
                amount,
                0.0,
                remark,
                null,
                null,
                OffsetDateTime.now(),
                EAttachment.NONE,
                transactionDate.toLocalTime()
        );
    }
}
