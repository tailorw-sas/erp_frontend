package com.kynsoft.finamer.payment.infrastructure.services.helpers;

import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsof.share.core.infrastructure.specifications.LogicalOperation;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.kynsoft.finamer.payment.application.command.paymentDetailApplyDeposit.create.CreatePaymentDetailApplyDepositCommand;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailRequest;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManagePaymentTransactionTypeResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentAntiRowError;
import com.kynsoft.finamer.payment.domain.services.AbstractPaymentImportHelperService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.infrastructure.excel.PaymentCacheFactory;
import com.kynsoft.finamer.payment.infrastructure.excel.event.applyDeposit.ApplyDepositEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.event.createAttachment.CreateAttachmentEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.anti.PaymentAntiValidatorFactory;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportErrorRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportAntiErrorRepository;
import io.jsonwebtoken.lang.Assert;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class PaymentImportAntiIncomeHelperServiceImpl extends AbstractPaymentImportHelperService {

    private final PaymentImportCacheRepository paymentImportCacheRepository;
    private final PaymentAntiValidatorFactory paymentAntiValidatorFactory;
    private final IPaymentDetailService paymentDetailService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final IManagePaymentTransactionTypeService transactionTypeService;

    private final PaymentImportAntiErrorRepository antiErrorRepository;
    private final PaymentImportErrorRepository paymentImportErrorRepository;


    public PaymentImportAntiIncomeHelperServiceImpl(PaymentImportCacheRepository paymentImportCacheRepository,
                                                    PaymentAntiValidatorFactory paymentAntiValidatorFactory,
                                                    RedisTemplate<String, String> redisTemplate,
                                                    IPaymentDetailService paymentDetailService,
                                                    ApplicationEventPublisher applicationEventPublisher,
                                                    IManagePaymentTransactionTypeService transactionTypeService,
                                                    PaymentImportAntiErrorRepository antiErrorRepository,
                                                    PaymentImportErrorRepository paymentImportErrorRepository) {
        super(redisTemplate);
        this.paymentImportCacheRepository = paymentImportCacheRepository;
        this.paymentAntiValidatorFactory = paymentAntiValidatorFactory;
        this.paymentDetailService = paymentDetailService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.transactionTypeService = transactionTypeService;
        this.antiErrorRepository = antiErrorRepository;
        this.paymentImportErrorRepository = paymentImportErrorRepository;
    }

    @Override
    public void readExcel(ReaderConfiguration readerConfiguration, Object rawRequest) {
        PaymentImportDetailRequest request = (PaymentImportDetailRequest) rawRequest;
        paymentAntiValidatorFactory.createValidators();
        ExcelBeanReader<AntiToIncomeRow> excelBeanReader = new ExcelBeanReader<>(readerConfiguration, AntiToIncomeRow.class);
        ExcelBean<AntiToIncomeRow> excelBean = new ExcelBean<>(excelBeanReader);
        for (AntiToIncomeRow row : excelBean) {
            row.setImportProcessId(request.getImportProcessId());
            row.setImportType(request.getImportPaymentType().name());
            if (paymentAntiValidatorFactory.validate(row)) {
                cachingPaymentImport(row);
            }
        }
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
        PaymentImportDetailRequest request = (PaymentImportDetailRequest) rawRequest;
        Pageable pageable = PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, "id"));
        Page<PaymentImportCache> cacheList;
        if (!paymentImportErrorRepository.existsPaymentImportErrorByImportProcessId(request.getImportProcessId())) {
            do {
                cacheList = paymentImportCacheRepository.findAllByImportProcessId(request.getImportProcessId(), pageable);
                cacheList.forEach(paymentImportCache -> {
                    PaymentDetailDto parentPaymentDetail = paymentDetailService.findByGenId(Integer.parseInt(paymentImportCache.getTransactionId()));
                    this.sendToCreateApplyDeposit(parentPaymentDetail.getId(),
                            Double.parseDouble(paymentImportCache.getPaymentAmount()),
                            UUID.fromString(request.getEmployeeId()),
                            getDefaultApplyDepositTransactionTypeId(),
                            UUID.fromString(request.getInvoiceTransactionTypeId()),
                            paymentImportCache.getRemarks()
                    );
                });
                pageable = pageable.next();
            } while (cacheList.hasNext());
        }
    }

    @Override
    public PaginatedResponse getPaymentImportErrors(String importProcessId, Pageable pageable) {
        Page<PaymentAntiRowError> page = antiErrorRepository.findAllByImportProcessId(importProcessId, pageable);
        return new PaginatedResponse(page.getContent(), page.getTotalPages(), page.getNumberOfElements(), page.getTotalElements(), page.getSize(), page.getNumber());
    }

    public UUID getPaymentIdStoreFromCache(String importProcessId) {
        Pageable pageable = PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, "id"));
        Page<PaymentImportCache> cacheList = paymentImportCacheRepository.findAllByImportProcessId(importProcessId, pageable);
        Optional<PaymentImportCache> paymentImportCache = cacheList.stream().findFirst();
        return paymentImportCache.map(importCache -> {
            PaymentDetailDto paymentDetailDto = paymentDetailService.findByGenId(Integer.parseInt(importCache.getTransactionId()));
            return paymentDetailDto.getPayment().getId();
        }).orElse(null);

    }

    private void sendToCreateApplyDeposit(UUID paymentDetail, double amount, UUID employee, UUID transactionType,
                                          UUID transactionTypeIdForAdjustment, String remarks) {
        CreatePaymentDetailApplyDepositCommand createPaymentDetailApplyDepositCommand =
                new CreatePaymentDetailApplyDepositCommand(Status.ACTIVE, paymentDetail, transactionType, amount, remarks, employee, transactionTypeIdForAdjustment);
        ApplyDepositEvent applyDepositEvent = new ApplyDepositEvent(createPaymentDetailApplyDepositCommand);
        applicationEventPublisher.publishEvent(applyDepositEvent);

    }


    private UUID getDefaultApplyDepositTransactionTypeId() {
        FilterCriteria markAsDefault = new FilterCriteria();
        markAsDefault.setKey("defaults");
        markAsDefault.setValue(true);
        markAsDefault.setOperator(SearchOperation.EQUALS);
        markAsDefault.setLogicalOperation(LogicalOperation.AND);

        FilterCriteria applyDeposit = new FilterCriteria();
        applyDeposit.setKey("applyDeposit");
        applyDeposit.setValue(true);
        applyDeposit.setOperator(SearchOperation.EQUALS);
        applyDeposit.setLogicalOperation(LogicalOperation.AND);

        FilterCriteria statusActive = new FilterCriteria();
        statusActive.setKey("status");
        statusActive.setValue(Status.ACTIVE);
        statusActive.setOperator(SearchOperation.EQUALS);
        PaginatedResponse response = transactionTypeService.search(Pageable.unpaged(), List.of(markAsDefault, applyDeposit, statusActive));
        Assert.notEmpty(response.getData(), "There is not  default apply deposit transaction type");
        ManagePaymentTransactionTypeResponse response1 = (ManagePaymentTransactionTypeResponse) response.getData().get(0);
        return response1.getId();
    }


}
