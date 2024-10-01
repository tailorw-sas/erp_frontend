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
import com.kynsoft.finamer.payment.application.command.paymentDetail.create.CreatePaymentDetailCommand;
import com.kynsoft.finamer.payment.application.command.paymentImport.payment.PaymentImportRequest;
import com.kynsoft.finamer.payment.application.query.attachmentType.search.GetSearchAttachmentTypeQuery;
import com.kynsoft.finamer.payment.application.query.manageResourceType.search.GetSearchManageResourceTypeQuery;
import com.kynsoft.finamer.payment.application.query.objectResponse.AttachmentTypeResponse;
import com.kynsoft.finamer.payment.application.query.objectResponse.ResourceTypeResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentStatusDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.excel.PaymentExpenseBookingImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentExpenseBookingRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentExpenseBookingRowError;
import com.kynsoft.finamer.payment.domain.services.AbstractPaymentImportHelperService;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentSourceService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentImportExpenseBookingHelperServiceImpl extends AbstractPaymentImportHelperService {

    private final PaymentExpenseBookingImportCacheRepository cacheRepository;
    private final PaymentImportExpenseBookingErrorRepository errorRepository;
    private final PaymentExpenseBookingValidatorFactory expenseBookingValidatorFactory;
    private final IManageBookingService bookingService;
    private List<String> availableClient;
    private final StorageService fileSystemService;
    private final PaymentUploadAttachmentUtil paymentUploadAttachmentUtil;
    private final IPaymentCloseOperationService closeOperationService;
    private final IManagePaymentSourceService paymentSourceService;
    private final IManagePaymentStatusService paymentStatusService;
    private final IManagePaymentTransactionTypeService transactionTypeService;
    private final ServiceLocator<IMediator> serviceLocator;

    @Value("${payment.source.expense.code}")
    private String PAYMENT_SOURCE_EXP_CODE;
    @Value("${payment.status.confirm.code}")
    private String PAYMENT_STATUS_CONF_CODE;


    public PaymentImportExpenseBookingHelperServiceImpl(PaymentExpenseBookingImportCacheRepository cacheRepository,
                                                        PaymentImportExpenseBookingErrorRepository errorRepository,
                                                        RedisTemplate<String, String> redisTemplate,
                                                        PaymentExpenseBookingValidatorFactory expenseBookingValidatorFactory,
                                                        IManageBookingService bookingService, StorageService fileSystemService,
                                                        PaymentUploadAttachmentUtil paymentUploadAttachmentUtil,
                                                        IPaymentCloseOperationService closeOperationService,
                                                        IManagePaymentSourceService paymentSourceService,
                                                        IManagePaymentStatusService paymentStatusService,
                                                        IManagePaymentTransactionTypeService transactionTypeService,
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
        this.serviceLocator = serviceLocator;
        this.availableClient = new ArrayList<>();
    }

    public void readExcel(ReaderConfiguration readerConfiguration, Object rawRequest) {
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
    }

    @Transactional
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
                group.merge(clientName, elements.getContent(), (oldList, newList) -> {
                    oldList.addAll(newList);
                    return oldList;
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
        PaymentCloseOperationDto closeOperationDto = closeOperationService.findByHotelIds(hotelId);
        ManagePaymentSourceDto paymentSource = paymentSourceService.findByCodeActive(PAYMENT_SOURCE_EXP_CODE);
        ManagePaymentStatusDto paymentStatus = paymentStatusService.findByCode("APL");
        CreatePaymentCommand createPaymentCommand = new CreatePaymentCommand(Status.ACTIVE, paymentSource.getId(), "",
                closeOperationDto.getEndDate(), paymentStatus.getId(),
                agencyDto.getClient().getId(), agencyDto.getId(), hotelId, null, null, amount,
                "Expense generated from action of import files", null, employeeId,true);
        CreatePaymentMessage createPaymentMessage = serviceLocator.getBean(IMediator.class).send(createPaymentCommand);
        return createPaymentMessage.getPayment().getId();
    }

    private void createPaymentDetails(List<ManageBookingDto> manageBookingDtos, UUID transactionType, UUID employeeId, UUID paymentId, double amount, String remarks) {
        for (ManageBookingDto manageBookingDto : manageBookingDtos) {
            CreatePaymentDetailCommand createPaymentDetailCommand =
                    new CreatePaymentDetailCommand(Status.ACTIVE, paymentId, transactionType,
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

            FilterCriteria filterDefault = new FilterCriteria();
            filterDefault.setKey("defaults");
            filterDefault.setValue(true);
            filterDefault.setOperator(SearchOperation.EQUALS);
            filterDefault.setLogicalOperation(LogicalOperation.AND);

            FilterCriteria statusActive = new FilterCriteria();
            statusActive.setKey("status");
            statusActive.setValue(Status.ACTIVE);
            statusActive.setOperator(SearchOperation.EQUALS);

            GetSearchManageResourceTypeQuery resourceTypeQuery = new GetSearchManageResourceTypeQuery(Pageable.unpaged(), List.of(filterDefault,statusActive), "");
            GetSearchAttachmentTypeQuery attachmentTypeQuery = new GetSearchAttachmentTypeQuery(Pageable.unpaged(), List.of(filterDefault,statusActive), "");
            PaginatedResponse resourceType = serviceLocator.getBean(IMediator.class).send(resourceTypeQuery);
            PaginatedResponse attachmentType = serviceLocator.getBean(IMediator.class).send(attachmentTypeQuery);
            ResourceTypeResponse resourceTypeResponse = (ResourceTypeResponse)resourceType.getData().get(0);
            AttachmentTypeResponse attachmentTypeResponse =  (AttachmentTypeResponse)attachmentType.getData().get(0);

            for (Map.Entry<String, File> attachment : attachmentFields.entrySet()) {
               try(FileInputStream fileInputStream = new FileInputStream(attachment.getValue())) {
                   byte[] fileContent = fileInputStream.readAllBytes();

                   //LinkedHashMap<String, String> response = paymentUploadAttachmentUtil.uploadAttachmentContent(attachment.getKey(), fileContent);

                   CreateMasterPaymentAttachmentCommand createMasterPaymentAttachmentCommand =
                           new CreateMasterPaymentAttachmentCommand(Status.ACTIVE, employeeId,
                                   paymentId
                                   , resourceTypeResponse.getId(), attachmentTypeResponse.getId(),
                                   attachment.getKey(), "",
//                                   attachment.getKey(), ""response.get("url"),
                                   "Attachment added automatically when the payment was imported",
                                   String.valueOf(fileContent.length));
                   serviceLocator.getBean(IMediator.class).send(createMasterPaymentAttachmentCommand);
               }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
