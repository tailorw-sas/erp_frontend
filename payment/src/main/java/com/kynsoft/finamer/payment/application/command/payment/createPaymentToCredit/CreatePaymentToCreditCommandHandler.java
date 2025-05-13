package com.kynsoft.finamer.payment.application.command.payment.createPaymentToCredit;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.application.command.attachment.create.CreateAttachmentCommand;
import com.kynsoft.finamer.payment.application.command.attachment.create.CreateAttachmentMessage;
import com.kynsoft.finamer.payment.application.command.attachmentStatusHistory.create.CreateAttachmentStatusHistoryCommand;
import com.kynsoft.finamer.payment.application.command.payment.create.CreateAttachmentRequest;
import com.kynsoft.finamer.payment.application.command.paymentAttachmentStatusHistory.create.CreatePaymentAttachmentStatusHistoryCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment.ApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPaymentToCredit.ApplyPaymentToCreditDetailCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeApplyDeposit.CreatePaymentDetailTypeApplyDepositCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeApplyDeposit.CreatePaymentDetailTypeApplyDepositMessage;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeCash.CreatePaymentDetailTypeCashCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeCash.CreatePaymentDetailTypeCashMessage;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeDeposit.CreatePaymentDetailTypeDepositCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeDeposit.CreatePaymentDetailTypeDepositMessage;
import com.kynsoft.finamer.payment.application.services.payment.credit.CreatePaymentFromCreditService;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.dtoEnum.ImportType;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CreatePaymentToCreditCommandHandler implements ICommandHandler<CreatePaymentToCreditCommand> {

    private final CreatePaymentFromCreditService createPaymentFromCreditService;

    public CreatePaymentToCreditCommandHandler(CreatePaymentFromCreditService createPaymentFromCreditService) {
        this.createPaymentFromCreditService = createPaymentFromCreditService;
    }

    @Override
    public void handle(CreatePaymentToCreditCommand command) {



    }

    //Payment creado con el deposit y apply deposit
//    private void createPaymentToCreditPositive(ManageHotelDto hotelDto, ManageBankAccountDto bankAccountDto, ManagePaymentSourceDto paymentSourceDto,
//            ManagePaymentStatusDto paymentStatusDto, ManageClientDto clientDto, ManageAgencyDto agencyDto,
//            ManagePaymentAttachmentStatusDto attachmentStatusDto, CreatePaymentToCreditCommand command, ManageEmployeeDto employee) {
//
//        if (!command.isAutoApplyCredit()) {
//            paymentStatusDto = this.statusService.findByConfirmed();
//        }
//
//        Double paymentAmount = command.getInvoiceDto().getInvoiceAmount() * -1;
//        double applied = 0.0;
//        double identified = 0.0;
//        double notIdentified = paymentAmount;
//        if (hotelDto.getAutoApplyCredit()) {// no aplica los apply deposit
//            applied = 0.0;
//            identified = 0.0;
//            notIdentified = paymentAmount;
//        } else {// aplica los apply deposit
//            applied = paymentAmount;
//            identified = paymentAmount;
//            notIdentified = 0.0;
//        }
//        if (command.getInvoiceDto().getInvoiceType().name().equals(EInvoiceType.OLD_CREDIT.name())) {
//            applied = 0.0;
//            identified = 0.0;
//            notIdentified = paymentAmount;
//        }
//        PaymentDto paymentDto = new PaymentDto(
//                UUID.randomUUID(),
//                Long.MIN_VALUE,
//                Status.ACTIVE,
//                paymentSourceDto,
//                deleteHotelInfo(command.getInvoiceDto().getInvoiceNumber()),
//                transactionDate(hotelDto.getId()),
//                //LocalDate.now(),
//                paymentStatusDto,
//                clientDto,
//                agencyDto,
//                hotelDto,
//                //hotelDto,
//                bankAccountDto,
//                attachmentStatusDto,
//                paymentAmount,
//                0.0,
//                paymentAmount,
//                0.0,
//                0.0,
//                /*
//                *identified es cero y el notIdentified toma el valor del paymentAmount y su valor varia en paymentAmount - identified.
//                *identified es la suma de todos los cash, que para este payment siempre va a ser cero.
//                */
//                identified,
//                notIdentified,
//                0.0, //Siempre es cero para este caso
//                applied,
//                "Created automatic to apply credit ( " + deleteHotelInfo(command.getInvoiceDto().getInvoiceNumber()) + ")",
//                command.getInvoiceDto(),
//                null,
//                null,
//                EAttachment.NONE,
//                LocalTime.now()
//        );
//
//        paymentDto.setCreateByCredit(true);
//        paymentDto.setImportType(ImportType.AUTOMATIC);
//        paymentDto.setApplyPayment(true);
//        paymentDto.setHasAttachment(true);
//        paymentDto.setHasDetailTypeDeposit(true);
//        PaymentDto paymentSave = this.paymentService.create(paymentDto);
//        PaymentDetailDto parentDetailDto = this.createPaymentDetailsToCreditDeposit(paymentSave, command);
////        if (command.getInvoiceDto().getBookings() != null) {
//        if (command.getInvoiceDto().getBookings() != null && command.isAutoApplyCredit()) {
//            List<PaymentDetailDto> updateChildrens = new ArrayList<>();
//            for (ManageBookingDto booking : command.getInvoiceDto().getBookings()) {
//                updateChildrens.add(this.createPaymentDetailsToCreditApplyDeposit(paymentSave, booking.getParent(), parentDetailDto, command, booking.getInvoiceAmount()));
//            }
//            parentDetailDto.setPaymentDetails(updateChildrens);
//        } else {
//            paymentSave.setDepositBalance(paymentAmount);
//            this.paymentService.update(paymentSave);
//        }
//
////        ManageEmployeeDto employeeDto = null;
//        if (command.getAttachments() != null) {
////            try {
////                employeeDto = this.manageEmployeeService.findById(command.getAttachments().get(0).getEmployee());
////            } catch (Exception e) {
////            }
//            paymentDto.setAttachments(this.createAttachment(command.getAttachments(), paymentDto, command));
//            //this.createAttachmentStatusHistory(employeeDto, paymentDto, command);
//            this.createAttachmentStatusHistory(employee, paymentDto, command);
//        }
//
//        //this.createPaymentAttachmentStatusHistory(employeeDto, paymentDto, command);
//        this.createPaymentAttachmentStatusHistory(employee, paymentDto, command);
//
//        this.paymentDetailService.update(parentDetailDto);
//
//    }





    private void createPaymentDetailsToCreditCash(PaymentDto paymentCash, ManageBookingDto booking, CreatePaymentToCreditCommand command) {
        CreatePaymentDetailTypeCashMessage message = command.getMediator().send(new CreatePaymentDetailTypeCashCommand(paymentCash, booking.getId(), booking.getAmountBalance(), false, command.getInvoiceDto().getInvoiceDate(), true));
        //CreatePaymentDetailTypeCashMessage message = command.getMediator().send(new CreatePaymentDetailTypeCashCommand(paymentCash, booking.getId(), booking.getAmountBalance() * -1, false, command.getInvoiceDto().getInvoiceDate(), true));
        //if (command.isAutoApplyCredit()) {
            command.getMediator().send(new ApplyPaymentDetailCommand(message.getId(), booking.getId(), null));//Este marcado o no el check AutoApplyCredit el cash que se crea siempre va aplicado al CREDIT.
        //}
    }

    private PaymentDetailDto createPaymentDetailsToCreditDeposit(PaymentDto payment, CreatePaymentToCreditCommand command) {
        CreatePaymentDetailTypeDepositMessage message = command.getMediator().send(new CreatePaymentDetailTypeDepositCommand(payment, true));
        return message.getNewDetailDto();
    }

    private PaymentDetailDto createPaymentDetailsToCreditApplyDeposit(PaymentDto payment, ManageBookingDto booking, PaymentDetailDto parentDetailDto, CreatePaymentToCreditCommand command, double amount) {
        CreatePaymentDetailTypeApplyDepositMessage message = command.getMediator().send(new CreatePaymentDetailTypeApplyDepositCommand(payment, /*booking.getAmountBalance()*/amount * -1, parentDetailDto, false, command.getInvoiceDto().getInvoiceDate(), true));
        command.getMediator().send(new ApplyPaymentToCreditDetailCommand(message.getNewDetailDto().getId(), booking.getId()));
        return message.getNewDetailDto();
    }

    private List<MasterPaymentAttachmentDto> createAttachment(List<CreateAttachmentRequest> attachments, PaymentDto paymentDto, CreatePaymentToCreditCommand command) {
          CreateAttachmentMessage message = command.getMediator().send(new CreateAttachmentCommand(attachments, paymentDto));
          return message.getRespose();
    }

    //Este metodo es para agregar el history del Attachemnt. Aqui el estado es el del nomenclador Manage Payment Attachment Status
    private void createAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment, CreatePaymentToCreditCommand command) {
        command.getMediator().send(new CreateAttachmentStatusHistoryCommand(payment, employeeDto));
    }

    //Este es para agregar el History del Payment. Aqui el estado es el del nomenclador Manage Payment Status
    private void createPaymentAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment, CreatePaymentToCreditCommand command) {
        command.getMediator().send(new CreatePaymentAttachmentStatusHistoryCommand(payment, employeeDto));
    }

}
