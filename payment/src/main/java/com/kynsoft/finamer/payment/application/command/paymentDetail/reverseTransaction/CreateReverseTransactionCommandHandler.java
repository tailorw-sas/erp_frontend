package com.kynsoft.finamer.payment.application.command.paymentDetail.reverseTransaction;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.application.command.paymentDetail.undoApplyPayment.UndoApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.rules.undoApplication.CheckApplyPaymentRule;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CreateReverseTransactionCommandHandler implements ICommandHandler<CreateReverseTransactionCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IPaymentService paymentService;
    private final IManageEmployeeService employeeService;
    private final IPaymentStatusHistoryService paymentAttachmentStatusHistoryService;
    private final IManagePaymentStatusService paymentStatusService;
    private final IPaymentCloseOperationService paymentCloseOperationService;

    public CreateReverseTransactionCommandHandler(IPaymentDetailService paymentDetailService,
                                                IPaymentService paymentService,
                                                IManageEmployeeService employeeService,
                                                IPaymentStatusHistoryService paymentAttachmentStatusHistoryService,
                                                IManagePaymentStatusService paymentStatusService,
                                                IPaymentCloseOperationService paymentCloseOperationService) {

        this.paymentDetailService = paymentDetailService;
        this.paymentService = paymentService;
        this.employeeService = employeeService;
        this.paymentAttachmentStatusHistoryService = paymentAttachmentStatusHistoryService;
        this.paymentStatusService = paymentStatusService;
        this.paymentCloseOperationService = paymentCloseOperationService;
    }

    @Override
    public void handle(CreateReverseTransactionCommand command) {
        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());
        RulesChecker.checkRule(new CheckApplyPaymentRule(paymentDetailDto.getApplayPayment()));
        //Comprobar que la fecha sea anterior al dia actual
        //Comprobar que el paymentDetails sea de tipo Apply Deposit o Cash
        PaymentDetailDto reverseFrom = new PaymentDetailDto(
                UUID.randomUUID(),
                paymentDetailDto.getStatus(),
                paymentDetailDto.getPayment(),
                paymentDetailDto.getTransactionType(),
                paymentDetailDto.getAmount() * -1,
                paymentDetailDto.getRemark(),
                null,
                null,
                null,
                transactionDate(paymentDetailDto.getPayment().getHotel().getId()),
                //OffsetDateTime.now(ZoneId.of("UTC")),
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );

        reverseFrom.setManageBooking(paymentDetailDto.getManageBooking());
        reverseFrom.setApplayPayment(Boolean.TRUE);
        reverseFrom.setReverseTransaction(true);
        this.paymentDetailService.create(reverseFrom);

        if (paymentDetailDto.getTransactionType().getApplyDeposit()) {
            reverseFrom.setReverseFrom(paymentDetailDto.getPaymentDetailId());
            reverseFrom.setReverseFromParentId(paymentDetailDto.getPaymentDetailId());
            reverseFrom.setParentId(paymentDetailDto.getParentId());
            this.addChildren(reverseFrom, paymentDetailDto.getParentId());
            this.calculateReverseApplyDeposit(reverseFrom.getPayment(), reverseFrom);
            this.paymentDetailService.update(reverseFrom);

            this.changeStatus(paymentDetailDto, command.getEmployee());
        } else if (paymentDetailDto.getTransactionType().getCash()) {
            this.calculateReverseCash(reverseFrom.getPayment(), reverseFrom.getAmount());
            reverseFrom.setReverseFromParentId(paymentDetailDto.getPaymentDetailId());
            this.paymentDetailService.update(reverseFrom);
            //paymentDetailDto.setTransactionDate(null);

            this.changeStatus(paymentDetailDto, command.getEmployee());
        } else {
            reverseFrom.setReverseFromParentId(paymentDetailDto.getPaymentDetailId());
            this.paymentDetailService.update(reverseFrom);
            this.calculateReverseOtherDeductions(reverseFrom.getPayment(), reverseFrom.getAmount());

            this.changeStatus(paymentDetailDto, command.getEmployee());
        }

        ManageBookingDto bookingDto = paymentDetailDto.getManageBooking();
        paymentDetailDto.setReverseTransaction(true);
        //paymentDetailDto.setManageBooking(null);
        this.paymentDetailService.update(paymentDetailDto);

        command.getMediator().send(new UndoApplyPaymentDetailCommand(command.getPaymentDetail(), bookingDto.getId()));
    }

    private OffsetDateTime transactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIds(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

    private void addChildren(PaymentDetailDto reverseFrom, Long parentId) {
        PaymentDetailDto parent = this.paymentDetailService.findByPaymentDetailId(parentId);
        List<PaymentDetailDto> updateChildrens = new ArrayList<>();
        updateChildrens.addAll(parent.getChildren());
        updateChildrens.add(reverseFrom);
        parent.setChildren(updateChildrens);
        parent.setApplyDepositValue(parent.getApplyDepositValue() - reverseFrom.getAmount());

        this.paymentDetailService.update(parent);
    }

    private void calculateReverseApplyDeposit(PaymentDto paymentDto, PaymentDetailDto newDetailDto) {
        paymentDto.setDepositBalance(paymentDto.getDepositBalance() - newDetailDto.getAmount());
        paymentDto.setNotApplied(paymentDto.getNotApplied() + newDetailDto.getAmount()); // TODO: al hacer un applied deposit el notApplied aumenta.
        paymentDto.setIdentified(paymentDto.getIdentified() + newDetailDto.getAmount());
        paymentDto.setNotIdentified(paymentDto.getPaymentAmount() - paymentDto.getIdentified());

        this.paymentService.update(paymentDto);
    }

    private void calculateReverseOtherDeductions(PaymentDto paymentDto, double amount) {
        paymentDto.setOtherDeductions(paymentDto.getOtherDeductions() + amount);

        this.paymentService.update(paymentDto);
    }

    private void calculateReverseCash(PaymentDto paymentDto, double amount) {
        paymentDto.setIdentified(paymentDto.getIdentified() + amount);
        paymentDto.setNotIdentified(paymentDto.getNotIdentified() - amount);

        paymentDto.setApplied(paymentDto.getApplied() + amount);
        paymentDto.setNotApplied(paymentDto.getNotApplied() - amount);
        paymentDto.setPaymentBalance(paymentDto.getPaymentBalance() - amount);

        this.paymentService.update(paymentDto);
    }

    private void changeStatus(PaymentDetailDto paymentDetailDto, UUID employee) {

        if (paymentDetailDto.getPayment().getPaymentStatus().getApplied()) {
            PaymentDto payment = this.paymentService.findById(paymentDetailDto.getPayment().getId());
            payment.setPaymentStatus(this.paymentStatusService.findByConfirmed());
            this.paymentService.update(payment);
            createPaymentAttachmentStatusHistory(employee, payment);
        }
    }

    //Este es para agregar el History del Payment. Aqui el estado es el del nomenclador Manage Payment Status
    private void createPaymentAttachmentStatusHistory(UUID employee, PaymentDto payment) {

        ManageEmployeeDto employeeDto = employee != null ? this.employeeService.findById(employee) : null;
        PaymentStatusHistoryDto attachmentStatusHistoryDto = new PaymentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("Update Payment.");
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(payment.getPaymentStatus().getCode() + "-" + payment.getPaymentStatus().getName());

        this.paymentAttachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }

}
