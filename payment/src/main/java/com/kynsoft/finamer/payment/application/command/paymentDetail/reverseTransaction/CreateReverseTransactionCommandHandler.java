package com.kynsoft.finamer.payment.application.command.paymentDetail.reverseTransaction;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.application.command.paymentDetail.undoApplyPayment.UndoApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.domain.core.undoApplyPayment.UndoApplyPayment;
import com.kynsoft.finamer.payment.domain.domainServices.ReverseTransactionService;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckPaymentDetailReversedTransactionRule;
import com.kynsoft.finamer.payment.domain.rules.undoApplication.CheckApplyPaymentRule;
import com.kynsoft.finamer.payment.domain.services.*;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.undoApplicationUpdateBooking.ProducerUndoApplicationUpdateBookingService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class CreateReverseTransactionCommandHandler implements ICommandHandler<CreateReverseTransactionCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final ReverseTransactionService reverseTransactionService;

    private final ProducerUndoApplicationUpdateBookingService producerUndoApplicationUpdateBookingService;

    public CreateReverseTransactionCommandHandler(IPaymentDetailService paymentDetailService,
                                                  ReverseTransactionService reverseTransactionService,
                                                  ProducerUndoApplicationUpdateBookingService producerUndoApplicationUpdateBookingService) {

        this.paymentDetailService = paymentDetailService;
        this.reverseTransactionService = reverseTransactionService;
        this.producerUndoApplicationUpdateBookingService = producerUndoApplicationUpdateBookingService;
    }

    @Override
    public void handle(CreateReverseTransactionCommand command) {
        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());
        PaymentDto payment = paymentDetailDto.getPayment();
        ManageBookingDto bookingDto = paymentDetailDto.getManageBooking();

        RulesChecker.checkRule(new CheckApplyPaymentRule(paymentDetailDto.getApplyPayment()));
        RulesChecker.checkRule(new CheckPaymentDetailReversedTransactionRule(paymentDetailDto));
        //Comprobar que la fecha sea anterior al dia actual
        //Comprobar que el paymentDetails sea de tipo Apply Deposit o Cash, pero puede ser de other deductions
        //Lo que no puede suceder es que si es other deductions cambie el estado del payment.

        this.reverseTransactionService.reverseTransaction(paymentDetailDto, payment, command.getEmployee(), bookingDto);
        this.replicateBooking(payment, paymentDetailDto, bookingDto);
    }



    private void replicateBooking(PaymentDto paymentDto, PaymentDetailDto paymentDetailDto, ManageBookingDto bookingDto){
        try {
            UpdateBookingBalanceKafka updateBookingBalanceKafka = new UpdateBookingBalanceKafka(bookingDto.getId(),
                    bookingDto.getAmountBalance(),
                    new ReplicatePaymentKafka(
                            paymentDto.getId(),
                            paymentDto.getPaymentId(),
                            new ReplicatePaymentDetailsKafka(paymentDetailDto.getId(), paymentDetailDto.getPaymentDetailId()
                            )),
                    paymentDetailDto.getTransactionType().getApplyDeposit(), OffsetDateTime.now());
            this.producerUndoApplicationUpdateBookingService.update(updateBookingBalanceKafka);
        } catch (Exception e) {
            Logger.getLogger(CreateReverseTransactionCommandHandler.class.getName()).log(Level.SEVERE, "Error trying to replicate booking", e);
        }
    }

}
