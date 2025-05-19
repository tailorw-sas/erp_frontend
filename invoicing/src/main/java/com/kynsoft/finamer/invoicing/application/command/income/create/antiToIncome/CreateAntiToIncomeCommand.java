package com.kynsoft.finamer.invoicing.application.command.income.create.antiToIncome;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.domain.http.entity.income.CreateAntiToIncomeAttachmentRequest;
import com.kynsof.share.core.domain.http.entity.income.CreateAntiToIncomeRequest;
import com.kynsof.share.core.domain.http.entity.income.CreateIncomeFromPaymentMessage;
import com.kynsoft.finamer.invoicing.domain.dto.IncomeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateAntiToIncomeCommand implements ICommand {

    private UUID id;
    private LocalDateTime invoiceDate;
    private Boolean manual;
    private UUID agency;
    private UUID hotel;
    private UUID invoiceType;
    private UUID invoiceStatus;
    private Double incomeAmount;
    private Status status;
    private Long invoiceNumber;
    private LocalDate dueDate;
    private Boolean reSend;
    private LocalDate reSendDate;
    private Long invoiceId;

    private IncomeDto income;
    private String employee;

    private List<CreateAntiToIncomeAttachmentRequest> attachments;
    private String invoiceNo;

    public CreateAntiToIncomeCommand(Status status, LocalDateTime invoiceDate, Boolean manual,
                               UUID agency, UUID hotel, UUID invoiceType, Double incomeAmount,
                               Long invoiceNumber, LocalDate dueDate, Boolean reSend, LocalDate reSendDate,
                               UUID invoiceStatus, String employee, List<CreateAntiToIncomeAttachmentRequest> attachments) {
        this.id = UUID.randomUUID();
        this.status = status;
        this.invoiceDate = invoiceDate;
        this.manual = manual;
        this.agency = agency;
        this.hotel = hotel;
        this.invoiceType = invoiceType;
        this.invoiceStatus = invoiceStatus;
        this.incomeAmount = incomeAmount;
        this.invoiceNumber = invoiceNumber;
        this.dueDate = dueDate;
        this.reSend = reSend;
        this.reSendDate = reSendDate;
        this.employee = employee;
        this.attachments = attachments;
    }

    public static CreateAntiToIncomeCommand fromRequest(CreateAntiToIncomeRequest request) {
        return new CreateAntiToIncomeCommand(
                Status.valueOf(request.getStatus()),
                LocalDateTime.parse(request.getInvoiceDate()),
                request.getManual(),
                request.getAgency(),
                request.getHotel(),
                request.getInvoiceType(),
                request.getIncomeAmount(),
                request.getInvoiceNumber(),
                LocalDate.parse(request.getDueDate()),
                request.getReSend(),
                LocalDate.parse(request.getReSendDate()),
                request.getInvoiceStatus(),
                request.getEmployee(),
                request.getAttachments()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateIncomeFromPaymentMessage(id, invoiceId, invoiceNo);
    }
}
