package com.kynsoft.finamer.payment.domain.core.payment;

import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import com.kynsoft.finamer.payment.domain.dtoEnum.ImportType;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;

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
    private final ManageInvoiceDto invoiceDto;

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
                                ManageInvoiceDto invoiceDto){
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
        this.invoiceDto = invoiceDto;
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
                this.invoiceDto,
                this.remark);

        paymentDto.setCreateByCredit(true);
        paymentDto.setImportType(ImportType.AUTOMATIC);
        paymentDto.setHasAttachment(true);
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
                                  ManageInvoiceDto invoiceDto,
                                  String remark){
        return new PaymentDto(
                UUID.randomUUID(),
                Long.MIN_VALUE,
                Status.ACTIVE,
                paymentSourceDto,
                invoiceDto.getInvoiceNumber().toString(),
                transactionDate.toLocalDate(),
                paymentStatusDto,
                clientDto,
                agencyDto,
                hotelDto,
                bankAccountDto,
                attachmentStatusDto,
                amount,
                0.0,
                0.0,
                0.0,
                0.0,
                /**
                 *Para este caso, el identified toma el valor del paymentAmount, dado que se crean cash por el valor total del payment Amount y
                 * entonces el notIdentified se hace cero.
                 **/
                amount,
                0.0,
                0.0,//Payment Amount - Deposit Balance - (Suma de trx tipo check Cash en el Manage Payment Transaction Type)
                amount,
                remark,
                invoiceDto,
                null,
                null,
                EAttachment.NONE,
                LocalTime.now()
        );
    }
}
