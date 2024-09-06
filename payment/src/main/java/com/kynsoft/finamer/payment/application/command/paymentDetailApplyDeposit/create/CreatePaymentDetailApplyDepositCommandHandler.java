package com.kynsoft.finamer.payment.application.command.paymentDetailApplyDeposit.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment.ApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckApplyDepositRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckDepositToApplyDepositRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckGreaterThanOrEqualToTheTransactionAmountRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckPaymentDetailAmountGreaterThanZeroRule;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

@Component
public class CreatePaymentDetailApplyDepositCommandHandler implements ICommandHandler<CreatePaymentDetailApplyDepositCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentService paymentService;

    private final IManageEmployeeService manageEmployeeService;

    private final IPaymentStatusHistoryService paymentAttachmentStatusHistoryService;

    public CreatePaymentDetailApplyDepositCommandHandler(IPaymentDetailService paymentDetailService,
            IManagePaymentTransactionTypeService paymentTransactionTypeService,
            IPaymentService paymentService,
            IManageEmployeeService manageEmployeeService,
            IPaymentStatusHistoryService paymentAttachmentStatusHistoryService) {
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentService = paymentService;
        this.manageEmployeeService = manageEmployeeService;
        this.paymentAttachmentStatusHistoryService = paymentAttachmentStatusHistoryService;
    }

    @Override
    public void handle(CreatePaymentDetailApplyDepositCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getEmployee(), "id", "Employee ID cannot be null."));

        ManageEmployeeDto employeeDto = this.manageEmployeeService.findById(command.getEmployee());

        RulesChecker.checkRule(new CheckPaymentDetailAmountGreaterThanZeroRule(command.getAmount()));

        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = this.paymentTransactionTypeService.findById(command.getTransactionType());
        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());
        PaymentDto paymentUpdate = paymentDetailDto.getPayment();

        RulesChecker.checkRule(new CheckApplyDepositRule(paymentTransactionTypeDto.getApplyDeposit()));
        RulesChecker.checkRule(new CheckDepositToApplyDepositRule(paymentDetailDto.getTransactionType().getDeposit()));
        //RulesChecker.checkRule(new CheckAmountIfDepositBalanceGreaterThanZeroRule(command.getAmount(), paymentUpdate.getDepositBalance()));
        RulesChecker.checkRule(new CheckGreaterThanOrEqualToTheTransactionAmountRule(command.getAmount(), paymentDetailDto.getApplyDepositValue()));

        ConsumerUpdate updatePayment = new ConsumerUpdate();
        //Cuando se creo el Payment Details de Tipo Deposit se resto del Payment Balance el Amount, si se le realiza Apply Deposit, este valor debe de ser sumado al Payment Balance.
        //UpdateIfNotNull.updateDouble(paymentUpdate::setPaymentBalance, paymentUpdate.getPaymentBalance() + (- paymentDetailDto.getAmount()), updatePayment::setUpdate);

        UpdateIfNotNull.updateDouble(paymentUpdate::setDepositBalance, paymentUpdate.getDepositBalance() - command.getAmount(), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setNotApplied, paymentUpdate.getNotApplied() + command.getAmount(), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setIdentified, paymentUpdate.getIdentified() + command.getAmount(), updatePayment::setUpdate);
        UpdateIfNotNull.updateDouble(paymentUpdate::setNotIdentified, paymentUpdate.getPaymentAmount() - paymentUpdate.getIdentified(), updatePayment::setUpdate);

        //TODO: Se debe de validar esta variable para que cumpla con el Close Operation
        OffsetDateTime transactionDate = OffsetDateTime.now(ZoneId.of("UTC"));
        PaymentDetailDto children = new PaymentDetailDto(
                command.getId(),
                command.getStatus(),
                paymentUpdate,
                paymentTransactionTypeDto,
                command.getAmount(),
                command.getRemark(),
                null,
                null,
                null,
                transactionDate,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );

        children.setParentId(paymentDetailDto.getPaymentDetailId());
        this.paymentDetailService.create(children);

        List<PaymentDetailDto> updateChildrens = new ArrayList<>();
        updateChildrens.addAll(paymentDetailDto.getChildren());
        updateChildrens.add(children);
        paymentDetailDto.setChildren(updateChildrens);
        paymentDetailDto.setApplyDepositValue(paymentDetailDto.getApplyDepositValue() - command.getAmount());
        paymentDetailService.update(paymentDetailDto);

        this.paymentService.update(paymentUpdate);

        if (Objects.nonNull(command.getApplyPayment()) && command.getApplyPayment()) {
            command.getMediator().send(new ApplyPaymentDetailCommand(command.getId(), command.getBooking()));
        }

//        createPaymentAttachmentStatusHistory(employeeDto, paymentUpdate, paymentDetailId, "Creating New Apply Deposit with ID: ");
        command.setPaymentResponse(paymentUpdate);

    }

    //Este es para agregar el History del Payment. Aqui el estado es el del nomenclador Manage Payment Status
//    private void createPaymentAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment, Long paymentDetail, String msg) {
//
//        PaymentStatusHistoryDto attachmentStatusHistoryDto = new PaymentStatusHistoryDto();
//        attachmentStatusHistoryDto.setId(UUID.randomUUID());
//        attachmentStatusHistoryDto.setDescription(msg + paymentDetail);
//        attachmentStatusHistoryDto.setEmployee(employeeDto);
//        attachmentStatusHistoryDto.setPayment(payment);
//        attachmentStatusHistoryDto.setStatus(payment.getPaymentStatus().getCode() + "-" + payment.getPaymentStatus().getName());
//
//        this.paymentAttachmentStatusHistoryService.create(attachmentStatusHistoryDto);
//    }
}
