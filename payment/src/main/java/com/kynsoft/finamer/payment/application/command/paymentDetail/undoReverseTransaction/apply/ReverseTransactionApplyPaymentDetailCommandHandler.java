package com.kynsoft.finamer.payment.application.command.paymentDetail.undoReverseTransaction.apply;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateBookingService;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ReverseTransactionApplyPaymentDetailCommandHandler implements ICommandHandler<ReverseTransactionApplyPaymentDetailCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManageBookingService manageBookingService;
    private final IPaymentService paymentService;
    private final ProducerUpdateBookingService producerUpdateBookingService;
    private final IManagePaymentStatusService statusService;
    private final IManageEmployeeService employeeService;
    private final IPaymentStatusHistoryService paymentAttachmentStatusHistoryService;

    public ReverseTransactionApplyPaymentDetailCommandHandler(IPaymentDetailService paymentDetailService,
            IManageBookingService manageBookingService,
            IPaymentService paymentService,
            ProducerUpdateBookingService producerUpdateBookingService,
            IManagePaymentStatusService statusService,
            IManageEmployeeService employeeService,
            IPaymentStatusHistoryService paymentAttachmentStatusHistoryService) {
        this.paymentDetailService = paymentDetailService;
        this.manageBookingService = manageBookingService;
        this.paymentService = paymentService;
        this.producerUpdateBookingService = producerUpdateBookingService;
        this.statusService = statusService;
        this.employeeService = employeeService;
        this.paymentAttachmentStatusHistoryService = paymentAttachmentStatusHistoryService;
    }

    @Override
    public void handle(ReverseTransactionApplyPaymentDetailCommand command) {
        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());
        ManageBookingDto bookingDto = this.manageBookingService.findById(paymentDetailDto.getManageBooking().getId());

        bookingDto.setAmountBalance(bookingDto.getAmountBalance() - paymentDetailDto.getAmount());
        paymentDetailDto.setApplayPayment(Boolean.TRUE);
        this.manageBookingService.update(bookingDto);
        this.paymentDetailService.update(paymentDetailDto);

        PaymentDto paymentDto = this.paymentService.findById(paymentDetailDto.getPayment().getId());
        try {
            ReplicatePaymentKafka paymentKafka = new ReplicatePaymentKafka(
                    paymentDto.getId(),
                    paymentDto.getPaymentId(),
                    new ReplicatePaymentDetailsKafka(paymentDetailDto.getId(), paymentDetailDto.getPaymentDetailId()
                    ));
            this.producerUpdateBookingService.update(new UpdateBookingBalanceKafka(bookingDto.getId(), paymentDetailDto.getAmount(), paymentKafka));
        } catch (Exception e) {
        }

        if (paymentDto.getNotApplied() == 0 && paymentDto.getDepositBalance() == 0 && !bookingDto.getInvoice().getInvoiceType().equals(EInvoiceType.CREDIT)) {
            paymentDto.setPaymentStatus(this.statusService.findByApplied());
            ManageEmployeeDto employeeDto = command.getEmployee() != null ? this.employeeService.findById(command.getEmployee()) : null;
            this.createPaymentAttachmentStatusHistory(employeeDto, paymentDto);
        }
        paymentDto.setApplyPayment(true);
        this.paymentService.update(paymentDto);

        command.setPaymentResponse(paymentDto);
    }

    //Este es para agregar el History del Payment. Aqui el estado es el del nomenclador Manage Payment Status
    private void createPaymentAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment) {

        PaymentStatusHistoryDto attachmentStatusHistoryDto = new PaymentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("Update Payment.");
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(payment.getPaymentStatus().getCode() + "-" + payment.getPaymentStatus().getName());

        this.paymentAttachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }

}
