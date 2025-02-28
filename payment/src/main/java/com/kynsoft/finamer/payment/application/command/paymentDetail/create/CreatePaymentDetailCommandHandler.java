package com.kynsoft.finamer.payment.application.command.paymentDetail.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.application.command.managePaymentTransactionType.create.CreateManagePaymentTransactionTypeCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyOtherDeductions.CreateApplyOtherDeductionsBookingRequest;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyOtherDeductions.CreateApplyOtherDeductionsCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment.ApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment.ApplyPaymentDetailMessage;
import com.kynsoft.finamer.payment.application.query.http.setting.paymenteTransactionType.ManagePaymentTransactionTypeRequest;
import com.kynsoft.finamer.payment.application.query.http.setting.paymenteTransactionType.ManagePaymentTransactionTypeResponse;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckAmountGreaterThanZeroStrictlyRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckAmountIfGreaterThanPaymentBalanceRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckIfNewPaymentDetailIsApplyDepositRule;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckPaymentDetailAmountGreaterThanZeroRule;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.services.http.PaymentTransactionTypeHttpService;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class CreatePaymentDetailCommandHandler implements ICommandHandler<CreatePaymentDetailCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentService paymentService;
    private final PaymentTransactionTypeHttpService paymentTransactionTypeHttpService;
    private final IManagePaymentStatusService statusService;

    public CreatePaymentDetailCommandHandler(IPaymentDetailService paymentDetailService,
            IManagePaymentTransactionTypeService paymentTransactionTypeService,
            IPaymentService paymentService, PaymentTransactionTypeHttpService paymentTransactionTypeHttpService,
            IManagePaymentStatusService statusService) {
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentService = paymentService;
        this.paymentTransactionTypeHttpService = paymentTransactionTypeHttpService;
        this.statusService = statusService;
    }

    @Override
    //@Transactional
    public void handle(CreatePaymentDetailCommand command) {

        ManagePaymentTransactionTypeDto paymentTransactionTypeDto = null;
        try {
            paymentTransactionTypeDto = this.paymentTransactionTypeService.findById(command.getTransactionType());
        } catch (Exception e) {
            //Esto es un flujo alternativo, si en algun momento kafka funciona y el proceso no encuentra el
            //Payment Transaction Type, se busca en setting para insertar.
            ManagePaymentTransactionTypeResponse response = paymentTransactionTypeHttpService.sendAccountStatement(new ManagePaymentTransactionTypeRequest(command.getTransactionType()));
            command.getMediator().send(CreateManagePaymentTransactionTypeCommand.fromRequest(response));
            paymentTransactionTypeDto = response.createObject();
        }

        PaymentDto paymentDto = this.paymentService.findById(command.getPayment());

        ConsumerUpdate updatePayment = new ConsumerUpdate();

        RulesChecker.checkRule(new CheckPaymentDetailAmountGreaterThanZeroRule(command.getAmount()));
        RulesChecker.checkRule(new CheckIfNewPaymentDetailIsApplyDepositRule(paymentTransactionTypeDto.getApplyDeposit()));

        //identified and notIdentified
        if (paymentTransactionTypeDto.getCash()) {
            RulesChecker.checkRule(new CheckAmountGreaterThanZeroStrictlyRule(command.getAmount(), paymentDto.getPaymentBalance()));
            UpdateIfNotNull.updateDouble(paymentDto::setIdentified, paymentDto.getIdentified() + command.getAmount(), updatePayment::setUpdate);
            UpdateIfNotNull.updateDouble(paymentDto::setNotIdentified, paymentDto.getNotIdentified() - command.getAmount(), updatePayment::setUpdate);

            //Suma de trx tipo check Cash + Check Apply Deposit  en el Manage Payment Transaction Type
            UpdateIfNotNull.updateDouble(paymentDto::setApplied, paymentDto.getApplied() + command.getAmount(), updatePayment::setUpdate);

            //Las transacciones de tipo Cash se restan al Payment Balance.
            UpdateIfNotNull.updateDouble(paymentDto::setPaymentBalance, paymentDto.getPaymentBalance() - command.getAmount(), updatePayment::setUpdate);
            UpdateIfNotNull.updateDouble(paymentDto::setNotApplied, paymentDto.getNotApplied() - command.getAmount(), updatePayment::setUpdate);

            //Aplicando regla para el campo Remark
            if (!paymentTransactionTypeDto.getRemarkRequired()) {
                //RulesChecker.checkRule(new CheckMinNumberOfCharacterInRemarkRule(paymentTransactionTypeDto.getMinNumberOfCharacter(), command.getRemark()));
//                command.setRemark(paymentTransactionTypeDto.getDefaultRemark());
            }
        }

        //Other Deductions
        boolean otherDeductionAndApplyPayment = !paymentTransactionTypeDto.getCash() && !paymentTransactionTypeDto.getDeposit() && command.getApplyPayment();
        if (!paymentTransactionTypeDto.getCash() && !paymentTransactionTypeDto.getDeposit() && !command.getApplyPayment()) {
            UpdateIfNotNull.updateDouble(paymentDto::setOtherDeductions, paymentDto.getOtherDeductions() + command.getAmount(), updatePayment::setUpdate);

            //Aplicando regla para el campo Remark
            if (!paymentTransactionTypeDto.getRemarkRequired()) {
                //RulesChecker.checkRule(new CheckMinNumberOfCharacterInRemarkRule(paymentTransactionTypeDto.getMinNumberOfCharacter(), command.getRemark()));
//                command.setRemark(paymentTransactionTypeDto.getDefaultRemark());
            }

        }

        //Deposit Amount and Deposit Balance
        PaymentDetailDto newDetailDto = new PaymentDetailDto(
                command.getId(),
                command.getStatus() != null ? command.getStatus() : Status.ACTIVE,
                paymentDto,
                paymentTransactionTypeDto,
                command.getAmount(),
                command.getRemark(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
        if (paymentTransactionTypeDto.getDeposit()) {
            // Crear regla que valide que el Amount ingresado no debe de ser mayor que el valor del Payment Balance y mayor que cero.
            RulesChecker.checkRule(new CheckAmountIfGreaterThanPaymentBalanceRule(command.getAmount(), paymentDto.getPaymentBalance(), paymentDto.getDepositAmount()));
            UpdateIfNotNull.updateDouble(paymentDto::setDepositAmount, paymentDto.getDepositAmount() + command.getAmount(), updatePayment::setUpdate);
            UpdateIfNotNull.updateDouble(paymentDto::setDepositBalance, paymentDto.getDepositBalance() + command.getAmount(), updatePayment::setUpdate);
            UpdateIfNotNull.updateBoolean(paymentDto::setHasDetailTypeDeposit, true, paymentDto.isHasDetailTypeDeposit(), updatePayment::setUpdate);
            if (paymentDto.getNotApplied() == null) {
                paymentDto.setNotApplied(0.0);
            }
            UpdateIfNotNull.updateDouble(paymentDto::setNotApplied, paymentDto.getNotApplied() - command.getAmount(), updatePayment::setUpdate);
            //Los Deposit deben de ser restados del Payment Balance, pero si sobre un Detalle de tipo Deposit se realiza Apply Deposit, ese valor hay que devolverselo al Payment Balance.
            UpdateIfNotNull.updateDouble(paymentDto::setPaymentBalance, paymentDto.getPaymentBalance() - command.getAmount(), updatePayment::setUpdate);
            newDetailDto.setAmount(command.getAmount() * -1);
            newDetailDto.setApplyDepositValue(command.getAmount());
            //Validar el Close Operation
            newDetailDto.setTransactionDate(OffsetDateTime.now(ZoneId.of("UTC")));
        }

        if (!otherDeductionAndApplyPayment) {
            this.paymentDetailService.create(newDetailDto);
        }

        if (updatePayment.getUpdate() > 0) {
            if (paymentDto.getPaymentBalance() == 0 && paymentDto.getDepositBalance() == 0) {
                paymentDto.setPaymentStatus(this.statusService.findByApplied());
            }
            this.paymentService.update(paymentDto);
//            createPaymentAttachmentStatusHistory(employeeDto, paymentDto, paymentDetail, msg);
        }
        if (command.getApplyPayment() && paymentTransactionTypeDto.getCash()) {
            ApplyPaymentDetailMessage message = command.getMediator().send(new ApplyPaymentDetailCommand(command.getId(), command.getBooking(), command.getEmployee()));
            paymentDto.setApplyPayment(message.getPayment().isApplyPayment());
            paymentDto.setPaymentStatus(message.getPayment().getPaymentStatus());
        }
        if (command.getApplyPayment() && !paymentTransactionTypeDto.getCash() && !paymentTransactionTypeDto.getDeposit()) {
            List<CreateApplyOtherDeductionsBookingRequest> booking = new ArrayList<>();
            CreateApplyOtherDeductionsBookingRequest b = new CreateApplyOtherDeductionsBookingRequest();
            b.setBookingId(command.getBooking());
            b.setBookingBalance(command.getAmount());
            booking.add(b);
            command.getMediator().send(new CreateApplyOtherDeductionsCommand(
                    command.getPayment(), 
                    command.getTransactionType(), 
                    command.getRemark(), 
                    booking, 
                    command.getMediator(), 
                    command.getEmployee()
            ));
        }
        command.setPaymentResponse(paymentDto);
    }
}
