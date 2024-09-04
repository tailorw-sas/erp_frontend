package com.kynsoft.finamer.payment.application.command.payment.applyPayment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment.ApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeCash.CreatePaymentDetailTypeCashCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeCash.CreatePaymentDetailTypeCashMessage;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ApplyPaymentCommandHandler implements ICommandHandler<ApplyPaymentCommand> {

    private final IManageInvoiceService manageInvoiceService;
    private final IPaymentService paymentService;

    public ApplyPaymentCommandHandler(IPaymentService paymentService,
                                      IManageInvoiceService manageInvoiceService) {
        this.paymentService = paymentService;
        this.manageInvoiceService = manageInvoiceService;
    }

    @Override
    public void handle(ApplyPaymentCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getPayment(), "id", "Payment ID cannot be null."));
        PaymentDto paymentDto = this.paymentService.findById(command.getPayment());

        List<ManageInvoiceDto> invoiceQueue = new ArrayList<>();

        for (UUID invoice : command.getInvoices()) {
            invoiceQueue.add(this.manageInvoiceService.findById(invoice));
        }

        Collections.sort(invoiceQueue, Comparator.comparingDouble(m -> m.getInvoiceAmount()));

        double notApplied = paymentDto.getNotApplied();
        for (ManageInvoiceDto manageInvoiceDto : invoiceQueue) {
            List<ManageBookingDto> bookingDtos = new ArrayList<>();
            if (!invoiceQueue.isEmpty()) {
                bookingDtos.addAll(manageInvoiceDto.getBookings());
                Collections.sort(bookingDtos, Comparator.comparingDouble(m -> m.getAmountBalance()));
                for (ManageBookingDto bookingDto : bookingDtos) {
                    if (notApplied > 0) {
                        double amountToApply = Math.min(notApplied, bookingDto.getAmountBalance());
                        CreatePaymentDetailTypeCashMessage message = command.getMediator().send(new CreatePaymentDetailTypeCashCommand(paymentDto, bookingDto.getId(), amountToApply, true));
                        command.getMediator().send(new ApplyPaymentDetailCommand(message.getId(), bookingDto.getId()));
                        notApplied = notApplied - amountToApply;
                    } else {
                        break;
                    }
                }
            }
            if (notApplied == 0) {
                break;
            }
        }
    }

}
