package com.kynsoft.finamer.payment.infrastructure.services.helpers;

import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.domain.service.IStorageService;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsof.share.core.infrastructure.specifications.LogicalOperation;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.kynsof.share.utils.ServiceLocator;
import com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.create.CreateMasterPaymentAttachmentCommand;
import com.kynsoft.finamer.payment.application.command.payment.create.CreatePaymentCommand;
import com.kynsoft.finamer.payment.application.command.payment.create.CreatePaymentMessage;
import com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment.ApplyPaymentDetailCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.create.CreatePaymentDetailCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.create.CreatePaymentDetailMessage;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createFormImport.CreatePaymentDetailFromFileCommand;
import com.kynsoft.finamer.payment.application.command.paymentImport.payment.PaymentImportRequest;
import com.kynsoft.finamer.payment.application.query.attachmentType.search.GetSearchAttachmentTypeQuery;
import com.kynsoft.finamer.payment.application.query.manageResourceType.search.GetSearchManageResourceTypeQuery;
import com.kynsoft.finamer.payment.application.query.objectResponse.AttachmentTypeResponse;
import com.kynsoft.finamer.payment.application.query.objectResponse.ResourceTypeResponse;
import com.kynsoft.finamer.payment.domain.dto.*;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.excel.PaymentExpenseBookingImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentExpenseBookingRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentExpenseBookingRowError;
import com.kynsoft.finamer.payment.domain.services.*;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.expenseBooking.PaymentExpenseBookingValidatorFactory;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportExpenseBookingErrorRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.expenseBooking.PaymentExpenseBookingImportCacheRepository;
import com.kynsoft.finamer.payment.infrastructure.utils.PaymentUploadAttachmentUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentImportExpenseBookingHelperServiceImpl extends AbstractPaymentImportHelperService {

    private final PaymentExpenseBookingImportCacheRepository cacheRepository;
    private final PaymentImportExpenseBookingErrorRepository errorRepository;
    private final PaymentExpenseBookingValidatorFactory expenseBookingValidatorFactory;
    private final IManageBookingService bookingService;
    private List<String> availableClient;
    private final IStorageService fileSystemService;
    private final PaymentUploadAttachmentUtil paymentUploadAttachmentUtil;
    private final IPaymentCloseOperationService closeOperationService;
    private final IManagePaymentSourceService paymentSourceService;
    private final IManagePaymentStatusService paymentStatusService;
    private final IManagePaymentTransactionTypeService transactionTypeService;
    private final IManagePaymentAttachmentStatusService paymentAttachmentStatusService;
    private final IManageAttachmentTypeService attachmentTypeService;
    private final IManageResourceTypeService resourceTypeService;
    private final IStorageService storageService;

    private final IPaymentDetailService paymentDetailService;
    private final ServiceLocator<IMediator> serviceLocator;

    @Value("${payment.source.expense.code}")
    private String PAYMENT_SOURCE_EXP_CODE;
    @Value("${payment.status.confirm.code}")
    private String PAYMENT_STATUS_CONFIRM_CODE;
    @Value("${payment.attachment.status.code}")
    private String PAYMENT_ATTACHMENT_STATUS_CODE;
    @Value("${payment.resource.type.code}")
    private String PAYMENT_EXPENSE_BOOKING_RESOURCE_TYPE;
    @Value("${payment.attachment.type.code}")
    private String PAYMENT_EXPENSE_BOOKING_ATTACHMENT_TYPE;


    public PaymentImportExpenseBookingHelperServiceImpl(PaymentExpenseBookingImportCacheRepository cacheRepository,
                                                        PaymentImportExpenseBookingErrorRepository errorRepository,
                                                        RedisTemplate<String, String> redisTemplate,
                                                        PaymentExpenseBookingValidatorFactory expenseBookingValidatorFactory,
                                                        IManageBookingService bookingService, IStorageService fileSystemService,
                                                        PaymentUploadAttachmentUtil paymentUploadAttachmentUtil,
                                                        IPaymentCloseOperationService closeOperationService,
                                                        IManagePaymentSourceService paymentSourceService,
                                                        IManagePaymentStatusService paymentStatusService,
                                                        IManagePaymentTransactionTypeService transactionTypeService,
                                                        IManagePaymentAttachmentStatusService paymentAttachmentStatusService,
                                                        IManageAttachmentTypeService attachmentTypeService,
                                                        IManageResourceTypeService resourceTypeService,
                                                        IStorageService storageService, IPaymentDetailService paymentDetailService,
                                                        ServiceLocator<IMediator> serviceLocator
    ) {
        super(redisTemplate);
        this.cacheRepository = cacheRepository;
        this.errorRepository = errorRepository;
        this.expenseBookingValidatorFactory = expenseBookingValidatorFactory;
        this.bookingService = bookingService;
        this.fileSystemService = fileSystemService;
        this.paymentUploadAttachmentUtil = paymentUploadAttachmentUtil;
        this.closeOperationService = closeOperationService;
        this.paymentSourceService = paymentSourceService;
        this.paymentStatusService = paymentStatusService;
        this.transactionTypeService = transactionTypeService;
        this.paymentAttachmentStatusService = paymentAttachmentStatusService;
        this.attachmentTypeService = attachmentTypeService;
        this.resourceTypeService = resourceTypeService;
        this.storageService = storageService;
        this.paymentDetailService = paymentDetailService;
        this.serviceLocator = serviceLocator;
        this.availableClient = new ArrayList<>();
    }

    public void readExcel(ReaderConfiguration readerConfiguration, Object rawRequest) {
        this.totalProcessRow=0;
        availableClient.clear();
        readerConfiguration.setStrictHeaderOrder(false);
        PaymentImportRequest request = (PaymentImportRequest) rawRequest;
        expenseBookingValidatorFactory.createValidators();
        ExcelBeanReader<PaymentExpenseBookingRow> excelBeanReader = new ExcelBeanReader<>(readerConfiguration, PaymentExpenseBookingRow.class);
        ExcelBean<PaymentExpenseBookingRow> excelBean = new ExcelBean<>(excelBeanReader);
        for (PaymentExpenseBookingRow row : excelBean) {
            row.setImportProcessId(request.getImportProcessId());
            row.setImportType(request.getImportPaymentType().name());
            row.setHotelId(request.getHotelId().toString());
            if (expenseBookingValidatorFactory.validate(row)) {
                row.setClientName(getClientName(row.getBookingId()));
                availableClient.add(row.getClientName());
                cachingPaymentImport(row);
                this.totalProcessRow++;
            }
        }
    }

    @Override
    public void cachingPaymentImport(Row paymentRow) {
        PaymentExpenseBookingImportCache paymentImportCache = new PaymentExpenseBookingImportCache((PaymentExpenseBookingRow) paymentRow);
        cacheRepository.save(paymentImportCache);
    }

    @Override
    public void clearPaymentImportCache(String importProcessId) {
        Pageable pageable = PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, "id"));
        Page<PaymentExpenseBookingImportCache> cacheList;
        do {
            cacheList = cacheRepository.findAllByImportProcessId(importProcessId, pageable);
            cacheRepository.deleteAll(cacheList.getContent());
            pageable = pageable.next();
        } while (cacheList.hasNext());
        storageService.deleteAll(importProcessId);
    }

    @Override
    public void readPaymentCacheAndSave(Object rawRequest) {
        PaymentImportRequest request = (PaymentImportRequest) rawRequest;
        Map<String, List<PaymentExpenseBookingImportCache>> grouped = this.groupCacheByClient(request.getImportProcessId());
        for (Map.Entry<String, List<PaymentExpenseBookingImportCache>> entry : grouped.entrySet()) {
            PaymentExpenseBookingImportCache sampleCache = entry.getValue().get(0);
            List<ManageBookingDto> bookingDtos = entry.getValue().stream().map(cache -> bookingService.findByGenId(Long.parseLong(cache.getBookingId()))).toList();
            double paymentAmount = entry.getValue().stream().mapToDouble(PaymentExpenseBookingImportCache::getBalance).sum();
            ManageBookingDto manageBooking = bookingDtos.get(0);
            ManagePaymentTransactionTypeDto paymentTransactionType = transactionTypeService.findByCode(sampleCache.getTransactionType());
            String remarks = Objects.isNull(sampleCache.getRemarks()) || sampleCache.getRemarks().isEmpty() ? paymentTransactionType.getDefaultRemark() : sampleCache.getRemarks();
            UUID paymentId = createPayment(request.getHotelId(), request.getEmployeeId(), manageBooking.getInvoice().getAgency(), paymentAmount);
            createAttachment(request.getImportProcessId(), request.getEmployeeId(), paymentId);
            createPaymentDetails(bookingDtos,paymentTransactionType.getId(),request.getEmployeeId(),paymentId,sampleCache.getBalance(),remarks);

        }

    }
    private String getClientName(String bookingId) {
        ManageInvoiceDto manageInvoiceDto = bookingService.findByGenId(Long.parseLong(bookingId)).getInvoice();
        return manageInvoiceDto.getAgency().getClient().getName();
    }

    private Map<String, List<PaymentExpenseBookingImportCache>> groupCacheByClient(String importProcessId) {
        Pageable pageable = PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, "id"));
        Map<String, List<PaymentExpenseBookingImportCache>> group = new HashMap<>();
        Page<PaymentExpenseBookingImportCache> elements;
        for (String clientName : availableClient) {
            do {
                elements = cacheRepository.findAllByImportProcessIdAndClientName(importProcessId, clientName, pageable);
                group.merge(clientName,new ArrayList<>(elements.getContent()), (oldList, newList) -> {
                    List<PaymentExpenseBookingImportCache> combinedList = new ArrayList<>(oldList); // Creamos una nueva lista mutable
                    combinedList.addAll(newList);
                    return combinedList;
                });
                pageable = pageable.next();
            } while (elements.hasNext());
        }
        return group;
    }

    @Override
    public PaginatedResponse getPaymentImportErrors(String importProcessId, Pageable pageable) {
        Page<PaymentExpenseBookingRowError> page = errorRepository.findAllByImportProcessId(importProcessId, pageable);
        return new PaginatedResponse(page.getContent(), page.getTotalPages(), page.getNumberOfElements(),
                page.getTotalElements(), page.getSize(), page.getNumber());
    }

    private UUID createPayment(UUID hotelId, UUID employeeId, ManageAgencyDto agencyDto, double amount) {

        ManagePaymentSourceDto paymentSource = paymentSourceService.findByCodeActive(PAYMENT_SOURCE_EXP_CODE);
        ManagePaymentStatusDto paymentStatus = paymentStatusService.findByCode(PAYMENT_STATUS_CONFIRM_CODE);
        ManagePaymentAttachmentStatusDto attachmentStatusDto = paymentAttachmentStatusService.findByCode(PAYMENT_ATTACHMENT_STATUS_CODE);
        CreatePaymentCommand createPaymentCommand = new CreatePaymentCommand(Status.ACTIVE, paymentSource.getId(), "",
                getTransactionDate(hotelId), paymentStatus.getId(),
                agencyDto.getClient().getId(), agencyDto.getId(), hotelId, null, attachmentStatusDto.getId(), amount,
                "Expense generated from action of import files", null, employeeId,true);
        CreatePaymentMessage createPaymentMessage = serviceLocator.getBean(IMediator.class).send(createPaymentCommand);
        return createPaymentMessage.getPayment().getId();
    }

    private void createPaymentDetails(List<ManageBookingDto> manageBookingDtos, UUID transactionType, UUID employeeId, UUID paymentId, double amount, String remarks) {
        for (ManageBookingDto manageBookingDto : manageBookingDtos) {
            CreatePaymentDetailFromFileCommand createPaymentDetailCommand =
                    new CreatePaymentDetailFromFileCommand(Status.ACTIVE, paymentId, transactionType,
                            amount, remarks, employeeId, manageBookingDto.getId(), true, serviceLocator.getBean(IMediator.class));
           serviceLocator.getBean(IMediator.class).send(createPaymentDetailCommand);
        }
    }

    private void createAttachment(String importProcessId, UUID employeeId, UUID paymentId) {
        try {
            Map<String, File> attachmentFields = fileSystemService.loadAll(importProcessId)
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .collect(Collectors.toMap(File::getName, file -> file));

            ResourceTypeDto resourceTypeDto = resourceTypeService.findByCode(PAYMENT_EXPENSE_BOOKING_RESOURCE_TYPE);
            AttachmentTypeDto attachmentTypeDto = attachmentTypeService.findByCode(PAYMENT_EXPENSE_BOOKING_ATTACHMENT_TYPE);

            for (Map.Entry<String, File> attachment : attachmentFields.entrySet()) {
               try(FileInputStream fileInputStream = new FileInputStream(attachment.getValue())) {
                   byte[] fileContent = fileInputStream.readAllBytes();
                   LinkedHashMap<String, String> response = paymentUploadAttachmentUtil.uploadAttachmentContent(attachment.getKey(), fileContent);

                   CreateMasterPaymentAttachmentCommand createMasterPaymentAttachmentCommand =
                           new CreateMasterPaymentAttachmentCommand(Status.ACTIVE, employeeId,
                                   paymentId
                                   , resourceTypeDto.getId(), attachmentTypeDto.getId(),
                                   attachment.getKey(), response.get("url") ,
                                   "Attachment added automatically when the payment was imported",
                                   String.valueOf(fileContent.length));
                   serviceLocator.getBean(IMediator.class).send(createMasterPaymentAttachmentCommand);
               }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private LocalDate getTransactionDate(UUID hotelId){
        PaymentCloseOperationDto closeOperationDto = closeOperationService.findByHotelIds(hotelId);
        LocalDate firstDateOgMonth = LocalDate.now().withDayOfMonth(1);
        if (closeOperationDto.getEndDate().isBefore(firstDateOgMonth)){
            return closeOperationDto.getEndDate();
        }
            return LocalDate.now();
    }
}
