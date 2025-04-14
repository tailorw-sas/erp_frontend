package com.kynsoft.finamer.payment.infrastructure.services.helpers;

import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.application.excel.validator.IImportControl;
import com.kynsof.share.core.domain.exception.ExcelException;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailRequest;
import com.kynsoft.finamer.payment.domain.core.applyPayment.ApplyOtherDeduction;
import com.kynsoft.finamer.payment.domain.core.deposit.ApplyDeposit;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.ImportControl;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentDetailRowError;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.excel.PaymentCacheFactory;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.detail.PaymentDetailValidatorFactory;
import com.kynsoft.finamer.payment.infrastructure.identity.Booking;
import com.kynsoft.finamer.payment.infrastructure.identity.ManagePaymentTransactionType;
import com.kynsoft.finamer.payment.infrastructure.identity.PaymentDetail;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportDetailErrorRepository;

import com.kynsoft.finamer.payment.domain.core.deposit.Deposit;
import com.kynsoft.finamer.payment.domain.core.applyPayment.ApplyPayment;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateBookingService;
import io.jsonwebtoken.lang.Assert;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class PaymentImportDetailHelperServiceImpl extends AbstractPaymentImportHelperService {

    private final PaymentImportCacheRepository paymentImportCacheRepository;
    private final PaymentDetailValidatorFactory paymentDetailValidatorFactory;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final IManagePaymentTransactionTypeService transactionTypeService;
    private final IPaymentService paymentService;
    private final IPaymentDetailService paymentDetailService;
    private final PaymentImportDetailErrorRepository detailErrorRepository;
    private final IManageBookingService bookingService;
    private final IPaymentCloseOperationService paymentCloseOperationService;
    private final IManageEmployeeService employeeService;
    private final IManagePaymentStatusService paymentStatusService;
    private final IPaymentStatusHistoryService paymentStatusHistoryService;
    private final ProducerUpdateBookingService producerUpdateBookingService;
    private final IManageInvoiceService invoiceService;

    private static final Logger logger = LoggerFactory.getLogger(PaymentImportDetailHelperServiceImpl.class);


    private boolean stopProcess;

    public PaymentImportDetailHelperServiceImpl(PaymentImportCacheRepository paymentImportCacheRepository,
                                                PaymentDetailValidatorFactory paymentDetailValidatorFactory,
                                                ApplicationEventPublisher applicationEventPublisher,
                                                IManagePaymentTransactionTypeService transactionTypeService,
                                                IPaymentService paymentService, IPaymentDetailService paymentDetailService,
                                                PaymentImportDetailErrorRepository detailErrorRepository,
                                                IManageBookingService bookingService,
                                                IPaymentCloseOperationService paymentCloseOperationService,
                                                IManageEmployeeService employeeService,
                                                IManagePaymentStatusService paymentStatusService,
                                                IPaymentStatusHistoryService paymentStatusHistoryService,
                                                ProducerUpdateBookingService producerUpdateBookingService,
                                                IManageInvoiceService invoiceService) {
        this.paymentImportCacheRepository = paymentImportCacheRepository;
        this.paymentDetailValidatorFactory = paymentDetailValidatorFactory;
        this.applicationEventPublisher = applicationEventPublisher;
        this.transactionTypeService = transactionTypeService;
        this.paymentService = paymentService;
        this.paymentDetailService = paymentDetailService;
        this.detailErrorRepository = detailErrorRepository;
        this.bookingService = bookingService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.employeeService = employeeService;
        this.paymentStatusService = paymentStatusService;
        this.paymentStatusHistoryService = paymentStatusHistoryService;
        this.producerUpdateBookingService = producerUpdateBookingService;
        this.invoiceService = invoiceService;
    }

    @Override
    public void readExcel(ReaderConfiguration readerConfiguration, Object rawRequest) {
        printLog("Start readExcel process");
        this.totalProcessRow = 0;
        PaymentImportDetailRequest request = (PaymentImportDetailRequest) rawRequest;
        ManageEmployeeDto employee = this.getEmployee(UUID.fromString(request.getEmployeeId()));
        ExcelBeanReader<PaymentDetailRow> excelBeanReader = new ExcelBeanReader<>(readerConfiguration, PaymentDetailRow.class);
        ExcelBean<PaymentDetailRow> excelBean = new ExcelBean<>(excelBeanReader);
        List<PaymentDetailRow> excelRows = new ArrayList<>();

        printLog("Antes de obtener cache");
        Cache cache = this.createCache(excelBean, excelRows, request, employee);
        printLog("Despues de obtener cache");

        paymentDetailValidatorFactory.createValidators(cache);
        boolean validationResult = paymentDetailValidatorFactory.validate(excelRows);
        if(validationResult){
            excelRows.forEach(row -> {
                cachingPaymentImport(row);
                this.totalProcessRow++;
            });
        }/*else{
            importControl.setShouldStopProcess(true);
        }*/

        printLog("End readExcel process");
        /*if(importControl.getShouldStopProcess()){
            throw new ExcelException("Excel has errors");
        }*/
    }

    private void clearCache() {
        this.transactionTypeService.clearCache();
    }

    @Override
    public void cachingPaymentImport(Row paymentRow) {
        PaymentImportCache paymentImportCache = PaymentCacheFactory.getPaymentImportCache(paymentRow);
        paymentImportCacheRepository.save(paymentImportCache);
    }

    @Override
    public void clearPaymentImportCache(String importProcessId) {
        Pageable pageable = PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, "id"));
        Page<PaymentImportCache> cacheList;
        do {
            cacheList = paymentImportCacheRepository.findAllByImportProcessId(importProcessId, pageable);
            paymentImportCacheRepository.deleteAll(cacheList.getContent());
            pageable = pageable.next();
        } while (cacheList.hasNext());
    }

    @Override
    public void readPaymentCacheAndSave(Object rawRequest) {
        PaymentImportDetailRequest request = (PaymentImportDetailRequest) rawRequest;
        printLog("Start readPaymentCacheAndSave process");
        ManageEmployeeDto employee = this.getEmployee(UUID.fromString(request.getEmployeeId()));
        List<PaymentImportCache> paymentCacheList = this.getPaymentImportCachedList(request.getImportProcessId());
        if(paymentCacheList.isEmpty()){
            return;
        }

        Cache cache = this.createCache(paymentCacheList, employee);
        //List<CreatePaymentDetail> createPaymentDetailList = new ArrayList<>();//TODO Validar una lista segura para implementar en hilos
        List<PaymentDto> paymentsToUpdateList = new ArrayList<>();
        List<PaymentDetailDto> paymentDetailsToCreate = new ArrayList<>();
        List<PaymentDetailDto> paymentDetailsAntiToUpdate = new ArrayList<>();
        List<ManageBookingDto> bookingsToUpdate = new ArrayList<>();
        ManagePaymentTransactionTypeDto depositPaymentTransactionType = cache.getDepositTransactionType();
        List<PaymentStatusHistoryDto> paymentStatusHistories = new ArrayList<>();
        ManagePaymentStatusDto paymentStatusApplied = getPaymentStatusApplied();

        for(PaymentImportCache paymentImportCache : paymentCacheList ){
            ManagePaymentTransactionTypeDto managePaymentTransactionTypeDto = cache.getManageTransactionTypeByCode(paymentImportCache.getTransactionId());
            Assert.notNull(managePaymentTransactionTypeDto, "Transaction type is null");

            PaymentDto paymentDto = cache.getPaymentByPaymentId(Long.parseLong(paymentImportCache.getPaymentId()));
            if(Objects.isNull(paymentDto)){
                break;
            }
            addPaymentToList(paymentDto, paymentsToUpdateList);
            OffsetDateTime transactionDate = cache.getTransactionDateByHotelId(paymentDto.getHotel().getId());

            ManageBookingDto bookingDto = getBookingFromCache(paymentImportCache.getBookId(), cache);
            if ( Objects.nonNull(bookingDto) && bookingDto.getAmountBalance() == 0) {
                PaymentDetailDto paymentDetailTypeDeposit = sendDeposit(paymentImportCache,
                                                                        paymentDto,
                                                                        Double.parseDouble(paymentImportCache.getPaymentAmount()),
                                                                        transactionDate,
                                                                        depositPaymentTransactionType);
                paymentDetailsToCreate.add(paymentDetailTypeDeposit);
            }else{
                if (Objects.nonNull(paymentImportCache.getAnti()) && !paymentImportCache.getAnti().isEmpty()) {
                    this.processAntiDetail(paymentImportCache,
                            cache,
                            paymentDto,
                            bookingDto,
                            employee,
                            managePaymentTransactionTypeDto,
                            transactionDate,
                            paymentStatusApplied,
                            paymentStatusHistories,
                            paymentDetailsToCreate,
                            paymentDetailsAntiToUpdate,
                            bookingsToUpdate);
                } else {
                    if (bookingDto == null) {
                        List<ManageBookingDto> bookings = cache.getBookingsByCoupon(paymentImportCache.getCoupon());
                        if(Objects.nonNull(bookings) && bookings.size() > 1){
                            ManagePaymentTransactionTypeDto transactionTypeDto = cache.getPaymentInvoiceTransactionType();
                            String remarks = getRemarks(paymentImportCache, transactionTypeDto) + " #payment was not applied because the coupon is duplicated.";
                            //detail sin booking, transaction type tipo cash, sin aplicar pago, tomando directo el amount que viene en el excel y con remark modificado
                            createDetailAndDeposit(paymentImportCache,
                                    null,
                                    transactionTypeDto,
                                    paymentDto,
                                    Double.parseDouble(paymentImportCache.getPaymentAmount()),
                                    remarks,
                                    employee,
                                    depositPaymentTransactionType,
                                    transactionDate,
                                    paymentStatusHistories,
                                    paymentStatusApplied,
                                    paymentDetailsToCreate);
                        } else {
                            if(Objects.isNull(bookings)){
                                PaymentDetailDto paymentDetailTypeDeposit = getDeposit(paymentImportCache,
                                        paymentDto,
                                        Double.parseDouble(paymentImportCache.getPaymentAmount()),
                                        true,
                                        "#coupon not found",
                                        transactionDate,
                                        depositPaymentTransactionType);
                                paymentDetailsToCreate.add(paymentDetailTypeDeposit);
                            }else{
                                ManageBookingDto booking = bookings.get(0);
                                if (booking.getAmountBalance() == 0) {
                                    PaymentDetailDto paymentDetailTypeDeposit = sendDeposit(paymentImportCache,
                                                                                            paymentDto,
                                                                                            Double.parseDouble(paymentImportCache.getPaymentAmount()),
                                                                                            transactionDate,
                                                                                            depositPaymentTransactionType);
                                    paymentDetailsToCreate.add(paymentDetailTypeDeposit);
                                }else{
                                    createDetailAndDeposit(paymentImportCache,
                                            booking,
                                            managePaymentTransactionTypeDto,
                                            paymentDto,
                                            Double.parseDouble(paymentImportCache.getPaymentAmount()),
                                            getRemarks(paymentImportCache, managePaymentTransactionTypeDto),
                                            employee,
                                            depositPaymentTransactionType,
                                            transactionDate,
                                            paymentStatusHistories,
                                            paymentStatusApplied,
                                            paymentDetailsToCreate);
                                    bookingsToUpdate.add(booking);
                                }
                            }
                        }
                    } else {
                        createDetailAndDeposit(paymentImportCache,
                                bookingDto,
                                managePaymentTransactionTypeDto,
                                paymentDto,
                                Double.parseDouble(paymentImportCache.getPaymentAmount()),
                                getRemarks(paymentImportCache, managePaymentTransactionTypeDto),
                                employee,
                                depositPaymentTransactionType,
                                transactionDate,
                                paymentStatusHistories,
                                paymentStatusApplied,
                                paymentDetailsToCreate);
                        bookingsToUpdate.add(bookingDto);
                    }
                }
            }
        }

        printLog("Antes de guardar en BDD");
        this.createPaymentsDetails(paymentDetailsToCreate);
        this.createPaymentsDetails(paymentDetailsAntiToUpdate);
        this.updatePayments(paymentsToUpdateList);
        this.updateBookings(bookingsToUpdate);
        this.createPaymentStatusHistory(paymentStatusHistories);
        this.replicateBookingToKafka(paymentDetailsToCreate);
        printLog("Despues de guardar en BDD");

        this.clearCache();
        printLog("End readPaymentCacheAndSave process");
    }

    private void processAntiDetail(PaymentImportCache paymentImportCache,
                                   Cache cache,
                                   PaymentDto paymentDto,
                                   ManageBookingDto bookingDto,
                                   ManageEmployeeDto employee,
                                   ManagePaymentTransactionTypeDto managePaymentTransactionTypeDto,
                                   OffsetDateTime transactionDate,
                                   ManagePaymentStatusDto paymentStatusApplied,
                                   List<PaymentStatusHistoryDto> paymentStatusHistories,
                                   List<PaymentDetailDto> paymentDetailsToCreate,
                                   List<PaymentDetailDto> paymentDetailsAntiToUpdate,
                                   List<ManageBookingDto> bookingsToUpdate){
        PaymentDetailDto paymentDetailDto = cache.getPaymentDetailByPaymentId(paymentDto.getId(), Long.parseLong(paymentImportCache.getAnti()));
        if(Objects.isNull(bookingDto)){
            List<ManageBookingDto> bookingList = cache.getBookingsByCoupon(paymentImportCache.getCoupon());
            if(Objects.nonNull(bookingList) && bookingList.size() == 1 && Objects.nonNull(paymentDetailDto)){
                ManageBookingDto booking = bookingList.get(0);
                this.sendToCreateApplyDeposit(paymentDetailDto,
                        Double.parseDouble(paymentImportCache.getPaymentAmount()),
                        employee,
                        managePaymentTransactionTypeDto,
                        getRemarks(paymentImportCache, managePaymentTransactionTypeDto),
                        booking,
                        paymentDto,
                        transactionDate,
                        paymentStatusApplied,
                        paymentStatusHistories,
                        paymentDetailsToCreate,
                        paymentDetailsAntiToUpdate
                );
                bookingsToUpdate.add(booking);
            }else{
                //Cuando esta duplicado, solo crea el detalle AANT sin aplicar a la factura
                this.sendToCreateApplyDeposit(paymentDetailDto,
                        Double.parseDouble(paymentImportCache.getPaymentAmount()),
                        employee,
                        managePaymentTransactionTypeDto,
                        " #payment was not applied because the coupon is duplicated.",
                        null,
                        paymentDto,
                        transactionDate,
                        paymentStatusApplied,
                        paymentStatusHistories,
                        paymentDetailsToCreate,
                        paymentDetailsAntiToUpdate
                );
            }
        }else{
            if(Objects.nonNull(paymentDetailDto)){
                this.sendToCreateApplyDeposit(paymentDetailDto,
                        Double.parseDouble(paymentImportCache.getPaymentAmount()),
                        employee,
                        managePaymentTransactionTypeDto,
                        getRemarks(paymentImportCache, managePaymentTransactionTypeDto),
                        bookingDto,
                        paymentDto,
                        transactionDate,
                        paymentStatusApplied,
                        paymentStatusHistories,
                        paymentDetailsToCreate,
                        paymentDetailsAntiToUpdate
                );
                bookingsToUpdate.add(bookingDto);
            }
        }
    }

    private ManageBookingDto getBookingFromCache(String bookingId, Cache cache){
        if(Objects.isNull(bookingId)){
            return null;
        }

        return cache.getBooking(Long.parseLong(bookingId));
    }

    private PaymentDetailDto sendDeposit(PaymentImportCache paymentImportCache, PaymentDto paymentDto, Double amount, OffsetDateTime transactionDate, ManagePaymentTransactionTypeDto depositPaymentTransactionType){
        double restAmount = BankerRounding.round(paymentDto.getPaymentAmount() - amount);
        if (restAmount > 0) {
            return getDeposit(paymentImportCache, paymentDto, Double.parseDouble(paymentImportCache.getPaymentAmount()), false, "", transactionDate, depositPaymentTransactionType);
        }
        return null;
    }

    private void createDetailAndDeposit(PaymentImportCache paymentImportCache,
                                        ManageBookingDto bookingDto,
                                        ManagePaymentTransactionTypeDto managePaymentTransactionTypeDto,
                                        PaymentDto paymentDto,
                                        double amount,
                                        String remarks,
                                        ManageEmployeeDto employee,
                                        ManagePaymentTransactionTypeDto depositPaymentTransactionType,
                                        OffsetDateTime transactionDate,
                                        List<PaymentStatusHistoryDto> paymentStatusHistories,
                                        ManagePaymentStatusDto paymentStatusDto,
                                        List<PaymentDetailDto> createDetailPaymentList) {
        //cash
        if (bookingDto != null) {
            amount = Math.min(bookingDto.getAmountBalance(), Double.parseDouble(paymentImportCache.getPaymentAmount()));
        }

        if (managePaymentTransactionTypeDto.getCash() || managePaymentTransactionTypeDto.getPaymentInvoice()) {
            //Crear el detalle y aplicar
            this.createPaymentDetailAndApply(
                    paymentDto,
                    amount,
                    employee,
                    managePaymentTransactionTypeDto,
                    remarks,
                    bookingDto,
                    transactionDate,
                    paymentStatusDto,
                    paymentStatusHistories,
                    createDetailPaymentList
                    );

            //Crear el deposit con el valor sobrante no cubierto por el booking (restante del importPaymentCache)
            double restAmount = BankerRounding.round(Double.parseDouble(paymentImportCache.getPaymentAmount()) - amount);
            if (restAmount > 0) {
                PaymentDetailDto paymentDetailTypeDeposit = getDeposit(paymentImportCache, paymentDto, restAmount, false, "", transactionDate, depositPaymentTransactionType);
                createDetailPaymentList.add(paymentDetailTypeDeposit);
            }
        } else {
            //Other deductions
            this.createPaymentDetailAndApply(paymentDto,
                    Double.parseDouble(paymentImportCache.getPaymentAmount()),
                    employee,
                    managePaymentTransactionTypeDto,
                    remarks,
                    bookingDto,
                    transactionDate,
                    paymentStatusDto,
                    paymentStatusHistories,
                    createDetailPaymentList);
        }
    }

    private PaymentDetailDto getDeposit(PaymentImportCache paymentImportCache,
                                        PaymentDto paymentDto,
                                        double restAmount,
                                        boolean byCoupon,
                                        String remarks,
                                        OffsetDateTime transactionDate,
                                        ManagePaymentTransactionTypeDto paymentTransactionType) {
        String invoiceNo = validateStringRemark(paymentImportCache.getInvoiceNo());
        String firstName = validateStringRemark(paymentImportCache.getFirstName());
        String lastName = validateStringRemark(paymentImportCache.getLastName());
        String bookingNo = validateStringRemark(paymentImportCache.getBookingNo());

        if (!byCoupon) {
            remarks = ("S/P " + invoiceNo + " " + firstName + " " + lastName + " " + bookingNo);
        }

        Deposit deposit = new Deposit(
                paymentDto,
                restAmount,
                remarks,
                paymentTransactionType,
                transactionDate
        );
        deposit.create();
        return deposit.getPaymentDetail();
    }

    private String getRemarks(PaymentImportCache paymentImportCache, ManagePaymentTransactionTypeDto transactionTypeDto) {
        if (Objects.isNull(paymentImportCache.getRemarks()) || paymentImportCache.getRemarks().isEmpty()) {
            return transactionTypeDto.getDefaultRemark();
        }
        return paymentImportCache.getRemarks();
    }

    private String validateStringRemark(String value){
        return (value != null && !"null".equals(value)) ? value : "";
    }

    @Override
    public PaginatedResponse getPaymentImportErrors(String importProcessId, Pageable pageable) {
        Page<PaymentDetailRowError> page = detailErrorRepository.findAllByImportProcessId(importProcessId, pageable);
        return new PaginatedResponse(
                page.getContent().stream().sorted(Comparator.comparingInt(PaymentDetailRowError::getRowNumber)).collect(Collectors.toList()),
                page.getTotalPages(),
                page.getNumberOfElements(),
                page.getTotalElements(),
                page.getSize(),
                page.getNumber()
        );
    }

    private void createPaymentDetailAndApply(PaymentDto payment,
                                             double amount,
                                             ManageEmployeeDto employee,
                                             ManagePaymentTransactionTypeDto transactionType,
                                             String remarks,
                                             ManageBookingDto booking,
                                             OffsetDateTime transactionDate,
                                             ManagePaymentStatusDto paymentStatusApplied,
                                             List<PaymentStatusHistoryDto> paymentStatusHistories,
                                             List<PaymentDetailDto> createDetailPaymentList) {
        PaymentDetailDto newPaymentDetailDto = new PaymentDetailDto(
                UUID.randomUUID(),
                Status.ACTIVE,
                payment,
                transactionType,
                amount,
                remarks,
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
                false
        );

        //Other Deductions
        boolean otherDeductionAndApplyPayment = !transactionType.getCash() && !transactionType.getDeposit();

        if (!otherDeductionAndApplyPayment){
            //Apply Payment
            ApplyPayment applyPayment = new ApplyPayment(payment,
                    newPaymentDetailDto,
                    booking,
                    transactionDate,
                    employee,
                    paymentStatusApplied,
                    amount);
            applyPayment.applyPayment();
            if(applyPayment.isApplied()){
                PaymentStatusHistoryDto paymentStatusHistory = applyPayment.getPaymentStatusHistory();
                paymentStatusHistories.add(paymentStatusHistory);
            }
        }else{
            //Apply Other Deductions
            ApplyOtherDeduction applyOtherDeduction = new ApplyOtherDeduction(payment,
                    newPaymentDetailDto,
                    booking,
                    transactionType,
                    transactionDate,
                    amount);
            applyOtherDeduction.applyOtherDeduction();
        }

        createDetailPaymentList.add(newPaymentDetailDto);
    }

    private void sendToCreateApplyDeposit(PaymentDetailDto paymentDetail,
                                          double amount,
                                          ManageEmployeeDto employee,
                                          ManagePaymentTransactionTypeDto transactionType,
                                          String remarks,
                                          ManageBookingDto booking,
                                          PaymentDto payment,
                                          OffsetDateTime transactionDate,
                                          ManagePaymentStatusDto paymentStatusApplied,
                                          List<PaymentStatusHistoryDto> paymentStatusHistories,
                                          List<PaymentDetailDto> createPaymentDetailList,
                                          List<PaymentDetailDto> updatePaymentDetailAntiList) {
        PaymentDetailDto newPaymentDetail = new PaymentDetailDto(
                UUID.randomUUID(),
                Status.ACTIVE,
                payment,
                transactionType,
                amount,
                remarks,
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
                false
        );

        ApplyDeposit applyDeposit = new ApplyDeposit(transactionType,
                paymentDetail,
                payment,
                newPaymentDetail,
                amount,
                paymentStatusApplied,
                booking,
                transactionDate,
                employee);
        if(applyDeposit.apply()){
            if(applyDeposit.isPaymentApplied()){
                PaymentStatusHistoryDto paymentStatusHistory = applyDeposit.getPaymentStatusHistory();
                paymentStatusHistories.add(paymentStatusHistory);
            }
            if(!updatePaymentDetailAntiList.contains(paymentDetail)){
                updatePaymentDetailAntiList.add(paymentDetail);
            }
            createPaymentDetailList.add(newPaymentDetail);
        }
    }

    public void addPaymentToList(PaymentDto payment, List<PaymentDto> paymentList) {
        boolean exists = paymentList.stream().anyMatch(item -> item.getId().equals(payment.getId()));
        if(!exists){
            paymentList.add(payment);
        }
    }

    private void createPaymentsDetails(List<PaymentDetailDto> paymentDetailsToCreate){
        if(Objects.isNull(paymentDetailsToCreate) || paymentDetailsToCreate.isEmpty() ){
            printLog("The payment detail list is null or empty");
            return;
        }

        paymentDetailService.bulk(paymentDetailsToCreate);
    }

    private void updatePayments(List<PaymentDto> paymentList){
        if(Objects.isNull(paymentList) || paymentList.isEmpty()){
            printLog("The payment list is null or empty");
            return;
        }

        paymentService.createBulk(paymentList);
    }

    private void createPaymentStatusHistory(List<PaymentStatusHistoryDto> paymentStatusHistoryList){
        if(Objects.isNull(paymentStatusHistoryList) || paymentStatusHistoryList.isEmpty()){
            printLog("The payment status history list is null or empty");
            return;
        }

        paymentStatusHistoryService.createAll(paymentStatusHistoryList);
    }

    private void updateBookings(List<ManageBookingDto> bookingList){
        if(Objects.isNull(bookingList) || bookingList.isEmpty()){
            printLog("The booking list is null or empty");
            return;
        }

        bookingService.updateAll(bookingList.stream().map(Booking::new).toList());
    }

    //TODO: Implementar en Invoicing esta replicacion para que se haga en bloque y no de uno en uno
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
                printLog("Error at sending UpdateBookingBalanceKafka to kafka");
            }
        });
    }


    private void printLog(String message){
        logger.info("{} at: {}", message, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    private ManageEmployeeDto getEmployee(UUID id){
        return employeeService.findById(id);
    }

    private List<UUID> getEmployeeHotelList(ManageEmployeeDto employee){
        if(Objects.isNull(employee)){
            return null;
        }

        return employee.getManageHotelList().stream()
                .map(ManageHotelDto::getId)
                .toList();
    }

    private List<UUID> getEmployeeAgencyList(ManageEmployeeDto employee){
        if(Objects.isNull(employee)){
            return null;
        }

        return employee.getManageAgencyList().stream()
                .map(ManageAgencyDto::getId)
                .toList();
    }

    private List<PaymentImportCache> getPaymentImportCachedList(String importProcessId){
        if(Objects.isNull(importProcessId) || importProcessId.isEmpty()){
            return Collections.emptyList();
        }

        List<PaymentImportCache> paymentCacheList = paymentImportCacheRepository.findAllByImportProcessId(importProcessId);
        if (paymentCacheList.isEmpty()) {
            return Collections.emptyList();
        }

        paymentCacheList.sort(Comparator.comparing(PaymentImportCache::getRowNumber));
        return paymentCacheList;
    }

    private Cache createCache(ExcelBean<PaymentDetailRow> excelBean, List<PaymentDetailRow> excelRows, PaymentImportDetailRequest request, ManageEmployeeDto employee){
        Set<String> transactionCodeSet = new HashSet<>();
        Set<Long> bookingsIdSet = new HashSet<>();
        Set<Long> paymentIdSet = new HashSet<>();
        Set<String> couponNumberSet = new HashSet<>();
        Set<Long> paymentDetailsAntiSet = new HashSet<>();

        List<UUID> agencys = this.getEmployeeAgencyList(employee);
        List<UUID> hotels = this.getEmployeeHotelList(employee);

        for(PaymentDetailRow excelRow : excelBean){
            excelRow.setImportProcessId(request.getImportProcessId());
            excelRow.setImportType(request.getImportPaymentType().name());
            excelRow.setAgencys(agencys);
            excelRow.setHotels(hotels);
            excelRows.add(excelRow);

            if(Objects.nonNull(excelRow.getTransactionType())){
                transactionCodeSet.add(excelRow.getTransactionType());
            }

            if(Objects.nonNull(excelRow.getBookId())){
                bookingsIdSet.add(Long.parseLong(excelRow.getBookId()));
            }

            if(Objects.nonNull(excelRow.getPaymentId())){
                paymentIdSet.add(Long.parseLong(excelRow.getPaymentId()));
            }

            if(Objects.nonNull(excelRow.getCoupon())){
                couponNumberSet.add(excelRow.getCoupon());
            }

            if(excelRow.getAnti() != null){
                paymentDetailsAntiSet.add(excelRow.getAnti().longValue());
            }
        }

        List<ManagePaymentTransactionTypeDto> managePaymentTransactionTypeList = getTransactionType(new ArrayList<>(transactionCodeSet));
        List<ManageBookingDto> bookings = getBookings(new ArrayList<>(bookingsIdSet));
        List<PaymentDto> paymentList = getPayments(new ArrayList<>(paymentIdSet));
        List<PaymentDetailDto> paymentDetailList = getPaymentDetailsProyection(new ArrayList<>(paymentIdSet));
        List<ManageBookingDto> bookingsByCouponList = getBookingByCoupon(new ArrayList<>(couponNumberSet));
        List<PaymentDetailDto> paymentDetailListAnti = getPaymentDetailListByGenId(new ArrayList<>(paymentDetailsAntiSet));

        List<UUID> hotelIds = paymentList.stream()
                .map(payment -> payment.getHotel().getId())
                .distinct()
                .toList();

        List<PaymentCloseOperationDto> closeOperationList = getCloseOperationDateTimeByHotel(hotelIds);

        return new Cache(managePaymentTransactionTypeList,
                bookings,
                paymentList,
                paymentDetailList,
                bookingsByCouponList,
                closeOperationList,
                paymentDetailListAnti,
                employee);
    }

    private Cache createCache(List<PaymentImportCache> paymentImportCacheList, ManageEmployeeDto employeeDto){
        Set<String> transactionIdSet = new HashSet<>();
        Set<Long> bookingsIdSet = new HashSet<>();
        Set<Long> paymentIdSet = new HashSet<>();
        Set<String> couponNumberSet = new HashSet<>();
        Set<Long> paymentDetailsAntiSet = new HashSet<>();

        for(PaymentImportCache paymentImportCache : paymentImportCacheList){
            if(Objects.nonNull(paymentImportCache.getTransactionId())){
                transactionIdSet.add(paymentImportCache.getTransactionId());
            }

            if(Objects.nonNull(paymentImportCache.getBookId())){
                bookingsIdSet.add(Long.parseLong(paymentImportCache.getBookId()));
            }

            if(Objects.nonNull(paymentImportCache.getPaymentId())){
                paymentIdSet.add(Long.parseLong(paymentImportCache.getPaymentId()));
            }

            if(Objects.nonNull(paymentImportCache.getCoupon())){
                couponNumberSet.add(paymentImportCache.getCoupon());
            }

            if(paymentImportCache.getAnti() != null){
                paymentDetailsAntiSet.add(Long.parseLong(paymentImportCache.getAnti()));
            }
        }

        List<ManagePaymentTransactionTypeDto> managePaymentTransactionTypeList = getTransactionType(new ArrayList<>(transactionIdSet));
        List<ManageBookingDto> bookings = getBookings(new ArrayList<>(bookingsIdSet));
        List<PaymentDto> paymentList = getPayments(new ArrayList<>(paymentIdSet));
        List<PaymentDetailDto> paymentDetailList = getPaymentDetailsProyection(new ArrayList<>(paymentIdSet));
        List<ManageBookingDto> bookingsByCouponList = getBookingByCoupon(new ArrayList<>(couponNumberSet));
        List<PaymentDetailDto> paymentDetailListAnti = new ArrayList<>(getPaymentDetailListByGenId(new ArrayList<>(paymentDetailsAntiSet)));

        List<UUID> hotelIds = paymentList.stream()
                .map(payment -> payment.getHotel().getId())
                .distinct()
                .toList();

        List<PaymentCloseOperationDto> closeOperationList = getCloseOperationDateTimeByHotel(hotelIds);

        return new Cache(managePaymentTransactionTypeList,
                bookings,
                paymentList,
                paymentDetailList,
                bookingsByCouponList,
                closeOperationList,
                paymentDetailListAnti,
                employeeDto);
    }

    private List<ManagePaymentTransactionTypeDto> getTransactionType(List<String> ids){
        List<ManagePaymentTransactionTypeDto> managePaymentTransactionTypeList = new ArrayList<>(transactionTypeService.findByCodesAndPaymentInvoice(ids));
        ManagePaymentTransactionTypeDto depositTransactionType = transactionTypeService.findByDeposit();
        addManagePaymentTransactionTypeToList(depositTransactionType, managePaymentTransactionTypeList);

        ManagePaymentTransactionTypeDto cashTransactionType = transactionTypeService.findByCash();
        addManagePaymentTransactionTypeToList(cashTransactionType, managePaymentTransactionTypeList);

        ManagePaymentTransactionTypeDto antiTransactionType = transactionTypeService.findByApplyDeposit();
        addManagePaymentTransactionTypeToList(antiTransactionType, managePaymentTransactionTypeList);

        return managePaymentTransactionTypeList;
    }

    private void addManagePaymentTransactionTypeToList(ManagePaymentTransactionTypeDto transactionType, List<ManagePaymentTransactionTypeDto> list){
        if(list.stream().noneMatch(manageTransactionType -> manageTransactionType.getCode().equals(transactionType.getCode()))){
            list.add(transactionType);
        }
    }

    private List<ManageBookingDto> getBookings(List<Long> ids){
        return bookingService.findByBookingIdIn(ids);
    }

    private List<PaymentDto> getPayments(List<Long> paymentIds){
        return paymentService.findPaymentsByPaymentId(paymentIds);
    }

    private List<PaymentDetailDto> getPaymentDetailsProyection(List<Long> paymentsIds){
        return paymentDetailService.findSimpleDetailsByPaymentGenIds(paymentsIds);
    }

    private List<ManageBookingDto> getBookingByCoupon(List<String> couponNumbers){
        return bookingService.findAllBookingByCoupons(couponNumbers);
    }

    private List<ManageInvoiceDto> getInvoicesByGenId(List<Long> invoiceIds){
        return new ArrayList<>(invoiceService.findInvoicesByGenId(invoiceIds));
    }

    private List<PaymentCloseOperationDto> getCloseOperationDateTimeByHotel(List<UUID> hotelIds){
        return this.paymentCloseOperationService.findByHotelIds(hotelIds);
    }

    private ManagePaymentStatusDto getPaymentStatusApplied(){
        return paymentStatusService.findByApplied();
    }

    private List<PaymentDetailDto> getPaymentDetailListByGenId(List<Long> ids){
        return paymentDetailService.findByPaymentDetailsIdIn(ids).stream()
                .map(PaymentDetail::toAggregate).collect(Collectors.toList());
    }
}
