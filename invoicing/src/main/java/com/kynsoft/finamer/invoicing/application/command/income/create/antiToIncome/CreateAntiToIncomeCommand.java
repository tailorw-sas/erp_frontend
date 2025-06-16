package com.kynsoft.finamer.invoicing.application.command.income.create.antiToIncome;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsof.share.core.domain.http.entity.AttachmentHttp;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsof.share.core.domain.http.entity.InvoiceHttp;
import com.kynsof.share.core.domain.http.entity.income.*;
import com.kynsoft.finamer.invoicing.application.command.income.create.CreateIncomeAttachmentRequest;
import com.kynsoft.finamer.invoicing.application.command.income.create.CreateIncomeCommand;
import com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create.CreateIncomeAdjustment;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class CreateAntiToIncomeCommand implements ICommand {

    private List<CreateIncomeCommand> createIncomeCommands;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CreateAntiToIncomeCommand(List<CreateIncomeCommand> createIncomeCommands) {
        this.createIncomeCommands = createIncomeCommands;
    }

    public static CreateAntiToIncomeCommand fromRequest(CreateAntiToIncomeFromPaymentRequest request) {
        List<CreateIncomeCommand> commands = request.getCreateIncomeRequests().stream()
                .map(createAntiToIncomeRequest -> {
                    return new CreateIncomeCommand(
                            Status.valueOf(createAntiToIncomeRequest.getStatus()),
                            createAntiToIncomeRequest.getInvoiceDate(),
                            createAntiToIncomeRequest.getManual(),
                            createAntiToIncomeRequest.getAgency(),
                            createAntiToIncomeRequest.getHotel(),
                            createAntiToIncomeRequest.getInvoiceType(),
                            createAntiToIncomeRequest.getIncomeAmount(),
                            createAntiToIncomeRequest.getInvoiceNumber(),
                            createAntiToIncomeRequest.getDueDate(),
                            createAntiToIncomeRequest.getReSend(),
                            createAntiToIncomeRequest.getReSendDate(),
                            createAntiToIncomeRequest.getInvoiceStatus(),
                            createAntiToIncomeRequest.getEmployee(),
                            createAntiToIncomeRequest.getAttachments().stream().map(antiToIncomeAttachmentRequest -> {
                                return new CreateIncomeAttachmentRequest(null,
                                        antiToIncomeAttachmentRequest.getFile(),
                                        null,
                                        null,
                                        antiToIncomeAttachmentRequest.getEmployee(),
                                        antiToIncomeAttachmentRequest.getEmployeeId(),
                                        null);
                            }).toList(),
                            createAntiToIncomeRequest.getAdjustments().stream().map(antiToIncomeAdjustmentRequest -> {
                                return new CreateIncomeAdjustment(antiToIncomeAdjustmentRequest.getTransactionType(),
                                        antiToIncomeAdjustmentRequest.getAmount(),
                                        antiToIncomeAdjustmentRequest.getDate(),
                                        antiToIncomeAdjustmentRequest.getRemark());
                            }).toList()
                    );
                })
                .collect(Collectors.toList());
        return new CreateAntiToIncomeCommand(commands);
    }

    @Override
    public ICommandMessage getMessage() {
        List<InvoiceHttp> messages = createIncomeCommands.stream()
                .map(command -> {
                    return convertToInvoiceHttp(command.getIncome(), true, true);
                })
                .toList();

        return new CreateAntiToIncomeFromPaymentMessage(messages);
    }

    private InvoiceHttp convertToInvoiceHttp(ManageInvoiceDto invoiceDto, boolean includeAttachments, boolean includeBookings){
        return new InvoiceHttp(invoiceDto.getId(),
                invoiceDto.getHotel() != null ? invoiceDto.getHotel().getId() : null,
                null,
                invoiceDto.getParent() != null ? invoiceDto.getParent().getId() : null,
                invoiceDto.getAgency() != null ? invoiceDto.getAgency().getId() : null,
                invoiceDto.getInvoiceId(),
                invoiceDto.getInvoiceNo(),
                invoiceDto.getInvoiceNumber(),
                invoiceDto.getInvoiceType().name(),
                invoiceDto.getInvoiceAmount(),
                (includeAttachments && invoiceDto.getAttachments() != null)
                        ? invoiceDto.getAttachments().stream().map(this::convertToAttachmentHttp).toList()
                        : List.of(),
                includeBookings ? invoiceDto.getBookings() != null ? invoiceDto.getBookings().stream().map(this::convertToBookingHttp).toList() : null : null,
                invoiceDto.getAttachments() != null && !invoiceDto.getAttachments().isEmpty(),
                invoiceDto.getInvoiceDate().format(formatter),
                invoiceDto.getAutoRec()
                );
    }

    private AttachmentHttp convertToAttachmentHttp(ManageAttachmentDto attachmentDto){
        return new AttachmentHttp(attachmentDto.getId(),
                attachmentDto.getEmployeeId(),
                attachmentDto.getFilename(),
                null,
                null,
                attachmentDto.getRemark(),
                false);
    }

    private BookingHttp convertToBookingHttp(ManageBookingDto bookingDto){
        return new BookingHttp(
                bookingDto.getId(),
                bookingDto.getBookingId(),
                bookingDto.getReservationNumber().toString(),//TODO Validar, capaz no es este campo
                bookingDto.getCheckIn().format(formatter),
                bookingDto.getCheckOut().format(formatter),
                bookingDto.getFullName(),
                bookingDto.getFirstName(),
                bookingDto.getLastName(),
                bookingDto.getInvoiceAmount(),
                bookingDto.getDueAmount(),
                bookingDto.getCouponNumber(),
                bookingDto.getAdults(),
                bookingDto.getChildren(),
                bookingDto.getParent() != null ? bookingDto.getParent().getId() : null,
                bookingDto.getInvoice() != null ? convertToInvoiceHttp(bookingDto.getInvoice(), false, false) : null,
                bookingDto.getBookingDate().format(formatter)
        );
    }
}
