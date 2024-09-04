package com.kynsoft.finamer.payment.application.command.paymentDetailSplitDeposit.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckDepositToSplitRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckDepositTransactionTypeRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckPaymentDetailAmountSplitGreaterThanZeroRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckPaymentDetailDepositTypeIsApplyRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckSplitAmountRule;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;
import org.springframework.stereotype.Component;

@Component
public class CreatePaymentDetailSplitDepositCommandHandler implements ICommandHandler<CreatePaymentDetailSplitDepositCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentService paymentService;

    private final IManageEmployeeService manageEmployeeService;

    private final IPaymentStatusHistoryService paymentAttachmentStatusHistoryService;

    public CreatePaymentDetailSplitDepositCommandHandler(IPaymentDetailService paymentDetailService,
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
    public void handle(CreatePaymentDetailSplitDepositCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getEmployee(), "id", "Employee ID cannot be null."));

        ManageEmployeeDto employeeDto = this.manageEmployeeService.findById(command.getEmployee());

        //El valor ingresado no debe ser cero.
        RulesChecker.checkRule(new CheckPaymentDetailAmountSplitGreaterThanZeroRule(command.getAmount()));

        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = this.paymentTransactionTypeService.findById(command.getTransactionType());
        RulesChecker.checkRule(new CheckDepositTransactionTypeRule(paymentTransactionTypeDto.getDeposit()));

        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());
        //Valida que la trx seleccionada sea de tipo deposit
        RulesChecker.checkRule(new CheckDepositToSplitRule(paymentDetailDto.getTransactionType().getDeposit()));
        //Valida que la trx seleccionada tenga balance al momento de aplicar el split.
        RulesChecker.checkRule(new CheckSplitAmountRule(command.getAmount(), paymentDetailDto.getAmount()));
        //Valida que no exista un Apply Deposit asociado a dicho deposito.
        RulesChecker.checkRule(new CheckPaymentDetailDepositTypeIsApplyRule(paymentDetailDto.getChildren()));

        ConsumerUpdate updatePayment = new ConsumerUpdate();
        UpdateIfNotNull.updateDouble(paymentDetailDto::setAmount, paymentDetailDto.getAmount() + command.getAmount(), updatePayment::setUpdate);

        PaymentDetailDto split = new PaymentDetailDto(
                command.getId(),
                command.getStatus(),
                paymentDetailDto.getPayment(),
                paymentTransactionTypeDto,
                command.getAmount() * -1,
                command.getRemark(),
                null,
                null,
                null,
                paymentDetailDto.getTransactionDate(),
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
        split.setApplyDepositValue(command.getAmount());
        Long paymentDetailId = paymentDetailService.create(split);
        paymentDetailService.update(paymentDetailDto);

//        createPaymentAttachmentStatusHistory(employeeDto, paymentDetailDto.getPayment(), paymentDetailId, "Creating Split ANTI with ID: ");
        command.setPaymentResponse(this.paymentService.findById(paymentDetailDto.getPayment().getId()));
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
