package com.kynsoft.finamer.payment.application.command.payment.applyPayment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment.ApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeApplyDeposit.CreatePaymentDetailTypeApplyDepositCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeApplyDeposit.CreatePaymentDetailTypeApplyDepositMessage;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeCash.CreatePaymentDetailTypeCashCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeCash.CreatePaymentDetailTypeCashMessage;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ApplyPaymentCommandHandler implements ICommandHandler<ApplyPaymentCommand> {

    private final IManageInvoiceService manageInvoiceService;
    private final IPaymentService paymentService;
    private final IPaymentDetailService paymentDetailService;

    public ApplyPaymentCommandHandler(IPaymentService paymentService,
            IManageInvoiceService manageInvoiceService,
            IPaymentDetailService paymentDetailService) {
        this.paymentService = paymentService;
        this.manageInvoiceService = manageInvoiceService;
        this.paymentDetailService = paymentDetailService;
    }

    @Override
    public void handle(ApplyPaymentCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getPayment(), "id", "Payment ID cannot be null."));
        PaymentDto paymentDto = this.paymentService.findById(command.getPayment());

        List<ManageInvoiceDto> invoiceQueue = createInvoiceQueue(command);// Ordenar las Invoice

        double paymentBalance = paymentDto.getPaymentBalance();// PaymentBalance
        double notApplied = paymentDto.getNotApplied();// notApplied
        double depositBalance = paymentDto.getDepositBalance();
        for (ManageInvoiceDto manageInvoiceDto : invoiceQueue) {
            List<ManageBookingDto> bookingDtos = getSortedBookings(manageInvoiceDto);// Ordelar los Booking de la Invoice seleccionada.
            for (ManageBookingDto bookingDto : bookingDtos) {
                //TODO: almaceno el valor de Balance del Booking porque puede que no llegue a cero cuando el Payment Balance si lo haga. Y todavia
                //tenga valor el notApplied
                double amountBalance = bookingDto.getAmountBalance();
                if (notApplied > 0 && paymentBalance > 0 && command.isApplyPaymentBalance() && amountBalance > 0) {
                    double amountToApply = Math.min(notApplied, amountBalance);
                    CreatePaymentDetailTypeCashMessage message = command.getMediator().send(new CreatePaymentDetailTypeCashCommand(paymentDto, bookingDto.getId(), amountToApply, true, manageInvoiceDto.getInvoiceDate()));
                    command.getMediator().send(new ApplyPaymentDetailCommand(message.getId(), bookingDto.getId()));
                    notApplied = notApplied - amountToApply;
                    paymentBalance = paymentBalance - amountToApply;
                    amountBalance = amountBalance - amountToApply;
                }
                if ((notApplied == 0 && paymentBalance == 0 && command.isApplyDeposit() && amountBalance > 0 && depositBalance > 0)
                        || //Aqui aplica para cuando dentro del flujo se usa payment balance y deposit.
                        (command.isApplyDeposit() && !command.isApplyPaymentBalance() && amountBalance > 0 && depositBalance > 0)) {//TODO: este aplica para cuando se quiere aplicar solo a los deposit
                    if (command.getDeposits() != null && !command.getDeposits().isEmpty()) {
                        List<PaymentDetailDto> deposits = this.createPaymentDetailsTypeDepositQueue(command.getDeposits());
                        for (PaymentDetailDto paymentDetailTypeDeposit : deposits) {
                            double depositAmount = paymentDetailTypeDeposit.getApplyDepositValue();
                            if (depositAmount == 0) {
                                continue;
                            }
                            while (depositAmount > 0) {
                                double amountToApply = Math.min(depositAmount, amountBalance);// Debe de compararse con el amountBalance, porque puede venir de haber sido rebajado en el flujo anterior.
                                CreatePaymentDetailTypeApplyDepositMessage message = command.getMediator().send(new CreatePaymentDetailTypeApplyDepositCommand(paymentDto, amountToApply, paymentDetailTypeDeposit, true, manageInvoiceDto.getInvoiceDate()));// quite *-1
                                command.getMediator().send(new ApplyPaymentDetailCommand(message.getNewDetailDto().getId(), bookingDto.getId()));
                                depositAmount = depositAmount - amountToApply;
                                amountBalance = amountBalance - amountToApply;
                                depositBalance = depositBalance - amountToApply;
                                if (amountBalance == 0 || depositBalance == 0) {
                                    break;
                                }
                            }
                            if (amountBalance == 0 || depositBalance == 0) {
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
            if (notApplied == 0 && paymentBalance == 0 && depositBalance == 0) {
                break;
            }
        }
    }

    /**
     * Ordena los Deposit por su Apply Deposit Value.
     *
     * @param manageInvoiceDto
     * @return
     */
    private List<PaymentDetailDto> createPaymentDetailsTypeDepositQueue(List<UUID> deposits) {
        List<PaymentDetailDto> queue = new ArrayList<>();
        for (UUID d : deposits) {
            queue.add(this.paymentDetailService.findById(d));
        }

        Collections.sort(queue, Comparator.comparingDouble(m -> m.getApplyDepositValue()));
        return queue;
    }

    /**
     * Ordena los Booking de menor a mayor por su AmountBalance.
     *
     * @param manageInvoiceDto
     * @return
     */
    private List<ManageBookingDto> getSortedBookings(ManageInvoiceDto manageInvoiceDto) {
        List<ManageBookingDto> bookingDtos = new ArrayList<>();
        if (manageInvoiceDto.getBookings() != null && !manageInvoiceDto.getBookings().isEmpty()) {
            bookingDtos.addAll(manageInvoiceDto.getBookings());
            Collections.sort(bookingDtos, Comparator.comparingDouble(m -> m.getAmountBalance()));
        }
        return bookingDtos;
    }

    /**
     * Ordena las Invoice de menor a mayor por su InvoiceAmount.
     *
     * @param command
     * @return
     */
    private List<ManageInvoiceDto> createInvoiceQueue(ApplyPaymentCommand command) {
        List<ManageInvoiceDto> queue = new ArrayList<>();
        for (UUID invoice : command.getInvoices()) {
            queue.add(this.manageInvoiceService.findById(invoice));
        }

        Collections.sort(queue, Comparator.comparingDouble(m -> m.getInvoiceAmount()));
        return queue;
    }
}
