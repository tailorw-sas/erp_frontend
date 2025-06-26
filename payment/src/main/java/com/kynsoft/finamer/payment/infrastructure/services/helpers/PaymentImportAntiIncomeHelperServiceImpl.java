package com.kynsoft.finamer.payment.infrastructure.services.helpers;

import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsof.share.core.domain.http.entity.InvoiceHttp;
import com.kynsof.share.core.domain.http.entity.income.CreateAntiToIncomeRequest;
import com.kynsof.share.core.domain.http.entity.income.adjustment.CreateAntiToIncomeAdjustmentRequest;
import com.kynsof.share.core.domain.http.entity.income.attachment.CreateAntiToIncomeAttachmentRequest;
import com.kynsof.share.core.domain.http.entity.income.CreateAntiToIncomeFromPaymentRequest;
import com.kynsof.share.core.domain.http.entity.income.CreateAntiToIncomeFromPaymentMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailRequest;
import com.kynsoft.finamer.payment.application.services.paymentDetail.apply.ReplicateBookingBalanceService;
import com.kynsoft.finamer.payment.domain.core.applyPayment.ProcessApplyPaymentDetail;
import com.kynsoft.finamer.payment.domain.core.paymentDetail.ProcessCreatePaymentDetail;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dto.helper.DetailAndIncomeHelper;
import com.kynsoft.finamer.payment.domain.dto.helper.ReplicateBookingBalanceHelper;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentAntiRowError;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.excel.PaymentCacheFactory;
import com.kynsoft.finamer.payment.infrastructure.excel.event.createAttachment.CreateAttachmentEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.anti.PaymentAntiValidatorFactory;
import com.kynsoft.finamer.payment.infrastructure.identity.PaymentDetail;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportAntiErrorRepository;
import com.kynsoft.finamer.payment.infrastructure.services.http.CreateIncomeHttpService;
import com.kynsoft.finamer.payment.infrastructure.utils.PaymentUploadAttachmentUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import java.time.format.DateTimeFormatter;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;

@Service
public class PaymentImportAntiIncomeHelperServiceImpl extends AbstractPaymentImportHelperService {

    private final PaymentImportCacheRepository paymentImportCacheRepository;
    private final PaymentAntiValidatorFactory paymentAntiValidatorFactory;
    private final IPaymentDetailService paymentDetailService;
    private final IManagePaymentTransactionTypeService transactionTypeService;
    private final PaymentImportAntiErrorRepository antiErrorRepository;

    private final IManageInvoiceStatusService statusService;
    private final IManageInvoiceTypeService invoiceTypeService;
    private final IManageEmployeeService manageEmployeeService;

    private final CreateIncomeHttpService createIncomeHttpService;
    private final IPaymentCloseOperationService paymentCloseOperationService;
    private final IManagePaymentStatusService paymentStatusService;

    private final IPaymentStatusHistoryService paymentStatusHistoryService;
    private final PaymentUploadAttachmentUtil paymentUploadAttachmentUtil;
    private final ApplicationEventPublisher applicationEventPublisher;

    private final IManageHotelService manageHotelService;
    private final IManageAgencyService manageAgencyService;
    private final IManageInvoiceService manageInvoiceService;
    private final IPaymentService paymentService;
    private final ReplicateBookingBalanceService replicateBookingBalanceService;

    @Value("${payment.relate.invoice.status.code}")
    private String RELATE_INCOME_STATUS_CODE;

    private static final Logger logger = LoggerFactory.getLogger(PaymentImportAntiIncomeHelperServiceImpl.class);

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public PaymentImportAntiIncomeHelperServiceImpl(PaymentImportCacheRepository paymentImportCacheRepository,
                                                    PaymentAntiValidatorFactory paymentAntiValidatorFactory,
                                                    IPaymentDetailService paymentDetailService,
                                                    IManagePaymentTransactionTypeService transactionTypeService,
                                                    PaymentImportAntiErrorRepository antiErrorRepository,
                                                    IManageInvoiceStatusService statusService,
                                                    IManageInvoiceTypeService invoiceTypeService,
                                                    IManageEmployeeService manageEmployeeService,
                                                    CreateIncomeHttpService createIncomeHttpService,
                                                    IPaymentCloseOperationService paymentCloseOperationService,
                                                    IManagePaymentStatusService paymentStatusService,
                                                    IPaymentStatusHistoryService paymentAttachmentStatusHistoryService,
                                                    PaymentUploadAttachmentUtil paymentUploadAttachmentUtil,
                                                    ApplicationEventPublisher applicationEventPublisher,
                                                    IManageHotelService manageHotelService,
                                                    IManageAgencyService manageAgencyService,
                                                    IManageInvoiceService manageInvoiceService,
                                                    IPaymentService paymentService,
                                                    ReplicateBookingBalanceService replicateBookingBalanceService) {
        this.paymentImportCacheRepository = paymentImportCacheRepository;
        this.paymentAntiValidatorFactory = paymentAntiValidatorFactory;
        this.paymentDetailService = paymentDetailService;
        this.transactionTypeService = transactionTypeService;
        this.antiErrorRepository = antiErrorRepository;
        this.statusService = statusService;
        this.invoiceTypeService = invoiceTypeService;
        this.manageEmployeeService = manageEmployeeService;
        this.createIncomeHttpService = createIncomeHttpService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.paymentStatusService = paymentStatusService;
        this.paymentStatusHistoryService = paymentAttachmentStatusHistoryService;
        this.paymentUploadAttachmentUtil = paymentUploadAttachmentUtil;
        this.applicationEventPublisher = applicationEventPublisher;
        this.manageHotelService = manageHotelService;
        this.manageAgencyService = manageAgencyService;
        this.manageInvoiceService  = manageInvoiceService;
        this.paymentService = paymentService;
        this.replicateBookingBalanceService = replicateBookingBalanceService;
    }

    @Override
    public void readExcel(ReaderConfiguration readerConfiguration, Object rawRequest) {
        printLog("Start readExcel process");
        this.totalProcessRow = 0;
        PaymentImportDetailRequest request = (PaymentImportDetailRequest) rawRequest;
        ManageEmployeeDto employeeDto = this.manageEmployeeService.findById(UUID.fromString(request.getEmployeeId()));

        ExcelBeanReader<AntiToIncomeRow> excelBeanReader = new ExcelBeanReader<>(readerConfiguration, AntiToIncomeRow.class);
        ExcelBean<AntiToIncomeRow> excelBean = new ExcelBean<>(excelBeanReader);
        List<AntiToIncomeRow> antiToIncomeRows = new ArrayList<>();
        excelBean.forEach(antiToIncomeRows::add);

        printLog("Antes de obtener cache");
        Cache cache = this.createCache(antiToIncomeRows, employeeDto, request);
        printLog("Antes de obtener cache");

        paymentAntiValidatorFactory.createValidators(cache);

        boolean result = paymentAntiValidatorFactory.validate(antiToIncomeRows);
        if(result){
            antiToIncomeRows.forEach(row -> {
                cachingPaymentImport(row);
                totalProcessRow++;
            });

        }
        printLog("End readExcel process");
    }

    public void createAttachment(PaymentImportDetailRequest request) {
        UUID paymentsId = this.getPaymentIdStoreFromCache(request.getImportProcessId());
        if (Objects.nonNull(paymentsId) && Objects.nonNull(request.getAttachment())) {
            this.sentToCreateAttachment(paymentsId, request);
        }
    }

    private void sentToCreateAttachment(UUID paymentsId, PaymentImportDetailRequest request) {
        CreateAttachmentEvent createAttachmentEvent = new CreateAttachmentEvent(this, paymentsId, request.getAttachment(),
                UUID.fromString(request.getEmployeeId()),
                request.getAttachmentFileName(),
                String.valueOf(request.getFile().length));
        applicationEventPublisher.publishEvent(createAttachmentEvent);
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
        printLog("Start readPaymentCacheAndSave process");
        PaymentImportDetailRequest request = (PaymentImportDetailRequest) rawRequest;
        List<PaymentImportCache> paymentImportCacheList = paymentImportCacheRepository.findAllByImportProcessId(request.getImportProcessId());

        if (Objects.nonNull(paymentImportCacheList) && !paymentImportCacheList.isEmpty()) {
            Map<Long, PaymentDetailDto> depositPaymentDetailMap = this.getPaymentDetailMap(paymentImportCacheList);
            List<PaymentDetailDto> paymentDetailDtoList = new ArrayList<>(depositPaymentDetailMap.values());

            ManageEmployeeDto employeeDto = this.manageEmployeeService.findById(UUID.fromString(request.getEmployeeId()));
            ManagePaymentTransactionTypeDto attachmentTransactionTypeDto = this.transactionTypeService.findById(UUID.fromString(request.getInvoiceTransactionTypeId()));
            ManageInvoiceTypeDto incomeInvoiceType = this.invoiceTypeService.findByCode(EInvoiceType.INCOME.getCode());
            ManageInvoiceStatusDto sendInvoiceStatus = this.statusService.findByCode(EInvoiceStatus.SENT.getCode());
            String employeeName = this.getEmployeeName(employeeDto);
            String attachment = "";
            Map<UUID, PaymentCloseOperationDto> closeOperationDtoMap = this.getCloseOperationMap(paymentDetailDtoList);

            Map<PaymentImportCache, DetailAndIncomeHelper> detailAndIncomeHelperMap = new HashMap<>();
            try {
                LinkedHashMap<String, String> response = paymentUploadAttachmentUtil.uploadAttachmentContent("detail.pdf", request.getAttachment());
                attachment = response.get("url");
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<CreateAntiToIncomeRequest> createAntiToIncomeRequestList = new ArrayList<>();

            for(PaymentImportCache paymentImportCache : paymentImportCacheList){
                PaymentDetailDto depositPaymentDetail = depositPaymentDetailMap.get(Long.parseLong(paymentImportCache.getTransactionId()));
                UUID newIncomeId = UUID.randomUUID();
                detailAndIncomeHelperMap.put(paymentImportCache, new DetailAndIncomeHelper(depositPaymentDetail.getPaymentDetailId(), newIncomeId));

                double amount = Double.parseDouble(paymentImportCache.getPaymentAmount());
                if (depositPaymentDetail.getApplyDepositValue() - amount < 0) {
                    this.totalProcessRow--;
                    return;
                }
                String remark = this.getRemark(paymentImportCache, attachmentTransactionTypeDto);
                PaymentCloseOperationDto closeOperationDto = this.getCloseOperationFromMap(depositPaymentDetail.getPayment().getHotel().getId(), closeOperationDtoMap);
                OffsetDateTime transationDate = this.getTransactionDate(closeOperationDto);

                CreateAntiToIncomeRequest antiToIncomeRequest = this.createAntiToIncomeRequest(newIncomeId, transationDate.toLocalDateTime(),
                        depositPaymentDetail,
                        incomeInvoiceType,
                        sendInvoiceStatus,
                        amount,
                        employeeName,
                        attachment,
                        employeeDto,
                        attachmentTransactionTypeDto,
                        remark);
                createAntiToIncomeRequestList.add(antiToIncomeRequest);

            }

            CreateAntiToIncomeFromPaymentRequest createAntiToIncomeFromPaymentRequest = new CreateAntiToIncomeFromPaymentRequest();
            createAntiToIncomeFromPaymentRequest.setCreateIncomeRequests(createAntiToIncomeRequestList);
            CreateAntiToIncomeFromPaymentMessage incomeCreationResponse = this.createIncomeHttpService.sendCreateIncomeRequest(createAntiToIncomeFromPaymentRequest);

            if(Objects.nonNull(incomeCreationResponse) && Objects.nonNull(incomeCreationResponse.getIncomes()) && !incomeCreationResponse.getIncomes().isEmpty()){
                List<ManageInvoiceDto> incomeList = this.processNewIncomes(incomeCreationResponse.getIncomes());

                Map<UUID, ManageInvoiceDto> incomeMap = incomeList.stream().
                        collect(Collectors.toMap(ManageInvoiceDto::getId, manageInvoiceDto -> manageInvoiceDto));

                List<PaymentDetailDto> applyDepositPaymentDetailList = new ArrayList<>();
                List<PaymentStatusHistoryDto> paymentStatusHistoryDtoList = new ArrayList<>();

                this.applyPayment(paymentImportCacheList,
                        detailAndIncomeHelperMap,
                        depositPaymentDetailMap,
                        incomeMap,
                        employeeDto,
                        closeOperationDtoMap,
                        applyDepositPaymentDetailList,
                        paymentStatusHistoryDtoList);

                this.manageInvoiceService.createAll(incomeList);
                this.paymentDetailService.bulk(applyDepositPaymentDetailList);
                List<PaymentDto> payments = applyDepositPaymentDetailList.stream().map(PaymentDetailDto::getPayment).toList();
                this.paymentDetailService.bulk(paymentDetailDtoList);
                this.paymentService.createBulk(payments);
                this.paymentStatusHistoryService.createAll(paymentStatusHistoryDtoList);

                List<ManageBookingDto> bookings = incomeList.stream().flatMap(invoiceDto -> invoiceDto.getBookings().stream()).toList();
                List<ReplicateBookingBalanceHelper> replicateBookingBalanceHelpers = ReplicateBookingBalanceHelper.from(bookings, false);
                this.replicateBookingBalanceService.replicateBooking(replicateBookingBalanceHelpers);
            }else{
                throw new RuntimeException("ERROR: incomeCreationResponse is null or empty. ImportProcessId: " + request.getImportProcessId());
            }
        }
        printLog("End readPaymentCacheAndSave process");
    }

    private Map<Long, PaymentDetailDto> getPaymentDetailMap(List<PaymentImportCache> paymentImportCacheList){
        List<Long> paymentDetailIds = paymentImportCacheList.stream()
                .filter(paymentImportCache -> Objects.nonNull(paymentImportCache.getTransactionId()))
                .map(paymentImportCache -> Long.parseLong(paymentImportCache.getTransactionId())).toList();
        return this.getPaymentDetailsByPaymentDetailGenId(paymentDetailIds).stream()
                .collect(Collectors.toMap(PaymentDetailDto::getPaymentDetailId, paymentDetailDto -> paymentDetailDto));
    }

    private Map<UUID, PaymentCloseOperationDto> getCloseOperationMap(List<PaymentDetailDto> paymentDetailList){
        List<UUID> hotelIds = paymentDetailList.stream()
                .map(detail -> detail.getPayment().getHotel().getId())
                .toList();
        return this.paymentCloseOperationService.getMapByHotelId(hotelIds);
    }

    private PaymentCloseOperationDto getCloseOperationFromMap(UUID hotelId, Map<UUID, PaymentCloseOperationDto> closeOperationDtoMap){
        if(closeOperationDtoMap.containsKey(hotelId)){
            return closeOperationDtoMap.get(hotelId);
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.PAYMENT_CLOSE_OPERATION_NOT_FOUND, new ErrorField("HotelId", DomainErrorMessage.PAYMENT_CLOSE_OPERATION_NOT_FOUND.getReasonPhrase())));
    }

    private OffsetDateTime getTransactionDate(PaymentCloseOperationDto closeOperationDto){
        return this.paymentCloseOperationService.getTransactionDate(closeOperationDto);
    }

    private String getEmployeeName(ManageEmployeeDto employeeDto){
        return employeeDto.getFirstName() + " " + employeeDto.getLastName();
    }

    private String getRemark(PaymentImportCache paymentImportCache, ManagePaymentTransactionTypeDto transactionTypeDto){
        return paymentImportCache.getRemarks() == null ? transactionTypeDto.getDefaultRemark() : paymentImportCache.getRemarks();
    }

    private CreateAntiToIncomeRequest createAntiToIncomeRequest(UUID id,
                                                                LocalDateTime transactionDate,
                                                                PaymentDetailDto depositPaymentDetail,
                                                                ManageInvoiceTypeDto incomeInvoiceType,
                                                                ManageInvoiceStatusDto sendInvoiceStatus,
                                                                Double incomeAmount,
                                                                String employeeName,
                                                                String attachmentFileName,
                                                                ManageEmployeeDto employeeDto,
                                                                ManagePaymentTransactionTypeDto paymentTransactionTypeDto,
                                                                String remark){

        return new CreateAntiToIncomeRequest(
                id,
                transactionDate.format(dateTimeFormatter),
                Boolean.FALSE,
                depositPaymentDetail.getPayment().getAgency().getId(),
                depositPaymentDetail.getPayment().getHotel().getId(),
                incomeInvoiceType.getId(),
                sendInvoiceStatus.getId(),
                incomeAmount,
                "ACTIVE",
                0L,
                transactionDate.format(formatter),
                Boolean.FALSE,
                LocalDate.now().format(formatter),
                employeeDto.getId().toString(),
                List.of(this.createAttachmentRequest(attachmentFileName, employeeName, employeeDto)),
                List.of(this.createAdjustmentRequest(paymentTransactionTypeDto, remark, incomeAmount))

        );
    }

    private CreateAntiToIncomeAdjustmentRequest createAdjustmentRequest(ManagePaymentTransactionTypeDto transactionType,
                                                                        String remark,
                                                                        Double amount) {
        CreateAntiToIncomeAdjustmentRequest adjustmentRequest = new CreateAntiToIncomeAdjustmentRequest();
        adjustmentRequest.setTransactionType(transactionType.getId());
        adjustmentRequest.setDate(LocalDate.now().toString());
        adjustmentRequest.setRemark(remark);
        adjustmentRequest.setAmount(amount);

        return adjustmentRequest;
    }

    private CreateAntiToIncomeAttachmentRequest createAttachmentRequest(String attachmentFileName,
                                                                        String employeeName,
                                                                        ManageEmployeeDto employeeDto){
        CreateAntiToIncomeAttachmentRequest antiToIncomeAttachmentRequest = new CreateAntiToIncomeAttachmentRequest();
        antiToIncomeAttachmentRequest.setFile(attachmentFileName);
        antiToIncomeAttachmentRequest.setEmployee(employeeName);
        antiToIncomeAttachmentRequest.setEmployeeId(employeeDto.getId());

        return antiToIncomeAttachmentRequest;
    }

    private List<ManageInvoiceDto> processNewIncomes(List<InvoiceHttp> invoiceHttps){
        Set<UUID> hotelIdSet = new HashSet<>();
        Set<UUID> agencyIdSet = new HashSet<>();
        for(InvoiceHttp invoiceHttp : invoiceHttps){
            if(invoiceHttp.getHotel() != null) hotelIdSet.add(invoiceHttp.getHotel());
            if(invoiceHttp.getAgency() != null) agencyIdSet.add(invoiceHttp.getAgency());
        }
        Map<UUID, ManageHotelDto> hotelDtoMap = this.getHotelMap(new ArrayList<>(hotelIdSet));
        Map<UUID, ManageAgencyDto> agencyDtoMap = this.getAgencyMap(new ArrayList<>(agencyIdSet));

        return invoiceHttps.stream()
                .map(invoiceHttp -> this.createInvoice(invoiceHttp, hotelDtoMap, agencyDtoMap))
                .collect(Collectors.toList());
    }

    private Map<UUID, ManageHotelDto>  getHotelMap(List<UUID> ids){
        return this.manageHotelService.getMapById(ids);
    }

    private Map<UUID, ManageAgencyDto>  getAgencyMap(List<UUID> ids){
        return this.manageAgencyService.getMapById(ids);
    }

    private ManageInvoiceDto createInvoice(InvoiceHttp invoiceHttp, Map<UUID, ManageHotelDto> hotelMap, Map<UUID, ManageAgencyDto> agencyMap){
        return new ManageInvoiceDto(invoiceHttp.getId(),
                invoiceHttp.getInvoiceId(),
                invoiceHttp.getInvoiceNo(),
                invoiceHttp.getInvoiceNumber(),
                EInvoiceType.fromName(invoiceHttp.getInvoiceType()),
                invoiceHttp.getInvoiceAmount(),
                invoiceHttp.getBookings().stream().map(this::createBooking).collect(Collectors.toList()),
                invoiceHttp.getHasAttachment(),
                null,
                LocalDate.parse(invoiceHttp.getInvoiceDate(), formatter).atStartOfDay(),
                this.getHotelFromMap(invoiceHttp.getHotel(), hotelMap),
                this.getAgencyFromMap(invoiceHttp.getAgency(), agencyMap),
                invoiceHttp.getAutoRec()
        );
    }

    private ManageBookingDto createBooking(BookingHttp bookingHttp){
        return new ManageBookingDto(
                bookingHttp.getId(),
                bookingHttp.getBookingId(),
                bookingHttp.getReservationNumber(),
                LocalDate.parse(bookingHttp.getCheckIn(), formatter).atStartOfDay(),
                LocalDate.parse(bookingHttp.getCheckOut(), formatter).atStartOfDay(),
                bookingHttp.getFullName(),
                bookingHttp.getFirstName(),
                bookingHttp.getLastName(),
                bookingHttp.getInvoiceAmount(),
                bookingHttp.getAmountBalance(),
                bookingHttp.getCouponNumber(),
                bookingHttp.getAdults(),
                bookingHttp.getChildren(),
                null,
                null,
                LocalDate.parse(bookingHttp.getBookingDate(), formatter).atStartOfDay()
        );
    }

    private ManageHotelDto getHotelFromMap(UUID hotelId, Map<UUID, ManageHotelDto> hotelMap){
        if(hotelMap.containsKey(hotelId)){
            return hotelMap.get(hotelId);
        }
        return null;
    }

    private ManageAgencyDto getAgencyFromMap(UUID agencyId, Map<UUID, ManageAgencyDto> agencyMap){
        if(agencyMap.containsKey(agencyId)){
            return agencyMap.get(agencyId);
        }
        return null;
    }

    public void applyPayment(List<PaymentImportCache> paymentImportCacheList,
                             Map<PaymentImportCache, DetailAndIncomeHelper> detailAndIncomeHelperMap,
                             Map<Long, PaymentDetailDto> depositPaymentDetailMap,
                             Map<UUID, ManageInvoiceDto> incomeMap,
                             ManageEmployeeDto employeeDto,
                             Map<UUID, PaymentCloseOperationDto> closeOperationDtoMap,
                             List<PaymentDetailDto> applyPaymentDetailList,
                             List<PaymentStatusHistoryDto> paymentStatusHistoryDtoList) {
        ManagePaymentTransactionTypeDto applyDepositPaymenttransactionTypeDto = this.transactionTypeService.findByApplyDepositAndDefaults();
        ManagePaymentStatusDto appliedPaymentStatus = this.paymentStatusService.findByApplied();

        for(PaymentImportCache paymentImportCache : paymentImportCacheList){
            DetailAndIncomeHelper detailAndIncomeHelper = detailAndIncomeHelperMap.get(paymentImportCache);
            PaymentDetailDto depositPaymenDetail = depositPaymentDetailMap.get(detailAndIncomeHelper.getPaymentDetailId());
            ManageInvoiceDto income = incomeMap.get(detailAndIncomeHelper.getIncomeId());
            ManageBookingDto bookingDto = income.getBookings().get(0);
            PaymentDto paymentDto = depositPaymenDetail.getPayment();
            PaymentCloseOperationDto closeOperationDto = this.getCloseOperationFromMap(paymentDto.getHotel().getId(), closeOperationDtoMap);
            OffsetDateTime transactionDate = this.getTransactionDate(closeOperationDto);
            Double amount = Double.parseDouble(paymentImportCache.getPaymentAmount());

            ProcessCreatePaymentDetail processCreatePaymentDetail = new ProcessCreatePaymentDetail(
                    paymentDto,
                    amount,
                    transactionDate,
                    employeeDto,
                    paymentImportCache.getRemarks(),
                    applyDepositPaymenttransactionTypeDto,
                    appliedPaymentStatus,
                    depositPaymenDetail
            );
            processCreatePaymentDetail.process();
            PaymentDetailDto applyDepositPaymentDetail = processCreatePaymentDetail.getDetail();
            applyPaymentDetailList.add(applyDepositPaymentDetail);

            if(processCreatePaymentDetail.isPaymentApplied()){
                PaymentStatusHistoryDto paymentStatusHistoryDto = processCreatePaymentDetail.getPaymentStatusHistory();
                paymentStatusHistoryDtoList.add(paymentStatusHistoryDto);
            }

            ProcessApplyPaymentDetail processApplyPaymentDetail = new ProcessApplyPaymentDetail(
                    paymentDto,
                    applyDepositPaymentDetail,
                    bookingDto,
                    transactionDate,
                    amount
            );
            processApplyPaymentDetail.process();
        }
    }

    @Override
    public PaginatedResponse getPaymentImportErrors(String importProcessId, Pageable pageable) {
        Page<PaymentAntiRowError> page = antiErrorRepository.findAllByImportProcessId(importProcessId, pageable);
        return new PaginatedResponse(
                page.getContent().stream().sorted(Comparator.comparingInt(PaymentAntiRowError::getRowNumber)).collect(Collectors.toList()),
                page.getTotalPages(),
                page.getNumberOfElements(),
                page.getTotalElements(),
                page.getSize(),
                page.getNumber()
        );
    }

    public UUID getPaymentIdStoreFromCache(String importProcessId) {
        Pageable pageable = PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, "id"));
        Page<PaymentImportCache> cacheList = paymentImportCacheRepository.findAllByImportProcessId(importProcessId, pageable);
        Optional<PaymentImportCache> paymentImportCache = cacheList.stream().findFirst();
        return paymentImportCache.map(importCache -> {
//            PaymentDetailDto paymentDetailDto = paymentDetailService.findByGenId(Integer.parseInt(importCache.getTransactionId()));
//            return paymentDetailDto.getPayment().getId();
            PaymentDetailSimpleDto paymentDetailDto = paymentDetailService.findSimpleDetailByGenId(Integer.parseInt(importCache.getTransactionId()));
            return paymentDetailDto.getPaymentId();
        }).orElse(null);

    }

    private void printLog(String message){
        logger.info("{} at: {}", message, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    private Cache createCache(List<AntiToIncomeRow> antiToIncomeRows, ManageEmployeeDto employeeDto, PaymentImportDetailRequest request){
        Set<Long> paymentDetailGenIdSet = new HashSet<>();

        for (AntiToIncomeRow row : antiToIncomeRows){
            paymentDetailGenIdSet.add(row.getTransactionId().longValue());

            row.setImportProcessId(request.getImportProcessId());
            row.setImportType(request.getImportPaymentType().name());
        }

        List<Long> paymentDetailGenIdList = new ArrayList<>(paymentDetailGenIdSet);
        List<PaymentDetailDto> paymentDetails = this.getPaymentDetailsByPaymentDetailGenId(paymentDetailGenIdList);

        return new Cache(paymentDetails, employeeDto);
    }

    private List<PaymentDetailDto> getPaymentDetailsByPaymentDetailGenId(List<Long> paymentDetailGenIds){
        return this.paymentDetailService.findByPaymentDetailsIdIn(paymentDetailGenIds).stream().map(PaymentDetail::toAggregate).collect(Collectors.toList());
    }


}
