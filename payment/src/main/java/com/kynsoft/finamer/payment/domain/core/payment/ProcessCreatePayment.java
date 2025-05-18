package com.kynsoft.finamer.payment.domain.core.payment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.payment.domain.core.attachment.ProcessCreateAttachment;
import com.kynsoft.finamer.payment.domain.core.helper.CreateAttachment;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import com.kynsoft.finamer.payment.domain.dtoEnum.ImportType;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.masterPaymentAttachment.MasterPaymetAttachmentWhitDefaultTrueIntoCreateMustBeUniqueRule;
import com.kynsoft.finamer.payment.domain.rules.payment.CheckIfTransactionDateIsWithInRangeCloseOperationRule;
import com.kynsoft.finamer.payment.domain.rules.payment.PaymentValidateBankAccountAndHotelRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckIfDateIsBeforeCurrentDateRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckPaymentAmountGreaterThanZeroRule;
import lombok.Getter;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private final Boolean isIgnoreBankAccount;
    private final ManageEmployeeDto employeeDto;
    private final PaymentCloseOperationDto closeOperationDto;
    private final List<CreateAttachment> attachments;
    private final ManagePaymentAttachmentStatusDto attachmentStatusSupport;
    private final ManagePaymentAttachmentStatusDto attachmentOtherSupport;
    private final List<MasterPaymentAttachmentDto> masterPaymentAttachmentDtoList;
    private final List<AttachmentStatusHistoryDto> attachmentStatusHistoryDtoList;
    private final PaymentStatusHistoryDto paymentStatusHistoryDto;
    private final ImportType importType;
    private final boolean createdByCredit;

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
                                String reference,
                                Boolean isIgnoreBankAccount,
                                ManageEmployeeDto employeeDto,
                                PaymentCloseOperationDto closeOperationDto,
                                List<CreateAttachment> attachments,
                                ManagePaymentAttachmentStatusDto attachmentStatusSupport,
                                ManagePaymentAttachmentStatusDto attachmentOtherSupport,
                                List<MasterPaymentAttachmentDto> masterPaymentAttachmentDtoList,
                                List<AttachmentStatusHistoryDto> attachmentStatusHistoryDtoList,
                                PaymentStatusHistoryDto paymentStatusHistoryDto,
                                ImportType importType,
                                boolean createdByCredit){
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
        this.isIgnoreBankAccount = isIgnoreBankAccount;
        this.employeeDto = employeeDto;
        this.closeOperationDto = closeOperationDto;
        this.attachments = attachments;
        this.attachmentStatusSupport = attachmentStatusSupport;
        this.attachmentOtherSupport = attachmentOtherSupport;
        this.masterPaymentAttachmentDtoList = masterPaymentAttachmentDtoList;
        this.attachmentStatusHistoryDtoList = attachmentStatusHistoryDtoList;
        this.paymentStatusHistoryDto = paymentStatusHistoryDto;
        this.importType = importType;
        this.createdByCredit = createdByCredit;
    }

    public PaymentDto create(){

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(this.paymentSourceDto, "paymentSource", "Payment Source ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(this.paymentStatusDto, "paymentStatus", "Payment Status ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(this.clientDto, "client", "Client ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(this.agencyDto, "agency", "Agency ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(this.hotelDto, "hotel", "Hotel ID cannot be null."));
        if (!this.isIgnoreBankAccount || !this.paymentSourceDto.getExpense())//Se agrega esto con el objetivo de ignorar este check cuando se importa
        {
            RulesChecker.checkRule(new ValidateObjectNotNullRule<>(this.bankAccountDto, "bankAccount", "Bank Account ID cannot be null."));
            RulesChecker.checkRule(new PaymentValidateBankAccountAndHotelRule(this.hotelDto, this.bankAccountDto));
        }

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(this.attachmentStatusDto, "attachmentStatus", "Attachment Status ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(this.employeeDto, "employee", "Employee ID cannot be null."));

        RulesChecker.checkRule(new CheckIfDateIsBeforeCurrentDateRule(this.transactionDate.toLocalDate()));
        RulesChecker.checkRule(new CheckPaymentAmountGreaterThanZeroRule(this.amount));

        RulesChecker.checkRule(new CheckIfTransactionDateIsWithInRangeCloseOperationRule(this.transactionDate.toLocalDate(),
                this.closeOperationDto.getBeginDate(), this.closeOperationDto.getEndDate()));

        //attachmentStatusHistoryDtoList = new ArrayList<>();
        //this.paymentStatusHistoryDto = new PaymentStatusHistoryDto();

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

        paymentDto.setImportType(this.importType);
        paymentDto.setCreateByCredit(this.createdByCredit);

        if(!this.attachments.isEmpty()){
            paymentDto.setHasAttachment(true);
            this.createAttachment(paymentDto,
                    this.attachments,
                    this.attachmentStatusSupport,
                    this.attachmentOtherSupport,
                    this.masterPaymentAttachmentDtoList);
            paymentDto.setAttachments(this.masterPaymentAttachmentDtoList);

            this.createAttachmentStatusHistory(this.employeeDto,
                    paymentDto,
                    this.masterPaymentAttachmentDtoList,
                    this.attachmentStatusHistoryDtoList);
        }else{
            this.createAttachmentStatusHistoryWithoutAttachment(paymentDto,
                    this.employeeDto,
                    this.attachmentStatusHistoryDtoList);
        }

        this.createPaymentAttachmentStatusHistory(paymentDto,
                this.employeeDto,
                this.paymentStatusHistoryDto);

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
                LocalTime.now()
        );
    }

    private void createAttachment(PaymentDto paymentDto,
                                                              List<CreateAttachment> attachments,
                                                              ManagePaymentAttachmentStatusDto attachmentStatusSupport,
                                                              ManagePaymentAttachmentStatusDto attachmentOtherSupport,
                                                              List<MasterPaymentAttachmentDto> masterPaymentAttachmentDtoList){
        int countDefaults = 0;//El objetivo de este contador es controlar cuantos Payment Support han sido agregados.
        for (CreateAttachment attachment : attachments) {
            ProcessCreateAttachment processCreateAttachment = new ProcessCreateAttachment(paymentDto, attachment);
            MasterPaymentAttachmentDto newAttachmentDto = processCreateAttachment.create();

            if (attachment.getAttachmentTypeDto().getDefaults()) {
                countDefaults++;
                newAttachmentDto.setStatusHistory(attachmentStatusSupport.getCode() + "-" + attachmentStatusSupport.getName());
            } else {
                //newAttachmentDto.setStatusHistory(attachmentStatusNonNone.getCode() + "-" + attachmentStatusNonNone.getName());
                newAttachmentDto.setStatusHistory(attachmentOtherSupport.getCode() + "-" + attachmentOtherSupport.getName());
            }
            masterPaymentAttachmentDtoList.add(newAttachmentDto);
        }

        RulesChecker.checkRule(new MasterPaymetAttachmentWhitDefaultTrueIntoCreateMustBeUniqueRule(countDefaults));

        if (countDefaults > 0) {
            paymentDto.setPaymentSupport(true);
            paymentDto.setAttachmentStatus(attachmentStatusSupport);
            //paymentDto.setAttachmentStatus(this.attachmentStatusService.findBySupported());
        } else {
            paymentDto.setAttachmentStatus(attachmentOtherSupport);
            //paymentDto.setAttachmentStatus(attachmentStatusNonNone);
            paymentDto.setPaymentSupport(false);
        }
    }

    private void createAttachmentStatusHistory(ManageEmployeeDto employeeDto,
                                               PaymentDto payment,
                                               List<MasterPaymentAttachmentDto> list,
                                               List<AttachmentStatusHistoryDto> attachmentStatusHistoryDtoList) {
        for (MasterPaymentAttachmentDto attachment : list) {
            AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
            attachmentStatusHistoryDto.setId(UUID.randomUUID());//Quitar
            attachmentStatusHistoryDto.setDescription("An attachment to the payment was inserted. The file name: " + attachment.getFileName());
            attachmentStatusHistoryDto.setEmployee(employeeDto);
            attachmentStatusHistoryDto.setPayment(payment);
            attachmentStatusHistoryDto.setStatus(attachment.getStatusHistory());
            //attachmentStatusHistoryDto.setStatus(payment.getAttachmentStatus().getCode() + "-" + payment.getAttachmentStatus().getName());
            attachmentStatusHistoryDto.setAttachmentId(attachment.getAttachmentId());
            attachmentStatusHistoryDtoList.add(attachmentStatusHistoryDto);
        }
    }

    private void createAttachmentStatusHistoryWithoutAttachment(PaymentDto payment,
                                                                ManageEmployeeDto employeeDto,
                                                                List<AttachmentStatusHistoryDto> attachmentStatusHistoryDtoList) {
        AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("Creating payment without attachment.");
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus("NON-NONE");
        attachmentStatusHistoryDtoList.add(attachmentStatusHistoryDto);
    }

    private void createPaymentAttachmentStatusHistory(PaymentDto payment,
                                                      ManageEmployeeDto employeeDto,
                                                      PaymentStatusHistoryDto attachmentStatusHistoryDto) {
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("Creating Payment.");
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(payment.getPaymentStatus().getCode() + "-" + payment.getPaymentStatus().getName());
    }
}
