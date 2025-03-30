package com.kynsoft.finamer.payment.infrastructure.services.helpers;

import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsoft.finamer.payment.application.command.paymentDetailApplyDeposit.create.CreatePaymentDetailApplyDepositCommand;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailRequest;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailSimpleDto;
import com.kynsoft.finamer.payment.domain.dto.projection.PaymentProjection;
import com.kynsoft.finamer.payment.domain.dto.projection.PaymentProjectionSimple;
import com.kynsoft.finamer.payment.domain.dto.projection.booking.BookingProjectionControlAmountBalance;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentDetailRowError;
import com.kynsoft.finamer.payment.domain.services.AbstractPaymentImportHelperService;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.excel.PaymentCacheFactory;
import com.kynsoft.finamer.payment.infrastructure.excel.event.applyDeposit.ApplyDepositEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.event.createPayment.CreatePaymentDetailEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.event.deposit.DepositEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.detail.PaymentDetailValidatorFactory;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageEmployeeReadDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportDetailErrorRepository;

import com.kynsoft.finamer.payment.infrastructure.services.helpers.entity.CreatePaymentDetail;
import io.jsonwebtoken.lang.Assert;

import java.time.LocalDateTime;
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
    private final ManageEmployeeReadDataJPARepository employeeReadDataJPARepository;

    private static final Logger logger = LoggerFactory.getLogger(PaymentImportDetailHelperServiceImpl.class);

    private Map<String, ManagePaymentTransactionTypeDto> managePaymentTransactionTypeMap;
    private Map<Long, BookingProjectionControlAmountBalance> bookingControlAmountBalanceMap;
    private Map<Long, PaymentProjection> paymentProjectionMap;
    private Map<UUID, Map<Long, PaymentDetailSimpleDto>> paymentDetailsProyectionMap;
    private Map<String, List<BookingProjectionControlAmountBalance>> bookingControlAmountBalanceByCouponMap;

    private List<CreatePaymentDetail> createPaymentDetailList = new ArrayList<>();

    public PaymentImportDetailHelperServiceImpl(PaymentImportCacheRepository paymentImportCacheRepository,
            PaymentDetailValidatorFactory paymentDetailValidatorFactory,
            ApplicationEventPublisher applicationEventPublisher,
            IManagePaymentTransactionTypeService transactionTypeService,
            IPaymentService paymentService, IPaymentDetailService paymentDetailService,
            PaymentImportDetailErrorRepository detailErrorRepository,
            IManageBookingService bookingService,
            ManageEmployeeReadDataJPARepository employeeReadDataJPARepository) {
        this.paymentImportCacheRepository = paymentImportCacheRepository;
        this.paymentDetailValidatorFactory = paymentDetailValidatorFactory;
        this.applicationEventPublisher = applicationEventPublisher;
        this.transactionTypeService = transactionTypeService;
        this.paymentService = paymentService;
        this.paymentDetailService = paymentDetailService;
        this.detailErrorRepository = detailErrorRepository;
        this.bookingService = bookingService;
        this.employeeReadDataJPARepository = employeeReadDataJPARepository;
    }

    @Override
    public void readExcel(ReaderConfiguration readerConfiguration, Object rawRequest) {
        printLog("Start readExcel process");
        this.totalProcessRow = 0;
        PaymentImportDetailRequest request = (PaymentImportDetailRequest) rawRequest;
        List<UUID> agencys = this.employeeReadDataJPARepository.findAgencyIdsByEmployeeId(UUID.fromString(request.getEmployeeId()));
        List<UUID> hotels = this.employeeReadDataJPARepository.findHotelsIdsByEmployeeId(UUID.fromString(request.getEmployeeId()));

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
        List<PaymentImportCache> paymentCacheList = paymentImportCacheRepository.findAllByImportProcessId(request.getImportProcessId());
        this.createCache(paymentCacheList);

        paymentCacheList.forEach(paymentImportCache -> {
            ManagePaymentTransactionTypeDto managePaymentTransactionTypeDto = this.getManageTransactionTypeByCode(paymentImportCache.getTransactionId());
            Assert.notNull(managePaymentTransactionTypeDto, "Transaction type is null");
            PaymentProjection paymentDto = paymentProjectionMap.get(Long.parseLong(paymentImportCache.getPaymentId()));
            PaymentProjectionSimple paymentProjectionSimple = new PaymentProjectionSimple(paymentDto.getId(), paymentDto.getPaymentId());
            BookingProjectionControlAmountBalance bookingDto = paymentImportCache.getBookId() != null ? bookingControlAmountBalanceMap.get(Long.parseLong(paymentImportCache.getBookId())) : null;
            if ( Objects.nonNull(bookingDto) && bookingDto.getBookingAmountBalance() == 0) {
                sendToDeposit(paymentImportCache, paymentDto);
                return;
            }
            if (Objects.nonNull(paymentImportCache.getAnti()) && !paymentImportCache.getAnti().isEmpty()) {
                boolean applyPayment = true;
                if (bookingDto == null) {
                    applyPayment = false;
                }

                PaymentDetailSimpleDto paymentDetailDto = this.findPaymentDetailInMap(paymentProjectionSimple.getId(), Long.parseLong(paymentImportCache.getAnti()));
                //TODO: No se si validar si el paymentDetailDto es nulo, se supone que en la validacion previa se valido que exista el paymentDetail, y si se valida
                // que es null, que se hace a continuacion
                this.sendToCreateApplyDeposit(paymentDetailDto.getId(),
                        Double.parseDouble(paymentImportCache.getPaymentAmount()),
                        UUID.fromString(request.getEmployeeId()),
                        managePaymentTransactionTypeDto.getId(),
                        null,
                        getRemarks(paymentImportCache, managePaymentTransactionTypeDto),
                        bookingDto != null ? bookingDto.getBookingId() : null,
                        applyPayment
                );

            } else {
                if (bookingDto == null) {
                    try {
                        if(couponIsDuplicated(paymentImportCache.getCoupon())){
                            //todo: implementar cuando existe mas de un booking con este coupon
                            //ManagePaymentTransactionTypeDto transactionTypeDto = this.transactionTypeService.findByPaymentInvoice();
                            ManagePaymentTransactionTypeDto transactionTypeDto = this.getPaymentInvoiceTransactionType();
                            String remarks = getRemarks(paymentImportCache, transactionTypeDto) + " #payment was not applied because the coupon is duplicated.";

                            //detail sin booking, transaction type tipo cash, sin aplicar pago, tomando directo el amount que viene en el excel y con remark modificado
                            createDetailAndDeposit(paymentImportCache,
                                    null,
                                    transactionTypeDto,
                                    paymentProjectionSimple,
                                    request,
                                    false,
                                    Double.parseDouble(paymentImportCache.getPaymentAmount()),
                                    remarks);
                        } else {
                            BookingProjectionControlAmountBalance bookingProjection = this.getBookingProjectionByCoupon(paymentImportCache.getCoupon());
                            if(Objects.isNull(bookingProjection)){
                                //todo: implementar cuando no existe el booking
                                DepositEvent depositEvent = getDepositEvent(paymentImportCache,
                                        paymentProjectionSimple,
                                        Double.parseDouble(paymentImportCache.getPaymentAmount()),
                                        true,
                                        "#coupon not found");
                                this.applicationEventPublisher.publishEvent(depositEvent);
                            }else{
                                //todo: implementar el caso de que existe el booking
                                //mismo flujo de cuando existe el booking por el id, en este caso el que se encuentra por el coupon
                                if (bookingProjection.getBookingAmountBalance() == 0) {
                                    sendToDeposit(paymentImportCache, paymentDto);
                                    return;
                                }
                                createDetailAndDeposit(paymentImportCache,
                                        bookingProjection,
                                        managePaymentTransactionTypeDto,
                                        paymentProjectionSimple,
                                        request,
                                        true,
                                        Double.parseDouble(paymentImportCache.getPaymentAmount()),
                                        getRemarks(paymentImportCache, managePaymentTransactionTypeDto));
                            }
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    createDetailAndDeposit(paymentImportCache,
                                            bookingDto,
                                            managePaymentTransactionTypeDto,
                                            paymentProjectionSimple,
                                            request,
                                true,
                                            Double.parseDouble(paymentImportCache.getPaymentAmount()),
                                            getRemarks(paymentImportCache, managePaymentTransactionTypeDto));
                }
            }
        });
        this.clearCache();
        printLog("End readPaymentCacheAndSave process");
    }

    private void sendToDeposit(PaymentImportCache paymentImportCache, PaymentProjection paymentProjection) {
        double rest = paymentProjection.getPaymentBalance() - Double.parseDouble(paymentImportCache.getPaymentAmount());
        if (rest >= 0) {
            DepositEvent depositEvent = getDepositEvent(paymentImportCache, new PaymentProjectionSimple(paymentProjection.getId(), paymentProjection.getPaymentId()),
                    Double.parseDouble(paymentImportCache.getPaymentAmount()), false, "");
            this.applicationEventPublisher.publishEvent(depositEvent);
        }
        return;
    }

    private void createDetailAndDeposit(PaymentImportCache paymentImportCache, BookingProjectionControlAmountBalance bookingDto, ManagePaymentTransactionTypeDto managePaymentTransactionTypeDto, PaymentProjectionSimple paymentDto, PaymentImportDetailRequest request, boolean applyPayment, double amount, String remarks) {
        //cash
        if (bookingDto != null) {
            amount = Math.min(bookingDto.getBookingAmountBalance(), Double.parseDouble(paymentImportCache.getPaymentAmount()));
        }

        if (managePaymentTransactionTypeDto.getCash() || managePaymentTransactionTypeDto.getPaymentInvoice()) {
            this.sendCreatePaymentDetail(
                    paymentDto.getId(),
                    amount,
                    UUID.fromString(request.getEmployeeId()),
                    managePaymentTransactionTypeDto.getId(),
                    remarks,
                    bookingDto != null ? bookingDto.getBookingId() : null,
                    applyPayment);

            //Crear el deposit.
            double restAmount = Double.valueOf(paymentImportCache.getPaymentAmount()) - amount;
            if (restAmount > 0) {
                DepositEvent depositEvent = getDepositEvent(paymentImportCache, paymentDto, restAmount, false, "");
                this.applicationEventPublisher.publishEvent(depositEvent);
            }
        } else {
            ///Other deductions
            this.sendCreatePaymentDetail(paymentDto.getId(),
                    Double.parseDouble(paymentImportCache.getPaymentAmount()),
                    UUID.fromString(request.getEmployeeId()),
                    managePaymentTransactionTypeDto.getId(),
                    remarks,
                    bookingDto != null ? bookingDto.getBookingId() : null,
                    applyPayment);
        }
    }

    private DepositEvent getDepositEvent(PaymentImportCache paymentImportCache, PaymentProjectionSimple paymentDto, double restAmount, boolean byCoupon, String remarks) {
        DepositEvent depositEvent = new DepositEvent(this);
        depositEvent.setAmount(restAmount);
        depositEvent.setPaymentDto(paymentDto);
        //depositEvent.setRemark("Create deposit in import details.");
        String invoiceNo = paymentImportCache.getInvoiceNo() != null ? paymentImportCache.getInvoiceNo() : "";
        String firstName = paymentImportCache.getFirstName() != null ? paymentImportCache.getFirstName() : "";
        String lastName = paymentImportCache.getLastName() != null ? paymentImportCache.getLastName() : "";
        String bookingNo = paymentImportCache.getBookingNo() != null ? paymentImportCache.getBookingNo() : "";

        if (byCoupon) {
            depositEvent.setRemark(remarks);
        } else {
            depositEvent.setRemark("S/P " + invoiceNo + " " + firstName + " " + lastName + " " + bookingNo);
        }
        return depositEvent;
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

    private void sendCreatePaymentDetail(UUID paymentId, double amount,
            UUID employee,
            UUID transactionType,
            String remarks,
            UUID bookId,
            boolean applyPayment) {
        CreatePaymentDetail createPaymentDetailEvent = new CreatePaymentDetail(this);
        CreatePaymentDetail.setPayment(paymentId);
        CreatePaymentDetail.setAmount(amount);
        CreatePaymentDetail.setStatus(Status.ACTIVE);
        CreatePaymentDetail.setEmployee(employee);
        CreatePaymentDetail.setTransactionType(transactionType);
        CreatePaymentDetail.setRemark(remarks);
        CreatePaymentDetail.setBooking(bookId);
        CreatePaymentDetail.setApplyPayment(applyPayment);
        //applicationEventPublisher.publishEvent(createPaymentDetailEvent);
        this.preparePaymentDetails(createPaymentDetailEvent);
    }

    private void preparePaymentDetails(CreatePaymentDetail createPaymentDetail){
        createPaymentDetailList.add(CreatePaymentDetail);
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

    private void printLog(String message){
        logger.info(message + " at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
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
        bookingControlAmountBalanceMap = getBookingsMap(bookingsId);

        List<Long> paymentIds = paymentImportCacheList.stream()
                .map(paymentImportCache -> Long.parseLong(paymentImportCache.getPaymentId()))
                .toList();
        paymentProjectionMap = getPaymentsMap(paymentIds);

        paymentDetailsProyectionMap = getPaymentDetailsProyectionMap(paymentIds);

        List<String> couponNumbers = paymentImportCacheList.stream()
                .map(PaymentImportCache::getCoupon)
                .distinct()
                .toList();

        bookingControlAmountBalanceByCouponMap = getBookingByCouponMap(couponNumbers);
    }

    private Map<String, ManagePaymentTransactionTypeDto> getTransactionTypeMap(List<String> ids){
        return transactionTypeService.findByCodesAndPaymentInvoice(ids).stream()
                .collect(Collectors.toMap(ManagePaymentTransactionTypeDto::getCode, managePaymentTransactionTypeDto -> managePaymentTransactionTypeDto));
    }

    private Map<Long, BookingProjectionControlAmountBalance> getBookingsMap(List<Long> ids){
        return bookingService.findAllSimpleBookingByGenId(ids).stream()
                .collect(Collectors.toMap(BookingProjectionControlAmountBalance::getBookingGenId, booking -> booking));
    }

    private Map<Long, PaymentProjection> getPaymentsMap(List<Long> paymentIds){
        return paymentService.findPaymentsByPaymentId(paymentIds).stream()
                .collect(Collectors.toMap(PaymentProjection::getPaymentId, paymentProjection -> paymentProjection));
    }

    private Map<UUID, Map<Long, PaymentDetailSimpleDto>> getPaymentDetailsProyectionMap(List<Long> paymentsIds){
        return paymentDetailService.findSimpleDetailsByPaymentGenIds(paymentsIds).stream()
                .collect(Collectors.groupingBy(PaymentDetailSimpleDto::getPaymentId, Collectors.toMap(PaymentDetailSimpleDto::getPaymentDetailId, dto -> dto)));
    }

    private Map<String, List<BookingProjectionControlAmountBalance>> getBookingByCouponMap(List<String> couponNumbers){
        return bookingService.findAllBookingProjectionControlAmountBalanceByCoupons(couponNumbers).stream()
                .collect(Collectors.groupingBy(BookingProjectionControlAmountBalance::getCouponNumber));
    }

    private PaymentDetailSimpleDto findPaymentDetailInMap(UUID paymentId, Long paymentDetailGenId){
        Map<Long, PaymentDetailSimpleDto> paymentDetails = paymentDetailsProyectionMap.get(paymentId);
        return paymentDetails.get(paymentDetailGenId);
    }

    private ManagePaymentTransactionTypeDto getManageTransactionTypeByCode(String code){
        if(Objects.isNull(managePaymentTransactionTypeMap) || managePaymentTransactionTypeMap.isEmpty()){
            return null;
        }

        return managePaymentTransactionTypeMap.get(code);
    }

    private Boolean couponIsDuplicated(String coupon){
        if(Objects.isNull(bookingControlAmountBalanceByCouponMap) || bookingControlAmountBalanceByCouponMap.isEmpty()){
            return false;
        }

        List<BookingProjectionControlAmountBalance> bookingList = bookingControlAmountBalanceByCouponMap.get(coupon);
        if(Objects.isNull(bookingList) || bookingList.isEmpty()){
            return false;
        }

        return bookingList.size() != 1;
    }

    private BookingProjectionControlAmountBalance getBookingProjectionByCoupon(String coupon){
        if(Objects.isNull(bookingControlAmountBalanceByCouponMap) || bookingControlAmountBalanceByCouponMap.isEmpty()){
            return null;
        }

        List<BookingProjectionControlAmountBalance> bookingList = bookingControlAmountBalanceByCouponMap.get(coupon);
        if(Objects.isNull(bookingList) || bookingList.isEmpty()){
            return null;
        }

        return bookingList.get(0);
    }

    private ManagePaymentTransactionTypeDto getPaymentInvoiceTransactionType(){
        if(Objects.isNull(managePaymentTransactionTypeMap) || managePaymentTransactionTypeMap.isEmpty()){
            return null;
        }

        return managePaymentTransactionTypeMap.entrySet().stream()
                .filter(transactionType     -> transactionType.getValue().getPaymentInvoice())
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }
}
