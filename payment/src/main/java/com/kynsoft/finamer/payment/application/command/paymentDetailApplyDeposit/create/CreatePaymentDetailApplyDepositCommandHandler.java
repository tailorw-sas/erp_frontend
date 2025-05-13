package com.kynsoft.finamer.payment.application.command.paymentDetailApplyDeposit.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicateBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.application.command.paymentDetail.create.CreatePaymentDetailCommandHandler;
import com.kynsoft.finamer.payment.application.services.paymentDetail.apply.ReplicateBookingBalanceService;
import com.kynsoft.finamer.payment.application.services.paymentDetail.create.CreatePaymentDetailService;
import com.kynsoft.finamer.payment.domain.core.applyPayment.ProcessApplyPaymentDetail;
import com.kynsoft.finamer.payment.domain.core.paymentDetail.ProcessCreatePaymentDetail;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dto.helper.ReplicateBookingBalanceHelper;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.*;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateBookingService;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class CreatePaymentDetailApplyDepositCommandHandler implements ICommandHandler<CreatePaymentDetailApplyDepositCommand> {

    private final CreatePaymentDetailService createPaymentDetailService;
    private final ReplicateBookingBalanceService replicateBookingBalanceService;

    public CreatePaymentDetailApplyDepositCommandHandler(CreatePaymentDetailService createPaymentDetailService,
                                                         ReplicateBookingBalanceService replicateBookingBalanceService) {
        this.createPaymentDetailService = createPaymentDetailService;
        this.replicateBookingBalanceService = replicateBookingBalanceService;
    }

    @Override
    public void handle(CreatePaymentDetailApplyDepositCommand command) {
        RulesChecker.checkRule(new CheckPaymentDetailAmountGreaterThanZeroRule(command.getAmount()));

        PaymentDetailDto paymentDetail = this.createPaymentDetailService.create(command.getId(),
                command.getEmployee(),
                command.getStatus(),
                null,
                command.getTransactionType(),
                command.getAmount(),
                command.getRemark(),
                command.getBooking(),
                command.getApplyPayment(),
                command.getMediator(),
                command.getPaymentDetail());

        PaymentDto payment = createPaymentDetailService.getPayment();
        ManageBookingDto booking = createPaymentDetailService.getBooking();

        if(command.getApplyPayment()){
            List<ReplicateBookingBalanceHelper> replicateBookingBalanceHelpers = ReplicateBookingBalanceHelper.from(booking, false);
            this.replicateBookingBalanceService.replicateBooking(replicateBookingBalanceHelpers);
        }

        command.setPaymentResponse(payment);
    }
}
