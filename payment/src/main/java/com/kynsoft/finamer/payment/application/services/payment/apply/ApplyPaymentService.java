package com.kynsoft.finamer.payment.application.services.payment.apply;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.payment.domain.core.applyPayment.ProcessApplyPaymentDetail;
import com.kynsoft.finamer.payment.domain.core.paymentDetail.ProcessCreatePaymentDetail;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.identity.Booking;
import com.kynsoft.finamer.payment.infrastructure.identity.Invoice;
import com.kynsoft.finamer.payment.infrastructure.identity.PaymentDetail;
import com.kynsoft.finamer.payment.infrastructure.services.http.InvoiceHttpUUIDService;
import com.kynsoft.finamer.payment.infrastructure.services.http.helper.InvoiceImportAutomaticeHelperServiceImpl;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateListBookingService;
import jakarta.transaction.Transactional;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

public class ApplyPaymentService {

    private final IManageInvoiceService manageInvoiceService;
    private final IPaymentService paymentService;
    private final IPaymentDetailService paymentDetailService;

    private final InvoiceHttpUUIDService invoiceHttpUUIDService;
    private final InvoiceImportAutomaticeHelperServiceImpl invoiceImportAutomaticeHelperServiceImpl;

    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentStatusHistoryService paymentStatusHistoryService;

    private final IPaymentCloseOperationService paymentCloseOperationService;
    private final IManageBookingService manageBookingService;

    private final ProducerUpdateListBookingService producerUpdateListBookingService;
    private final IManagePaymentStatusService statusService;
    private final IManageEmployeeService manageEmployeeService;

    public ApplyPaymentService(IPaymentService paymentService,
                               IManageInvoiceService manageInvoiceService,
                               IPaymentDetailService paymentDetailService,
                               InvoiceHttpUUIDService invoiceHttpUUIDService,
                               InvoiceImportAutomaticeHelperServiceImpl invoiceImportAutomaticeHelperServiceImpl,
                               IManagePaymentTransactionTypeService paymentTransactionTypeService,
                               IPaymentStatusHistoryService paymentStatusHistoryService,
                               IPaymentCloseOperationService paymentCloseOperationService,
                               IManageBookingService manageBookingService,
                               IManagePaymentStatusService statusService,
                               IManageEmployeeService manageEmployeeService,
                               ProducerUpdateListBookingService producerUpdateListBookingService){
        this.invoiceHttpUUIDService = invoiceHttpUUIDService;
        this.invoiceImportAutomaticeHelperServiceImpl = invoiceImportAutomaticeHelperServiceImpl;
        this.paymentService = paymentService;
        this.manageInvoiceService = manageInvoiceService;
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentStatusHistoryService = paymentStatusHistoryService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.manageBookingService = manageBookingService;
        this.statusService = statusService;
        this.manageEmployeeService = manageEmployeeService;
        this.producerUpdateListBookingService = producerUpdateListBookingService;
    }
    @Transactional
    public PaymentDto apply(UUID paymentId,
                            boolean shouldApplyDeposit,
                            boolean shouldApplyPaymentBalance,
                            List<UUID> invoices,
                            List<UUID> deposits,
                            UUID employeeId){
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(paymentId, "id", "Payment ID cannot be null."));

        PaymentDto payment = this.getPayment(paymentId);
        ManageEmployeeDto employeeDto = this.getEmployee(employeeId);
        List<ManageInvoiceDto> sortedInvoices = createInvoiceQueue(invoices);
        List<PaymentDetailDto> depositPaymentDetails = this.createPaymentDetailsTypeDepositQueue(deposits);

        OffsetDateTime transactionDate = this.getTransactionDate(payment.getHotel().getId());
        ManagePaymentTransactionTypeDto paymentInvoiceTransactionType = this.getCashTransactionType(); //PAGO
        ManagePaymentTransactionTypeDto applyDepositTransactionType = this.getApplyPaymentTransactionType();//AANT
        ManagePaymentStatusDto appliedPaymentStatus = this.getAppliedPaymentStatus();

        List<PaymentDetailDto> createPaymentDetails = new ArrayList<>();
        List<PaymentStatusHistoryDto> paymentStatusHistoryList = new ArrayList<>();

        double paymentBalance = payment.getPaymentBalance();
        double notApplied = payment.getNotApplied();
        double depositBalance = payment.getDepositBalance();
        boolean applyPaymentBalance = payment.getPaymentBalance() != 0;

        for (ManageInvoiceDto manageInvoiceDto : sortedInvoices) {
            this.sortBookingsInInvoice(manageInvoiceDto);
            for (ManageBookingDto bookingDto : manageInvoiceDto.getBookings()) {
                //TODO: almaceno el valor de Balance del Booking porque puede que no llegue a cero cuando el Payment Balance si lo haga. Y todavia
                // tenga valor el notApplied
                double amountBalance = bookingDto.getAmountBalance();

                if (notApplied > 0 && paymentBalance > 0 && shouldApplyPaymentBalance && amountBalance > 0) {
                    double amountToApply = Math.min(notApplied, amountBalance);

                    PaymentDetailDto paymentDetail = this.createPaymentDetail(amountToApply,
                            paymentInvoiceTransactionType,
                            payment,
                            transactionDate,
                            employeeDto,
                            appliedPaymentStatus,
                            paymentStatusHistoryList);
                    createPaymentDetails.add(paymentDetail);
                    this.applyPayment(payment, paymentDetail, bookingDto, transactionDate);

                    notApplied =  BankerRounding.round(notApplied - amountToApply);
                    paymentBalance = BankerRounding.round(paymentBalance - amountToApply);
                    amountBalance = BankerRounding.round(amountBalance - amountToApply);

                }

                if ((notApplied == 0 && paymentBalance == 0 && shouldApplyDeposit && amountBalance > 0 && depositBalance > 0)
                        || (shouldApplyDeposit && !shouldApplyPaymentBalance && amountBalance > 0 && depositBalance > 0)) {
                    //TODO: este aplica para cuando se quiere aplicar solo a los deposit
                    if (deposits != null && !deposits.isEmpty()) {
                        List<PaymentDetailDto> availableDepositPaymentDetails = depositPaymentDetails.stream().
                                filter(detail -> detail.getApplyDepositValue() > 0)
                                .collect(Collectors.toList());

                        for (PaymentDetailDto depositPaymentDetail : availableDepositPaymentDetails) {
                            double depositAmount = depositPaymentDetail.getApplyDepositValue();


                            while (depositAmount > 0) {
                                double amountToApply = Math.min(depositAmount, amountBalance);//TODO Validar ambos casos, cuando el valor del deposito sea mayor que el valor del booking y cuando el valor del booking sea mayor que el deposito

                                PaymentDetailDto applyPaymentDetail = this.createPaymentDetail(amountToApply,
                                        applyDepositTransactionType,
                                        payment,
                                        transactionDate,
                                        employeeDto,
                                        appliedPaymentStatus,
                                        paymentStatusHistoryList);
                                createPaymentDetails.add(applyPaymentDetail);

                                this.applyPayment(payment,
                                        applyPaymentDetail,
                                        bookingDto,
                                        transactionDate);

                                depositAmount = BankerRounding.round(depositAmount - amountToApply);
                                amountBalance = BankerRounding.round(amountBalance - amountToApply);
                                depositBalance = BankerRounding.round(depositBalance - amountToApply);
                                if (amountBalance == 0 || depositBalance == 0) {
                                    break;
                                }
                            }

                            //Termino el for cuando el balance del booking llegue a 0
                            if (bookingDto.getAmountBalance() == 0) {
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

        //Se crean los Payment Details que genera el flujo de aplicacion de pago.
        this.paymentDetailService.createAll(createPaymentDetails);
        //Se actualizan los booking balance.
        this.manageBookingService.updateAll(bookingsList);

        //Se actualiza el payment
        this.paymentService.updateBalances(
                updatePayment.getPaymentBalance(),
                updatePayment.getDepositBalance(),
                updatePayment.getIdentified(),
                updatePayment.getNotIdentified(),
                updatePayment.getNotApplied(),
                updatePayment.getApplied(),
                updatePayment.isApplyPayment(),
                updatePayment.getId()
        );
        //Se actualizan los deposit
        this.paymentDetailService.createAll(detailTypeDeposits);
        this.paymentCloseOperationService.clearCache();

        this.producerUpdateListBookingService.update(kafkaList);
    }

    private PaymentDto getPayment(UUID paymentId){
        return this.paymentService.findByIdCustom(paymentId);
    }

    private ManageEmployeeDto getEmployee(UUID employeeId){
        return this.manageEmployeeService.findById(employeeId);
    }

    private List<ManageInvoiceDto> createInvoiceQueue(List<UUID> invoiceIds) {
        return this.manageInvoiceService.findSortedInvoicesByIdIn(invoiceIds);
    }

    private List<PaymentDetailDto> createPaymentDetailsTypeDepositQueue(List<UUID> deposits) {
        try {
            List<PaymentDetailDto> queue = this.paymentDetailService.findByIdIn(deposits);
            queue.sort(Comparator.comparingDouble(PaymentDetailDto::getApplyDepositValue));
            return queue;
        } catch (Exception e) {
            return List.of();
        }
    }

    private Map<UUID, PaymentCloseOperationDto> getPaymentCloseOperationMap(List<ManageInvoiceDto> invoices){
        List<UUID> hotelIds = invoices.stream().map(invoice -> invoice.getHotel().getId()).collect(Collectors.toList());
        return this.paymentCloseOperationService.findByHotelId(hotelIds).stream()
                .collect(Collectors.toMap(paymentCloseOperationDto -> paymentCloseOperationDto.getHotel().getId(),
                        paymentCloseOperationDto -> paymentCloseOperationDto));
    }

    private ManagePaymentTransactionTypeDto getCashTransactionType(){
        return this.paymentTransactionTypeService.findByPaymentInvoiceEntityGraph().toAggregate();
    }

    private ManagePaymentTransactionTypeDto getApplyPaymentTransactionType(){
        return this.paymentTransactionTypeService.findByApplyDepositEntityGraph().toAggregate();
    }

    private ManagePaymentStatusDto getAppliedPaymentStatus(){
        return this.statusService.findPaymentStatusByApplied().toAggregate();
    }

    private OffsetDateTime getTransactionDate(UUID hotelId) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelId(hotelId);
        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }



    private List<Booking> getSortedBookings(Invoice manageInvoiceDto) {
        List<Booking> bookingDtoList = new ArrayList<>();
        if (manageInvoiceDto.getBookings() != null && !manageInvoiceDto.getBookings().isEmpty()) {
            bookingDtoList.addAll(manageInvoiceDto.getBookings());
            bookingDtoList.sort(Comparator.comparingDouble(Booking::getAmountBalance));
        }
        return bookingDtoList;
    }

    private void sortBookingsInInvoice(ManageInvoiceDto invoices){
        invoices.getBookings().sort(Comparator.comparingDouble(ManageBookingDto::getAmountBalance));
    }

    private PaymentDetailDto createPaymentDetail(double invoiceAmount,
                                                   ManagePaymentTransactionTypeDto transactionTypeDto,
                                                   PaymentDto updatePayment,
                                                   OffsetDateTime transactionDate,
                                                   ManageEmployeeDto employeeDto,
                                                   ManagePaymentStatusDto appliedPaymentStatus,
                                                   List<PaymentStatusHistoryDto> paymentStatusHistoryList) {
        ProcessCreatePaymentDetail processCreatePaymentDetail = new ProcessCreatePaymentDetail(updatePayment,
                invoiceAmount,
                transactionDate,
                employeeDto,
                "",
                transactionTypeDto,
                appliedPaymentStatus,
                null
        );
        processCreatePaymentDetail.process();
        PaymentDetailDto paymentDetailDto = processCreatePaymentDetail.getDetail();
        if(processCreatePaymentDetail.isPaymentApplied()){
            PaymentStatusHistoryDto paymentStatusHistoryDto = processCreatePaymentDetail.getPaymentStatusHistory();
            paymentStatusHistoryList.add(paymentStatusHistoryDto);
        }

        return paymentDetailDto;
    }

    public void applyPayment(PaymentDto payment,
                             PaymentDetailDto paymentDetailDto,
                             ManageBookingDto bookingDto,
                             OffsetDateTime transactionDate) {

        ProcessApplyPaymentDetail processApplyPaymentDetail = new ProcessApplyPaymentDetail(payment,
                paymentDetailDto,
                bookingDto,
                transactionDate,
                paymentDetailDto.getAmount());
        processApplyPaymentDetail.process();
    }
}
