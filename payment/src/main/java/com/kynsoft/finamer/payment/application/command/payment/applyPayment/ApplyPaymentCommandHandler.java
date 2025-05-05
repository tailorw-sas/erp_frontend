package com.kynsoft.finamer.payment.application.command.payment.applyPayment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.http.entity.InvoiceHttp;
import com.kynsof.share.core.domain.kafka.entity.ReplicateBookingKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.payment.domain.core.paymentDetail.ProcessCreatePaymentDetail;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;
import com.kynsoft.finamer.payment.infrastructure.identity.*;
import com.kynsoft.finamer.payment.infrastructure.services.http.InvoiceHttpUUIDService;
import com.kynsoft.finamer.payment.infrastructure.services.http.helper.InvoiceImportAutomaticeHelperServiceImpl;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateListBookingService;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ApplyPaymentCommandHandler implements ICommandHandler<ApplyPaymentCommand> {



    public ApplyPaymentCommandHandler() {

    }

    @Override
    public void handle(ApplyPaymentCommand command) {

    }






    private void createPaymentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment) {

        PaymentStatusHistoryDto paymentStatusHistoryDto = new PaymentStatusHistoryDto();
        paymentStatusHistoryDto.setId(UUID.randomUUID());
        paymentStatusHistoryDto.setDescription("Update Payment.");
        paymentStatusHistoryDto.setEmployee(employeeDto);
        paymentStatusHistoryDto.setPayment(payment);
        paymentStatusHistoryDto.setStatus(payment.getPaymentStatus().getCode() + "-" + payment.getPaymentStatus().getName());

        this.paymentStatusHistoryService.create(paymentStatusHistoryDto);
    }

    public PaymentDetail createDetailsTypeApplyDeposit(PaymentDetail parentDetail, double amount, ManagePaymentTransactionType transactionTypeDto,
            List<PaymentDetail> detailTypeDeposits, Payment updatePayment, OffsetDateTime transactionDate) {

        this.calculateApplyDeposit(amount, updatePayment);

        //Create detail Apply Deposit.
        PaymentDetail children = new PaymentDetail();
        children.setId(UUID.randomUUID());
        children.setStatus(Status.ACTIVE);
        children.setPayment(parentDetail.getPayment());
        children.setTransactionType(transactionTypeDto);
        children.setAmount(amount);
        children.setRemark(transactionTypeDto.getDefaultRemark());
        children.setTransactionDate(transactionDate);

        //Set Parent
        children.setParentId(parentDetail.getPaymentDetailId());

        //Add los Apply Deposit.
        List<PaymentDetail> updateChildren = new ArrayList<>(parentDetail.getPaymentDetails());
        updateChildren.add(children);
        parentDetail.setPaymentDetails(updateChildren);
        parentDetail.setApplyDepositValue(BankerRounding.round(parentDetail.getApplyDepositValue() - amount));

        parentDetail.setUpdatedAt(OffsetDateTime.now());
        parentDetail.setAppliedAt(transactionDate);//TODO Preguntar si es factible setear el campo appliedAt al deposito del cual se esta creando el AANT.

        //Update Detail Deposit
        this.updatePaymentDetails(parentDetail, detailTypeDeposits);
        return children;
    }

    private void calculateApplyDeposit(double amount, Payment updatePayment) {
        updatePayment.setDepositBalance(BankerRounding.round(updatePayment.getDepositBalance() - amount));
        updatePayment.setApplied(BankerRounding.round(updatePayment.getApplied() + amount)); // TODO: Suma de trx tipo check Cash + Check Apply

        updatePayment.setIdentified(BankerRounding.round(updatePayment.getIdentified() + amount));
        updatePayment.setNotIdentified(BankerRounding.round(updatePayment.getNotIdentified() - amount));

    }



    private void updateBooking(Booking update, List<Booking> bookingsList) {
        int index = bookingsList.indexOf(update);
        if (index != -1) {
            bookingsList.set(index, update);
        }
    }

    private void updatePaymentDetails(PaymentDetail update, List<PaymentDetail> detailTypeDeposits) {
        int index = detailTypeDeposits.indexOf(update);
        if (index != -1) {
            detailTypeDeposits.set(index, update);
        }
    }

    private void updatePaymentStatus(Payment updatePayment){
        if (Objects.nonNull(updatePayment) && Objects.nonNull(updatePayment.getPaymentStatus())){
            this.paymentService.updateStatus(updatePayment.getId(), updatePayment.getPaymentStatus().getId());
        }
    }




}
