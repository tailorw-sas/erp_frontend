package com.kynsoft.finamer.payment.infrastructure.services.helpers;

import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsof.share.utils.BankerRounding;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.payment.application.command.paymentDetailApplyDeposit.create.CreatePaymentDetailApplyDepositCommand;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailRequest;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dto.projection.PaymentProjectionSimple;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentDetailRowError;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.excel.PaymentCacheFactory;
import com.kynsoft.finamer.payment.infrastructure.excel.event.applyDeposit.ApplyDepositEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.detail.PaymentDetailValidatorFactory;
import com.kynsoft.finamer.payment.infrastructure.identity.Booking;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportDetailErrorRepository;

import com.kynsoft.finamer.payment.infrastructure.services.helpers.entity.CreatePaymentDetail;
import com.kynsoft.finamer.payment.infrastructure.services.helpers.entity.Deposit.Deposit;
import com.kynsoft.finamer.payment.infrastructure.services.helpers.entity.applyPayment.ApplyOtherDeduction;
import com.kynsoft.finamer.payment.infrastructure.services.helpers.entity.applyPayment.ApplyPayment;
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

    private static final Logger logger = LoggerFactory.getLogger(PaymentImportDetailHelperServiceImpl.class);

    private ManageEmployeeDto employee;
    private Map<String, ManagePaymentTransactionTypeDto> managePaymentTransactionTypeMap;
    private Map<Long, ManageBookingDto> bookingMap;
    private Map<Long, PaymentDto> paymentProjectionMap;
    private Map<UUID, Map<Long, PaymentDetailDto>> paymentDetailsProyectionMap;
    private Map<String, List<ManageBookingDto>> bookingByCouponMap;
    private Map<UUID, PaymentCloseOperationDto> closeOperationDateTimeByHotelMap;

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
                                                ProducerUpdateBookingService producerUpdateBookingService) {
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
    }

    @Override
    public void readExcel(ReaderConfiguration readerConfiguration, Object rawRequest) {
        printLog("Start readExcel process");
        this.totalProcessRow = 0;
        PaymentImportDetailRequest request = (PaymentImportDetailRequest) rawRequest;

        employee = this.getEmployee(UUID.fromString(request.getEmployeeId()));
        List<UUID> agencys = this.getEmployeeAgencyList(employee);
        List<UUID> hotels = this.getEmployeeHotelList(employee);

        paymentDetailValidatorFactory.createValidators();
        ExcelBeanReader<PaymentDetailRow> excelBeanReader = new ExcelBeanReader<>(readerConfiguration, PaymentDetailRow.class);
        ExcelBean<PaymentDetailRow> excelBean = new ExcelBean<>(excelBeanReader);
        for (PaymentDetailRow row : excelBean) {
            row.setImportProcessId(request.getImportProcessId());
            row.setImportType(request.getImportPaymentType().name());
            row.setAgencys(agencys);
            row.setHotels(hotels);
            if (Objects.nonNull(request.getPaymentId())
                    && !request.getPaymentId().isEmpty()) {
                row.setExternalPaymentId(UUID.fromString(request.getPaymentId()));
            }
            if (paymentDetailValidatorFactory.validate(row)) {
                cachingPaymentImport(row);
                this.totalProcessRow++;
            }
        }
        printLog("End readExcel process");
    }

    private void clearCache() {
        this.transactionTypeService.clearCache();
        this.managePaymentTransactionTypeMap.clear();
        this.bookingMap.clear();
        this.paymentProjectionMap.clear();
        this.paymentDetailsProyectionMap.clear();
        this.bookingByCouponMap.clear();
        this.closeOperationDateTimeByHotelMap.clear();
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

    private ManageBookingDto getBooking(String bookingId){
        if(Objects.isNull(bookingId)){
            return null;
        }

        return bookingMap.get(Long.parseLong(bookingId));
    }

    @Override
    public void readPaymentCacheAndSave(Object rawRequest) {
        PaymentImportDetailRequest request = (PaymentImportDetailRequest) rawRequest;
        printLog("Start readPaymentCacheAndSave process");
        List<PaymentImportCache> paymentCacheList = this.getPaymentImportCachedList(request.getImportProcessId());
        this.createCache(paymentCacheList);
        List<CreatePaymentDetail> createPaymentDetailList = new ArrayList<>();//TODO Validar una lista segura para implementar en hilos
        List<PaymentDetailDto> createDepositList = new ArrayList<>();

        for(PaymentImportCache paymentImportCache : paymentCacheList ){
            ManagePaymentTransactionTypeDto managePaymentTransactionTypeDto = this.getManageTransactionTypeByCode(paymentImportCache.getTransactionId());
            Assert.notNull(managePaymentTransactionTypeDto, "Transaction type is null");

            PaymentDto paymentDto = this.getPaymentByPaymentId(Long.parseLong(paymentImportCache.getPaymentId()));
            if(Objects.isNull(paymentDto)){
                break;
            }

            PaymentProjectionSimple paymentProjectionSimple = new PaymentProjectionSimple(paymentDto.getId(), paymentDto.getPaymentId());

            ManageBookingDto bookingDto = this.getBooking(paymentImportCache.getBookId());

            if ( Objects.nonNull(bookingDto) && bookingDto.getAmountBalance() == 0) {
                PaymentDetailDto paymentDetailTypeDeposit = sendDeposit(paymentImportCache, paymentDto, Double.parseDouble(paymentImportCache.getPaymentAmount()));
                createDepositList.add(paymentDetailTypeDeposit);
            }else{
                if (Objects.nonNull(paymentImportCache.getAnti()) && !paymentImportCache.getAnti().isEmpty()) {
                    boolean applyPayment = bookingDto != null;

                    PaymentDetailDto paymentDetailDto = this.findPaymentDetailInMap(paymentProjectionSimple.getId(), Long.parseLong(paymentImportCache.getAnti()));
                    this.sendToCreateApplyDeposit(paymentDetailDto.getId(),
                            Double.parseDouble(paymentImportCache.getPaymentAmount()),
                            UUID.fromString(request.getEmployeeId()),
                            managePaymentTransactionTypeDto.getId(),
                            null,
                            getRemarks(paymentImportCache, managePaymentTransactionTypeDto),
                            bookingDto != null ? bookingDto.getId() : null,
                            applyPayment
                    );

                } else {
                    if (bookingDto == null) {
                        if(couponIsDuplicated(paymentImportCache.getCoupon())){
                            //TODO: implementar cuando existe mas de un booking con este coupon
                            ManagePaymentTransactionTypeDto transactionTypeDto = this.getPaymentInvoiceTransactionType();
                            String remarks = getRemarks(paymentImportCache, transactionTypeDto) + " #payment was not applied because the coupon is duplicated.";

                            //detail sin booking, transaction type tipo cash, sin aplicar pago, tomando directo el amount que viene en el excel y con remark modificado
                            createDetailAndDeposit(paymentImportCache,
                                    null,
                                    transactionTypeDto,
                                    paymentDto,
                                    false,
                                    Double.parseDouble(paymentImportCache.getPaymentAmount()),
                                    remarks,
                                    createPaymentDetailList,
                                    createDepositList);
                        } else {
                            ManageBookingDto booking = this.getBookingProjectionByCoupon(paymentImportCache.getCoupon());
                            if(Objects.isNull(booking)){
                                //TODO: implementar cuando no existe el booking
                                PaymentDetailDto paymentDetailTypeDeposit = getDeposit(paymentImportCache,
                                        paymentDto,
                                        Double.parseDouble(paymentImportCache.getPaymentAmount()),
                                        true,
                                        "#coupon not found");
                                createDepositList.add(paymentDetailTypeDeposit);
                                //TODO Implementar el bloque y validar si es lo mismo que createDetailAndDeposit
                            }else{
                                //TODO: implementar el caso de que existe el booking
                                //mismo flujo de cuando existe el booking por el id, en este caso el que se encuentra por el coupon
                                if (booking.getAmountBalance() == 0) {
                                    PaymentDetailDto paymentDetailTypeDeposit = sendDeposit(paymentImportCache, paymentDto, Double.parseDouble(paymentImportCache.getPaymentAmount()));
                                    createDepositList.add(paymentDetailTypeDeposit);
                                    return;
                                }
                                createDetailAndDeposit(paymentImportCache,
                                        booking,
                                        managePaymentTransactionTypeDto,
                                        paymentDto,
                                        true,
                                        Double.parseDouble(paymentImportCache.getPaymentAmount()),
                                        getRemarks(paymentImportCache, managePaymentTransactionTypeDto),
                                        createPaymentDetailList,
                                        createDepositList);
                            }
                        }
                    } else {
                        createDetailAndDeposit(paymentImportCache,
                                bookingDto,
                                managePaymentTransactionTypeDto,
                                paymentDto,
                                true,
                                Double.parseDouble(paymentImportCache.getPaymentAmount()),
                                getRemarks(paymentImportCache, managePaymentTransactionTypeDto),
                                createPaymentDetailList,
                                createDepositList);
                    }
                }
            }
        }

        this.createPaymentDetails(createPaymentDetailList);
        this.createPaymentDetailsAsDeposit(createDepositList);

        this.clearCache();
        printLog("End readPaymentCacheAndSave process");
    }

    private PaymentDetailDto sendDeposit(PaymentImportCache paymentImportCache, PaymentDto paymentDto, Double amount){
        double restAmount = BankerRounding.round(paymentDto.getPaymentAmount() - amount);
        if (restAmount > 0) {
            return getDeposit(paymentImportCache, paymentDto, Double.parseDouble(paymentImportCache.getPaymentAmount()), false, "");
        }
        return null;
    }

    private void createDetailAndDeposit(PaymentImportCache paymentImportCache,
                                        ManageBookingDto bookingDto,
                                        ManagePaymentTransactionTypeDto managePaymentTransactionTypeDto,
                                        PaymentDto paymentDto,
                                        boolean applyPayment,
                                        double amount,
                                        String remarks,
                                        List<CreatePaymentDetail> createPaymentDetailList,
                                        List<PaymentDetailDto> createDepositList) {
        //cash
        if (bookingDto != null) {
            amount = Math.min(bookingDto.getAmountBalance(), Double.parseDouble(paymentImportCache.getPaymentAmount()));
        }

        if (managePaymentTransactionTypeDto.getCash() || managePaymentTransactionTypeDto.getPaymentInvoice()) {
            this.sendCreatePaymentDetail(
                    paymentDto,
                    amount,
                    employee,
                    managePaymentTransactionTypeDto,
                    remarks,
                    bookingDto,
                    applyPayment,
                    createPaymentDetailList);

            //Crear el deposit con el valor sobrante no cubierto por el booking (restante del importPaymentCache)
            double restAmount = BankerRounding.round(Double.parseDouble(paymentImportCache.getPaymentAmount()) - amount);
            if (restAmount > 0) {
                PaymentDetailDto paymentDetailTypeDeposit = getDeposit(paymentImportCache, paymentDto, Double.parseDouble(paymentImportCache.getPaymentAmount()), false, "");
                createDepositList.add(paymentDetailTypeDeposit);
            }
        } else {
            //Other deductions
            this.sendCreatePaymentDetail(paymentDto,
                    Double.parseDouble(paymentImportCache.getPaymentAmount()),
                    employee,
                    managePaymentTransactionTypeDto,
                    remarks,
                    bookingDto,
                    applyPayment,
                    createPaymentDetailList);
        }
    }

    private PaymentDetailDto getDeposit(PaymentImportCache paymentImportCache, PaymentDto paymentDto, double restAmount, boolean byCoupon, String remarks) {
        ManagePaymentTransactionTypeDto paymentTransactionType = getDepositTransactionType();//TODO Incluir en el mapa de transaction type el deposit

        String invoiceNo = paymentImportCache.getInvoiceNo() != null ? paymentImportCache.getInvoiceNo() : "";
        String firstName = paymentImportCache.getFirstName() != null ? paymentImportCache.getFirstName() : "";
        String lastName = paymentImportCache.getLastName() != null ? paymentImportCache.getLastName() : "";
        String bookingNo = paymentImportCache.getBookingNo() != null ? paymentImportCache.getBookingNo() : "";

        if (!byCoupon) {
            remarks = ("S/P " + invoiceNo + " " + firstName + " " + lastName + " " + bookingNo);
        }

        Deposit deposit = new Deposit(
            paymentDto,
            restAmount,
            remarks,
            paymentTransactionType,
            getTransactionDate(paymentDto.getHotel().getId())
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

    private void sendCreatePaymentDetail(PaymentDto payment,
                                         double amount,
                                         ManageEmployeeDto employee,
                                         ManagePaymentTransactionTypeDto transactionType,
                                         String remarks,
                                         ManageBookingDto booking,
                                         boolean applyPayment,
                                         List<CreatePaymentDetail> createPaymentDetailList) {
        CreatePaymentDetail createPaymentDetail = new CreatePaymentDetail(
                employee,
                Status.ACTIVE,
                payment,
                transactionType,
                amount,
                remarks,
                booking,
                applyPayment,
                getTransactionDate(payment.getHotel().getId())
        );
        createPaymentDetailList.add(createPaymentDetail);
    }

    private void sendToCreateApplyDeposit(UUID paymentDetail, double amount, UUID employee, UUID transactionType,
            UUID transactionTypeIdForAdjustment, String remarks,
            UUID bookId,
            boolean applyPayment) {
        CreatePaymentDetailApplyDepositCommand createPaymentDetailApplyDepositCommand
                = new CreatePaymentDetailApplyDepositCommand(
                        Status.ACTIVE,
                        paymentDetail,
                        transactionType,
                        amount,
                        remarks,
                        employee,
                        transactionTypeIdForAdjustment,
                        bookId,
                        applyPayment
                );
        ApplyDepositEvent applyDepositEvent = new ApplyDepositEvent(createPaymentDetailApplyDepositCommand, false);
        applicationEventPublisher.publishEvent(applyDepositEvent);
    }

    private void createPaymentDetails(List<CreatePaymentDetail> createPaymentDetails){
        if(Objects.isNull(createPaymentDetails)){
            throw new IllegalArgumentException("The payment detail list must not be null");
        }
        ManagePaymentStatusDto paymentStatusApplied = getPaymentStatusApplied();
        List<PaymentDetailDto> paymentDetailsToCreate = new ArrayList<>();
        List<PaymentDto> paymentsToUpdate = new ArrayList<>();
        List<PaymentStatusHistoryDto> paymentStatusHistories = new ArrayList<>();
        List<ManageBookingDto> bookingsToUpdate = new ArrayList<>();

        for(CreatePaymentDetail createPaymentDetail : createPaymentDetails){
            PaymentDto paymentDto = createPaymentDetail.getPayment();
            ManagePaymentTransactionTypeDto paymentTransactionType = createPaymentDetail.getTransactionType();
            ManageBookingDto booking = createPaymentDetail.getBooking();

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

            if (updatePayment.getUpdate() > 0) {
                if (paymentDto.getPaymentBalance() == 0 && paymentDto.getDepositBalance() == 0) {
                    paymentDto.setPaymentStatus(paymentStatusApplied);
                }
            }

            //Apply Payment
            if (createPaymentDetail.getApplyPayment() && paymentTransactionType.getCash()){
                ApplyPayment applyPayment = new ApplyPayment(paymentDto,
                        newPaymentDetailDto,
                        booking,
                        createPaymentDetail.getTransactionDate(),
                        createPaymentDetail.getEmployee(),
                        paymentStatusApplied);
                applyPayment.applyPayment();
                bookingsToUpdate.add(booking);
                if(applyPayment.isApplied()){
                    PaymentStatusHistoryDto paymentStatusHistory = applyPayment.getPaymentStatusHistory();
                    paymentStatusHistories.add(paymentStatusHistory);
                }
            }


            //Apply Other Deductions
            if (otherDeductionAndApplyPayment){
                PaymentStatusHistoryDto paymentStatusHistory = new PaymentStatusHistoryDto();
                ApplyOtherDeduction applyOtherDeduction = new ApplyOtherDeduction(paymentDto,
                        newPaymentDetailDto,
                        booking,
                        paymentTransactionType,
                        createPaymentDetail.getTransactionDate(),
                        createPaymentDetail.getEmployee(),
                        paymentStatusApplied,
                        paymentStatusHistory);
                applyOtherDeduction.applyOtherDeduction();
                bookingsToUpdate.add(booking);
                paymentStatusHistories.add(paymentStatusHistory);
            }

            addPaymentToList(paymentDto, paymentsToUpdate);
            paymentDetailsToCreate.add(newPaymentDetailDto);
        }

        this.updateBookings(bookingsToUpdate);
        this.createPaymentsDetails(paymentDetailsToCreate);
        this.updatePayments(paymentsToUpdate);
        this.createPaymentStatusHistory(paymentStatusHistories);
        this.replicateBookingToKafka(paymentDetailsToCreate);
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

    private void createPaymentDetailsAsDeposit(List<PaymentDetailDto> depositList){
        if(Objects.isNull(depositList) || depositList.isEmpty()){
            printLog("The deposit list is null or empty");
            return;
        }

        List<PaymentDto> paymentsToUpdate = depositList.stream()
                        .map(PaymentDetailDto::getPayment)
                                .toList();

        paymentDetailService.bulk(depositList);
        paymentService.createBulk(paymentsToUpdate);
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

    private void createCache(List<PaymentImportCache> paymentImportCacheList){
        List<String> transactionIds = paymentImportCacheList.stream()
                .map(PaymentImportCache::getTransactionId)
                .distinct()
                .toList();
        managePaymentTransactionTypeMap = getTransactionTypeMap(transactionIds);

        List<Long> bookingsId = paymentImportCacheList.stream()
                .filter(paymentImportCache -> Objects.nonNull(paymentImportCache.getBookId()))
                .map(payment -> Long.parseLong(payment.getBookId()))
                .distinct()
                .toList();
        bookingMap = getBookingsMap(bookingsId);

        List<Long> paymentIds = paymentImportCacheList.stream()
                .map(paymentImportCache -> Long.parseLong(paymentImportCache.getPaymentId()))
                .toList();
        paymentProjectionMap = getPaymentsMap(paymentIds);

        paymentDetailsProyectionMap = getPaymentDetailsProyectionMap(paymentIds);

        List<String> couponNumbers = paymentImportCacheList.stream()
                .map(PaymentImportCache::getCoupon)
                .distinct()
                .toList();

        bookingByCouponMap = getBookingByCouponMap(couponNumbers);

        List<UUID> hotelCodes = paymentProjectionMap.values().stream()
                .toList().stream()
                .map(paymentDto -> paymentDto.getHotel().getId())
                .toList();

        closeOperationDateTimeByHotelMap = getCloseOperationDateTimeByHotelMap(hotelCodes);

    }

    private Map<String, ManagePaymentTransactionTypeDto> getTransactionTypeMap(List<String> ids){
        Map<String, ManagePaymentTransactionTypeDto> managePaymentTransactionTypeMap = transactionTypeService.findByCodesAndPaymentInvoice(ids).stream()
                .collect(Collectors.toMap(ManagePaymentTransactionTypeDto::getCode, managePaymentTransactionTypeDto -> managePaymentTransactionTypeDto));

        ManagePaymentTransactionTypeDto depositTransactionType = transactionTypeService.findByDeposit();
        managePaymentTransactionTypeMap.put(depositTransactionType.getCode(), depositTransactionType);

        return managePaymentTransactionTypeMap;
    }

    private Map<Long, ManageBookingDto> getBookingsMap(List<Long> ids){
        return bookingService.findByBookingIdIn(ids).stream()
                .collect(Collectors.toMap(ManageBookingDto::getBookingId, booking -> booking));
    }

    private Map<Long, PaymentDto> getPaymentsMap(List<Long> paymentIds){
        return paymentService.findPaymentsByPaymentId(paymentIds).stream()
                .collect(Collectors.toMap(PaymentDto::getPaymentId, payment -> payment));
    }

    private Map<UUID, Map<Long, PaymentDetailDto>> getPaymentDetailsProyectionMap(List<Long> paymentsIds){
        return paymentDetailService.findSimpleDetailsByPaymentGenIds(paymentsIds).stream()
                .collect(Collectors.groupingBy(paymentDetail -> paymentDetail.getPayment().getId(), Collectors.toMap(PaymentDetailDto::getPaymentDetailId, paymentDetail -> paymentDetail)));
    }

    private Map<String, List<ManageBookingDto>> getBookingByCouponMap(List<String> couponNumbers){
        return bookingService.findAllBookingByCoupons(couponNumbers).stream()
                .collect(Collectors.groupingBy(ManageBookingDto::getCouponNumber));
    }

    private Map<UUID, PaymentCloseOperationDto> getCloseOperationDateTimeByHotelMap(List<UUID> hotelCodes){
        return this.paymentCloseOperationService.findByHotelIds(hotelCodes).stream()
                .collect(Collectors.toMap(closeOperation -> closeOperation.getHotel().getId(), paymentCloseOperation -> paymentCloseOperation));
    }

    private PaymentDetailDto findPaymentDetailInMap(UUID paymentId, Long paymentDetailGenId){
        Map<Long, PaymentDetailDto> paymentDetails = paymentDetailsProyectionMap.get(paymentId);
        return paymentDetails.get(paymentDetailGenId);
    }

    private ManagePaymentTransactionTypeDto getManageTransactionTypeByCode(String code){
        if(Objects.isNull(managePaymentTransactionTypeMap) || managePaymentTransactionTypeMap.isEmpty()){
            printLog("The managePaymentTransactionTypeMap map is null or Empty");
            return null;
        }

        return managePaymentTransactionTypeMap.get(code);
    }

    private PaymentDto getPaymentByPaymentId(Long paymentId){
        if(Objects.isNull(paymentProjectionMap) || paymentProjectionMap.isEmpty()){
            printLog("The paymentProjectionMap map is null or Empty");
            return null;
        }

        return paymentProjectionMap.get(paymentId);
    }

    private Boolean couponIsDuplicated(String coupon){
        if(Objects.isNull(bookingByCouponMap) || bookingByCouponMap.isEmpty()){
            printLog("The bookingControlAmountBalanceByCouponMap map is null or Empty");
            return false;
        }

        List<ManageBookingDto> bookingList = bookingByCouponMap.get(coupon);
        if(Objects.isNull(bookingList) || bookingList.isEmpty()){
            return false;
        }

        return bookingList.size() != 1;
    }

    private ManageBookingDto getBookingProjectionByCoupon(String coupon){
        if(Objects.isNull(bookingByCouponMap) || bookingByCouponMap.isEmpty()){
            printLog("The bookingControlAmountBalanceByCouponMap map is null or Empty");
            return null;
        }

        List<ManageBookingDto> bookingList = bookingByCouponMap.get(coupon);
        if(Objects.isNull(bookingList) || bookingList.isEmpty()){
            return null;
        }

        return bookingList.get(0);
    }

    private ManagePaymentTransactionTypeDto getPaymentInvoiceTransactionType(){
        if(Objects.isNull(managePaymentTransactionTypeMap) || managePaymentTransactionTypeMap.isEmpty()){
            printLog("The managePaymentTransactionTypeMap map is null or Empty");
            return null;
        }

        return managePaymentTransactionTypeMap.values().stream()
                .filter(ManagePaymentTransactionTypeDto::getPaymentInvoice)
                .findFirst()
                .orElse(null);
    }

    private ManagePaymentTransactionTypeDto getDepositTransactionType(){
        if(Objects.isNull(managePaymentTransactionTypeMap) || managePaymentTransactionTypeMap.isEmpty()){
            printLog("The managePaymentTransactionTypeMap map is null or Empty");
            return null;
        }

        return managePaymentTransactionTypeMap.values().stream()
                .filter(ManagePaymentTransactionTypeDto::getDeposit)
                .findFirst()
                .orElse(null);
    }

    private OffsetDateTime getTransactionDate(UUID hotel){
        if(Objects.isNull(closeOperationDateTimeByHotelMap) || closeOperationDateTimeByHotelMap.isEmpty()){
            printLog("The managePaymentTransactionTypeMap map is null or Empty");
            return null;
        }

        PaymentCloseOperationDto paymentCloseOperation = closeOperationDateTimeByHotelMap.get(hotel);
        if (DateUtil.getDateForCloseOperation(paymentCloseOperation.getBeginDate(), paymentCloseOperation.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(paymentCloseOperation.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

    private ManagePaymentStatusDto getPaymentStatusApplied(){
        return paymentStatusService.findByApplied();
    }
}
