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
import com.kynsoft.finamer.payment.infrastructure.services.http.InvoiceHttpUUIDService;
import com.kynsoft.finamer.payment.infrastructure.services.http.helper.InvoiceImportAutomaticeHelperServiceImpl;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateListBookingService;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApplyPaymentService {

    private final IManageInvoiceService manageInvoiceService;
    private final IPaymentService paymentService;
    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentStatusHistoryService paymentStatusHistoryService;
    private final IPaymentCloseOperationService paymentCloseOperationService;
    private final IManageBookingService manageBookingService;
    private final IManagePaymentStatusService statusService;
    private final IManageEmployeeService manageEmployeeService;

    @Getter
    private List<PaymentDetailDto> createPaymentDetails;

    @Getter
    private List<ManageBookingDto> bookingList;

    public ApplyPaymentService(IPaymentService paymentService,
                               IManageInvoiceService manageInvoiceService,
                               IPaymentDetailService paymentDetailService,
                               IManagePaymentTransactionTypeService paymentTransactionTypeService,
                               IPaymentStatusHistoryService paymentStatusHistoryService,
                               IPaymentCloseOperationService paymentCloseOperationService,
                               IManageBookingService manageBookingService,
                               IManagePaymentStatusService statusService,
                               IManageEmployeeService manageEmployeeService){
        this.paymentService = paymentService;
        this.manageInvoiceService = manageInvoiceService;
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentStatusHistoryService = paymentStatusHistoryService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.manageBookingService = manageBookingService;
        this.statusService = statusService;
        this.manageEmployeeService = manageEmployeeService;
    }
    @Transactional
    public PaymentDto apply(UUID paymentId,
                            boolean shouldApplyPaymentBalance,
                            boolean shouldApplyDeposit,
                            List<UUID> invoices,
                            List<UUID> deposits,
                            UUID employeeId){
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(paymentId, "id", "Payment ID cannot be null."));

        PaymentDto payment = this.getPayment(paymentId);
        ManageEmployeeDto employeeDto = this.getEmployee(employeeId);//TODO Optimizar esta consulta de employee con Criteria API
        List<ManageInvoiceDto> sortedInvoices = createInvoiceQueue(invoices);//TODO Optimizar esta consulta en PaymentDetail
        List<PaymentDetailDto> depositPaymentDetails = this.createPaymentDetailsTypeDepositQueue(deposits);
        Set<PaymentDetailDto> updatedDepositPaymentDetails = new HashSet<>();

        OffsetDateTime transactionDate = this.getTransactionDate(payment.getHotel().getId());
        ManagePaymentTransactionTypeDto paymentInvoiceTransactionType = this.getCashTransactionType(); //PAGO
        ManagePaymentTransactionTypeDto applyDepositTransactionType = this.getApplyPaymentTransactionType();//AANT
        ManagePaymentStatusDto appliedPaymentStatus = this.getAppliedPaymentStatus();

        this.createPaymentDetails = new ArrayList<>();
        this.bookingList = new ArrayList<>();

        List<PaymentStatusHistoryDto> paymentStatusHistoryList = new ArrayList<>();


        for (ManageInvoiceDto manageInvoiceDto : sortedInvoices) {
            this.sortBookingsInInvoice(manageInvoiceDto);
            for (ManageBookingDto bookingDto : manageInvoiceDto.getBookings()) {
                boolean isUpdatedBooking = false;
                if (payment.getNotApplied() > 0 && payment.getPaymentBalance() > 0 && shouldApplyPaymentBalance && bookingDto.getAmountBalance() > 0) {
                    double amountToApply = Math.min(payment.getNotApplied(), bookingDto.getAmountBalance());

                    PaymentDetailDto paymentDetail = this.createPaymentDetail(amountToApply,
                            paymentInvoiceTransactionType,
                            payment,
                            null,
                            transactionDate,
                            employeeDto,
                            appliedPaymentStatus,
                            paymentStatusHistoryList);
                    this.createPaymentDetails.add(paymentDetail);
                    this.applyPayment(payment, paymentDetail, bookingDto, transactionDate);
                    isUpdatedBooking = true;
                }

                if ((payment.getNotApplied() == 0 && payment.getPaymentBalance() == 0 && shouldApplyDeposit && bookingDto.getAmountBalance() > 0 && payment.getDepositBalance() > 0)
                        || (shouldApplyDeposit && !shouldApplyPaymentBalance && bookingDto.getAmountBalance() > 0 && payment.getDepositBalance() > 0)) {
                    if (depositPaymentDetails.isEmpty()) {
                        break;
                    }

                    Iterator<PaymentDetailDto> depositPaymentDetailsIterator = depositPaymentDetails.iterator();
                    while (depositPaymentDetailsIterator.hasNext()){
                        PaymentDetailDto depositPaymentDetail = depositPaymentDetailsIterator.next();
                        double amountToApply = Math.min(depositPaymentDetail.getApplyDepositValue(), bookingDto.getAmountBalance());//TODO Validar ambos casos, cuando el valor del deposito sea mayor que el valor del booking y cuando el valor del booking sea mayor que el deposito

                        PaymentDetailDto applyDepositPaymentDetail = this.createPaymentDetail(amountToApply,
                                applyDepositTransactionType,
                                payment,
                                depositPaymentDetail,
                                transactionDate,
                                employeeDto,
                                appliedPaymentStatus,
                                paymentStatusHistoryList);
                        this.createPaymentDetails.add(applyDepositPaymentDetail);

                        this.applyPayment(payment,
                                applyDepositPaymentDetail,
                                bookingDto,
                                transactionDate);

                        isUpdatedBooking = true;
                        updatedDepositPaymentDetails.add(depositPaymentDetail);
                        //Cuando se usa todo el deposit value del deposito lo quito de la lista de depositos
                        if(depositPaymentDetail.getApplyDepositValue() == 0){
                            depositPaymentDetailsIterator.remove();
                        }

                        if (bookingDto.getAmountBalance() == 0 || payment.getDepositBalance() == 0) {
                            break;
                        }
                    }
                }

                if(isUpdatedBooking){
                    this.bookingList.add(bookingDto);
                }

                if (payment.getPaymentBalance() == 0 && payment.getDepositBalance() == 0) {
                    break;
                }
            }
            if (payment.getPaymentBalance() == 0 && payment.getDepositBalance() == 0) {
                break;
            }
        }

        this.saveChanges(payment,
                this.createPaymentDetails,
                updatedDepositPaymentDetails,
                paymentStatusHistoryList,
                this.bookingList);

        this.paymentCloseOperationService.clearCache();

        return payment;
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
                                                   PaymentDetailDto depositPaymentDetail,
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
                depositPaymentDetail
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

    private void saveChanges(PaymentDto payment,
                             List<PaymentDetailDto> createPaymentDetails,
                             Set<PaymentDetailDto> updatedDepositPaymentDetails,
                             List<PaymentStatusHistoryDto> paymentStatusHistoryList,
                             List<ManageBookingDto> bookingList){
        //Se crean los Payment Details que genera el flujo de aplicacion de pago.
        this.paymentDetailService.createAll(createPaymentDetails);
        if(!updatedDepositPaymentDetails.isEmpty()){
            List<PaymentDetailDto> updatedDepositPaymentDetailList = new ArrayList<>(updatedDepositPaymentDetails);
            this.paymentDetailService.createAll(updatedDepositPaymentDetailList);
        }

        //Se actualizan los booking balance.
        this.manageBookingService.updateAllBooking(bookingList);

        this.paymentStatusHistoryService.createAll(paymentStatusHistoryList);

        this.paymentService.update(payment);
    }
}
