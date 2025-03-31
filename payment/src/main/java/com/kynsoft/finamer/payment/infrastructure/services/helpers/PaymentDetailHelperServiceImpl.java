package com.kynsoft.finamer.payment.infrastructure.services.helpers;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.rules.paymentDetail.CheckAmountIfGreaterThanPaymentBalanceRule;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.services.helpers.entity.CreatePaymentDetail;
import com.kynsoft.finamer.payment.infrastructure.services.helpers.entity.CreatePaymentDetailsRequest;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateBookingService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PaymentDetailHelperServiceImpl implements IPaymentDetailHelperService {

    private final IManagePaymentStatusService paymentStatusService;
    private final IPaymentDetailService paymentDetailService;
    private final IPaymentService paymentService;
    private final ProducerUpdateBookingService producerUpdateBookingService;
    private final IPaymentStatusHistoryService paymentStatusHistoryService;

    public PaymentDetailHelperServiceImpl(IManagePaymentStatusService paymentStatusService,
                                          IPaymentDetailService paymentDetailService,
                                          IPaymentService paymentService,
                                          ProducerUpdateBookingService producerUpdateBookingService,
                                          IPaymentStatusHistoryService paymentStatusHistoryService){
        this.paymentStatusService = paymentStatusService;
        this.paymentDetailService = paymentDetailService;
        this.paymentService = paymentService;
        this.producerUpdateBookingService = producerUpdateBookingService;
        this.paymentStatusHistoryService = paymentStatusHistoryService;
    }

    public void processPaymentDetails(CreatePaymentDetailsRequest request){
        if(Objects.isNull(request.getPaymentDetails()) || request.getPaymentDetails().isEmpty()){
            return;
        }

        List<PaymentDetailDto> paymentDetailsToCreate = new ArrayList<>();
        List<PaymentDto> paymentsToCreate = new ArrayList<>();
        List<PaymentStatusHistoryDto> paymentStatusHistories = new ArrayList<>();

        ManagePaymentStatusDto paymentStatusApplied = getPaymentStatusApplied();

        for(CreatePaymentDetail createPaymentDetail : request.getPaymentDetails()){
            PaymentDto paymentDto = createPaymentDetail.getPayment();
            ManagePaymentTransactionTypeDto paymentTransactionType = createPaymentDetail.getTransactionType();

            ConsumerUpdate updatePayment = new ConsumerUpdate();

            if(createPaymentDetail.getTransactionType().getCash()){
                UpdateIfNotNull.updateDouble(paymentDto::setIdentified, paymentDto.getIdentified() + createPaymentDetail.getAmount(), updatePayment::setUpdate);
                UpdateIfNotNull.updateDouble(paymentDto::setNotIdentified, paymentDto.getNotIdentified() - createPaymentDetail.getAmount(), updatePayment::setUpdate);

                //Suma de trx tipo check Cash + Check Apply Deposit  en el Manage Payment Transaction Type
                UpdateIfNotNull.updateDouble(paymentDto::setApplied, paymentDto.getApplied() + createPaymentDetail.getAmount(), updatePayment::setUpdate);

                //Las transacciones de tipo Cash se restan al Payment Balance.
                UpdateIfNotNull.updateDouble(paymentDto::setPaymentBalance, paymentDto.getPaymentBalance() - createPaymentDetail.getAmount(), updatePayment::setUpdate);
                UpdateIfNotNull.updateDouble(paymentDto::setNotApplied, paymentDto.getNotApplied() - createPaymentDetail.getAmount(), updatePayment::setUpdate);
            }

            //Other Deductions
            boolean otherDeductionAndApplyPayment = !paymentTransactionType.getCash() && !paymentTransactionType.getDeposit() && createPaymentDetail.getApplyPayment();
            if(!paymentTransactionType.getCash() && !paymentTransactionType.getDeposit() && !createPaymentDetail.getApplyPayment()){
                UpdateIfNotNull.updateDouble(paymentDto::setOtherDeductions, paymentDto.getOtherDeductions() + createPaymentDetail.getAmount(), updatePayment::setUpdate);
            }

            PaymentDetailDto newPaymentDetailDto = new PaymentDetailDto(
                    createPaymentDetail.getId(),
                    createPaymentDetail.getStatus() != null ? createPaymentDetail.getStatus() : Status.ACTIVE,
                    paymentDto,
                    paymentTransactionType,
                    createPaymentDetail.getAmount(),
                    createPaymentDetail.getRemark(),
                    null,
                    null,
                    null,
                    createPaymentDetail.getTransactionDate(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    false
            );

            if(paymentTransactionType.getDeposit()){
                RulesChecker.checkRule(new CheckAmountIfGreaterThanPaymentBalanceRule(createPaymentDetail.getAmount(), paymentDto.getPaymentBalance(), paymentDto.getDepositAmount()));
                UpdateIfNotNull.updateDouble(paymentDto::setDepositAmount, paymentDto.getDepositAmount() + createPaymentDetail.getAmount(), updatePayment::setUpdate);
                UpdateIfNotNull.updateDouble(paymentDto::setDepositBalance, paymentDto.getDepositBalance() + createPaymentDetail.getAmount(), updatePayment::setUpdate);
                UpdateIfNotNull.updateBoolean(paymentDto::setHasDetailTypeDeposit, true, paymentDto.isHasDetailTypeDeposit(), updatePayment::setUpdate);
                if (paymentDto.getNotApplied() == null) {
                    paymentDto.setNotApplied(0.0);
                }
                UpdateIfNotNull.updateDouble(paymentDto::setNotApplied, paymentDto.getNotApplied() - createPaymentDetail.getAmount(), updatePayment::setUpdate);
                //Los Deposit deben de ser restados del Payment Balance, pero si sobre un Detalle de tipo Deposit se realiza Apply Deposit, ese valor hay que devolverselo al Payment Balance.
                UpdateIfNotNull.updateDouble(paymentDto::setPaymentBalance, paymentDto.getPaymentBalance() - createPaymentDetail.getAmount(), updatePayment::setUpdate);
                newPaymentDetailDto.setAmount(createPaymentDetail.getAmount() * -1);
                newPaymentDetailDto.setApplyDepositValue(createPaymentDetail.getAmount());
            }

            if(!otherDeductionAndApplyPayment){
                paymentDetailsToCreate.add(newPaymentDetailDto);
            }

            if (updatePayment.getUpdate() > 0) {
                if (paymentDto.getPaymentBalance() == 0 && paymentDto.getDepositBalance() == 0) {
                    paymentDto.setPaymentStatus(paymentStatusApplied);
                }
            }

            if (createPaymentDetail.getApplyPayment() && paymentTransactionType.getCash()){
                //Apply Payment
                this.applyPaymentDetail(createPaymentDetail.getBooking(),
                        newPaymentDetailDto,
                        createPaymentDetail.getTransactionDate(),
                        paymentDto,
                        createPaymentDetail.getEmployee(),
                        paymentStatusApplied,
                        paymentStatusHistories);
            }

            if (createPaymentDetail.getApplyPayment() && !paymentTransactionType.getCash() && !paymentTransactionType.getDeposit()){
                //TODO: Apply Other Deductions
            }

            this.addPaymentToList(paymentDto, paymentsToCreate);
        }

        this.createPaymentsDetails(paymentDetailsToCreate);
        this.updatePayments(paymentsToCreate);
        this.createPaymentStatusHistory(paymentStatusHistories);
        this.replicateBookingToKafka(paymentDetailsToCreate);
    }

    private ManagePaymentStatusDto getPaymentStatusApplied(){
        return paymentStatusService.findByApplied();
    }

    @Override
    public void applyPaymentDetail(ManageBookingDto booking,
                                   PaymentDetailDto paymentDetail,
                                   OffsetDateTime transactionDate,
                                   PaymentDto payment,
                                   ManageEmployeeDto employee,
                                   ManagePaymentStatusDto paymentStatus,
                                   List<PaymentStatusHistoryDto> paymentStatusHistoryList){

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(booking, "id", "Booking ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(paymentDetail, "id", "Payment Detail ID cannot be null."));

        booking.setAmountBalance(booking.getAmountBalance() - paymentDetail.getAmount());
        paymentDetail.setManageBooking(booking);
        paymentDetail.setApplyPayment(true);
        paymentDetail.setAppliedAt(OffsetDateTime.now(ZoneId.of("UTC")));
        paymentDetail.setEffectiveDate(transactionDate);

        if(payment.getNotApplied() == 0 && payment.getDepositBalance() == 0 && !booking.getInvoice().getInvoiceType().equals(EInvoiceType.CREDIT)){
            payment.setPaymentStatus(paymentStatus);
            PaymentStatusHistoryDto paymentStatusHistory = createPaymentAttachmentStatusHistory(employee, payment);
            paymentStatusHistoryList.add(paymentStatusHistory);
        }

        payment.setApplyPayment(true);
    }

    private PaymentStatusHistoryDto createPaymentAttachmentStatusHistory(ManageEmployeeDto employee, PaymentDto payment){
        PaymentStatusHistoryDto attachmentStatusHistoryDto = new PaymentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("Update Payment.");
        attachmentStatusHistoryDto.setEmployee(employee);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(payment.getPaymentStatus().getCode() + "-" + payment.getPaymentStatus().getName());

        return attachmentStatusHistoryDto;
    }

    private void createPaymentsDetails(List<PaymentDetailDto> paymentDetailsToCreate){
        if(Objects.isNull(paymentDetailsToCreate) || paymentDetailsToCreate.isEmpty() ){
            Logger.getLogger(PaymentDetailHelperServiceImpl.class.getName()).log(Level.SEVERE, null, "The payment detail list is null or empty");
        }

        paymentDetailService.bulk(paymentDetailsToCreate);
    }

    private void updatePayments(List<PaymentDto> paymentList){
        if(Objects.isNull(paymentList) || paymentList.isEmpty()){
            Logger.getLogger(PaymentDetailHelperServiceImpl.class.getName()).log(Level.SEVERE, null, "The payment list is null or empty");
        }

        paymentService.createBulk(paymentList);
    }

    private void createPaymentStatusHistory(List<PaymentStatusHistoryDto> paymentStatusHistoryList){
        if(Objects.isNull(paymentStatusHistoryList) || paymentStatusHistoryList.isEmpty()){
            Logger.getLogger(PaymentDetailHelperServiceImpl.class.getName()).log(Level.SEVERE, null, "The payment status history list is null or empty");
        }

        paymentStatusHistoryService.createAll(paymentStatusHistoryList);
    }

    private void replicateBookingToKafka(List<PaymentDetailDto> details){
        details.forEach(paymentDetail -> {
            try {
                PaymentDto payment = paymentDetail.getPayment();
                ManageBookingDto booking = paymentDetail.getManageBooking();

                ReplicatePaymentKafka paymentKafka = new ReplicatePaymentKafka(
                        payment.getId(),
                        payment.getPaymentId(),
                        new ReplicatePaymentDetailsKafka(paymentDetail.getId(), paymentDetail.getPaymentDetailId()
                        ));
                if (booking.getInvoice().getInvoiceType().equals(EInvoiceType.CREDIT) || booking.getInvoice().getInvoiceType().equals(EInvoiceType.OLD_CREDIT)) {
                    this.producerUpdateBookingService.update(new UpdateBookingBalanceKafka(booking.getId(), paymentDetail.getAmount(), paymentKafka, false));
                } else {
                    this.producerUpdateBookingService.update(new UpdateBookingBalanceKafka(booking.getId(), paymentDetail.getAmount(), paymentKafka, false));
                }
            } catch (Exception e) {
                Logger.getLogger(PaymentDetailHelperServiceImpl.class.getName()).log(Level.SEVERE, null, "Error at sending UpdateBookingBalanceKafka to kafka");
            }
        });
    }

    public void addPaymentToList(PaymentDto payment, List<PaymentDto> paymentList) {
        boolean exists = paymentList.stream().anyMatch(item -> item.getId().equals(payment.getId()));
        if(!exists){
            paymentList.add(payment);
        }
    }
}
