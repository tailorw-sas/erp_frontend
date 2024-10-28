package com.kynsoft.finamer.payment.application.command.paymentImport.detail.applyDeposit;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.CreateIncomeTransactionKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckApplyDepositRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckDepositToApplyDepositRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckGreaterThanOrEqualToTheTransactionAmountRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckPaymentDetailAmountGreaterThanZeroRule;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.createIncomeTransaction.ProducerCreateIncomeTransactionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CreatePaymentDetailApplyDepositFromFileCommandHandler implements ICommandHandler<CreatePaymentDetailApplyDepositFromFileCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentService paymentService;
    private final IManageEmployeeService manageEmployeeService;
    private final IManageInvoiceStatusService statusService;
    private final ProducerCreateIncomeTransactionService producerCreateIncomeService;

    @Value("${payment.relate.invoice.status.code}")
    private String RELATE_INCOME_STATUS_CODE;

    public CreatePaymentDetailApplyDepositFromFileCommandHandler(IPaymentDetailService paymentDetailService,
                                                                 IManagePaymentTransactionTypeService paymentTransactionTypeService,
                                                                 IPaymentService paymentService,
                                                                 IManageEmployeeService manageEmployeeService,
                                                                 IPaymentStatusHistoryService paymentAttachmentStatusHistoryService, IManageInvoiceStatusService statusService,
                                                                 ProducerCreateIncomeTransactionService producerCreateIncomeService) {
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentService = paymentService;
        this.manageEmployeeService = manageEmployeeService;
        this.statusService = statusService;
        this.producerCreateIncomeService = producerCreateIncomeService;
    }

    @Override
    public void handle(CreatePaymentDetailApplyDepositFromFileCommand command) {
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
                true
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
        command.setPaymentResponse(paymentUpdate);
        ManageInvoiceStatusDto manageInvoiceStatusDto = statusService.findByCode(RELATE_INCOME_STATUS_CODE);
        this.sendToCreateRelatedIncome(children, employeeDto.getFirstName(), command.getTransactionTypeForAdjustment(),manageInvoiceStatusDto.getId());

    }

    private void sendToCreateRelatedIncome(PaymentDetailDto paymentDetailDto, String employeeName, UUID transactionType,UUID status) {
        PaymentDto paymentDto = paymentDetailDto.getPayment();
        CreateIncomeTransactionKafka createIncomeTransactionSuccessKafka = new CreateIncomeTransactionKafka();
        UUID incomeId = UUID.randomUUID();
        createIncomeTransactionSuccessKafka.setId(incomeId);
        createIncomeTransactionSuccessKafka.setAgency(paymentDto.getAgency().getId());
        createIncomeTransactionSuccessKafka.setHotel(paymentDto.getHotel().getId());
        createIncomeTransactionSuccessKafka.setInvoiceDate(LocalDateTime.now());
        createIncomeTransactionSuccessKafka.setIncomeAmount(paymentDetailDto.getAmount());
        createIncomeTransactionSuccessKafka.setManual(false);
        createIncomeTransactionSuccessKafka.setInvoiceStatus(status);
        createIncomeTransactionSuccessKafka.setStatus(Status.ACTIVE.name());
        createIncomeTransactionSuccessKafka.setTransactionTypeAdjustment(transactionType);
        createIncomeTransactionSuccessKafka.setEmployeeAdjustment(employeeName);
        createIncomeTransactionSuccessKafka.setDateAdjustment(LocalDate.now());
        createIncomeTransactionSuccessKafka.setRelatedPaymentDetail(paymentDetailDto.getId());
        createIncomeTransactionSuccessKafka.setStatusAdjustment(Status.ACTIVE.name());
        producerCreateIncomeService.create(createIncomeTransactionSuccessKafka);
    }


}
