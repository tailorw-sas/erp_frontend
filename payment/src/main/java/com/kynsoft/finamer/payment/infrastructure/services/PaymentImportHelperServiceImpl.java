package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsoft.finamer.payment.application.command.paymentImport.payment.PaymentImportRequest;
import com.kynsoft.finamer.payment.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.payment.domain.dto.ManageClientDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentSourceDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentStatusDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EImportPaymentType;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportError;
import com.kynsoft.finamer.payment.domain.excel.bean.PaymentBankRow;
import com.kynsoft.finamer.payment.domain.excel.bean.PaymentRow;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import com.kynsoft.finamer.payment.domain.services.IManageAgencyService;
import com.kynsoft.finamer.payment.domain.services.IManageBankAccountService;
import com.kynsoft.finamer.payment.domain.services.IManageClientService;
import com.kynsoft.finamer.payment.domain.services.IManageHotelService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentSourceService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportHelperService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.excel.PaymentCacheFactory;
import com.kynsoft.finamer.payment.infrastructure.excel.mapper.PaymentBankRowMapper;
import com.kynsoft.finamer.payment.infrastructure.excel.mapper.PaymentRowMapper;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.bank.PaymentBankValidatorFactory;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.expense.PaymentValidatorFactory;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportErrorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentImportHelperServiceImpl implements IPaymentImportHelperService {
    private final PaymentImportCacheRepository paymentImportCacheRepository;
    private final PaymentImportErrorRepository paymentImportErrorRepository;
    private final PaymentValidatorFactory paymentValidatorFactory;
    private final PaymentBankValidatorFactory paymentBankValidatorFactory;
    private final RedisTemplate<String, String> redisTemplate;
    private final IPaymentService paymentService;
    private final IManageAgencyService manageAgencyService;
    private final IManageHotelService manageHotelService;
    private final IManageBankAccountService bankAccountService;
    private final IManagePaymentSourceService paymentSourceService;
    private final IManagePaymentStatusService paymentStatusService;
    private final PaymentRowMapper paymentRowMapper;
    private final PaymentBankRowMapper paymentBankRowMapper;


    @Value("${payment.source.expense.code}")
    private  String PAYMENT_SOURCE_EXP_CODE;
    @Value("${payment.source.bank.code}")
    private  String PAYMENT_SOURCE_BANK_CODE;
    @Value("${payment.status.confirm.code}")
    private  String PAYMENT_STATUS_CONF_CODE;

    public PaymentImportHelperServiceImpl(PaymentImportCacheRepository paymentImportCacheRepository,
                                          PaymentImportErrorRepository paymentImportErrorRepository,
                                          PaymentValidatorFactory paymentValidatorFactory,
                                          PaymentBankValidatorFactory paymentBankValidatorFactory,
                                          RedisTemplate<String, String> redisTemplate,
                                          IPaymentService paymentService,
                                          IManageAgencyService manageAgencyService,
                                          IManageHotelService manageHotelService,
                                          IManageBankAccountService bankAccountService,
                                          IManagePaymentSourceService paymentSourceService,
                                          IManageClientService clientService,
                                          IManagePaymentStatusService paymentStatusService,
                                          PaymentRowMapper paymentRowMapper,
                                          PaymentBankRowMapper paymentBankRowMapper) {
        this.paymentImportCacheRepository = paymentImportCacheRepository;
        this.paymentImportErrorRepository = paymentImportErrorRepository;
        this.paymentValidatorFactory = paymentValidatorFactory;
        this.paymentBankValidatorFactory = paymentBankValidatorFactory;
        this.redisTemplate = redisTemplate;
        this.paymentService = paymentService;
        this.manageAgencyService = manageAgencyService;
        this.manageHotelService = manageHotelService;
        this.bankAccountService = bankAccountService;
        this.paymentSourceService = paymentSourceService;
        this.paymentStatusService = paymentStatusService;
        this.paymentRowMapper = paymentRowMapper;
        this.paymentBankRowMapper = paymentBankRowMapper;
    }

    @Override
    public void readExcelForExpense(ReaderConfiguration readerConfiguration, PaymentImportRequest request) {
        paymentValidatorFactory.createValidators();
        ExcelBeanReader<PaymentRow> excelBeanReader = new ExcelBeanReader<>(readerConfiguration, PaymentRow.class);
        ExcelBean<PaymentRow> excelBean = new ExcelBean<>(excelBeanReader);
        for (PaymentRow row : excelBean) {
            row.setImportProcessId(request.getImportProcessId());
            row.setImportType(request.getImportPaymentType().name());
            if (paymentValidatorFactory.validate(row)) {
                cachingPaymentImport(row);
            }
        }
    }

    @Override
    public void readExcelForBank(ReaderConfiguration readerConfiguration, PaymentImportRequest request) {
        paymentBankValidatorFactory.createValidators();
        ExcelBeanReader<PaymentBankRow> excelBeanReader = new ExcelBeanReader<>(readerConfiguration, PaymentBankRow.class);
        ExcelBean<PaymentBankRow> excelBean = new ExcelBean<>(excelBeanReader);
        for (PaymentBankRow row : excelBean) {
            row.setImportProcessId(request.getImportProcessId());
            row.setImportType(request.getImportPaymentType().name());
            if (paymentBankValidatorFactory.validate(row)) {
                cachingPaymentImport(row);
            }
        }
    }

    @Override
    public void cachingPaymentImport(Row paymentRow) {
        PaymentImportCache paymentImportCache = PaymentCacheFactory.getPaymentImportCache(paymentRow);
        paymentImportCacheRepository.save(paymentImportCache);
    }

    @Override
    public void clearPaymentImportCache(String importProcessId) {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "id"));
        Page<PaymentImportCache> cacheList;
        do {
            cacheList = paymentImportCacheRepository.findAllByImportProcessId(importProcessId, pageable);
            paymentImportCacheRepository.deleteAll(cacheList.getContent());
            pageable.next();
        } while (cacheList.hasNext());
    }

    @Override
    public void readPaymentCacheAndSave(String importProcessId) {
        if (!paymentImportErrorRepository.existsPaymentImportErrorByImportProcessId(importProcessId)) {
            Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "id"));
            Page<PaymentImportCache> cacheList;
            do {
                cacheList = paymentImportCacheRepository.findAllByImportProcessId(importProcessId, pageable);
                List<PaymentDto> paymentDtoList = cacheList.map(cache -> {
                    PaymentDto paymentDto;
                    if (EImportPaymentType.EXPENSE.name().equals(cache.getImportType())) {
                        paymentDto = paymentRowMapper.toObject(cache).toAggregate();
                        Optional<ManagePaymentSourceDto> paymentSourceOptional = Optional.ofNullable(paymentSourceService.findByCodeActive(PAYMENT_SOURCE_EXP_CODE));
                        paymentSourceOptional.ifPresent(paymentDto::setPaymentSource);
                    } else {
                        paymentDto = paymentBankRowMapper.toObject(cache).toAggregate();
                        Optional<ManageBankAccountDto> manageBankAccountDtoOptional = Optional.ofNullable(bankAccountService.findByAccountNumber(cache.getBankAccount()));
                        manageBankAccountDtoOptional.ifPresent(paymentDto::setBankAccount);
                        Optional<ManagePaymentSourceDto> paymentSourceOptional = Optional.ofNullable(paymentSourceService.findByCodeActive(PAYMENT_SOURCE_BANK_CODE));
                        paymentSourceOptional.ifPresent(paymentDto::setPaymentSource);
                    }
                    paymentDto.setAgency(manageAgencyService.findByCode(cache.getAgency()));
                    paymentDto.setHotel(manageHotelService.findByCode(cache.getHotel()));
                    Optional<ManageClientDto> clientDtoOptional = Optional.ofNullable(paymentDto.getAgency().getClient());
                    clientDtoOptional.ifPresent(paymentDto::setClient);
                    paymentDto.setStatus(Status.ACTIVE);
                    Optional<ManagePaymentStatusDto> paymentStatusOptional = Optional.ofNullable(paymentStatusService.findByCode(PAYMENT_STATUS_CONF_CODE));
                    paymentStatusOptional.ifPresent(paymentDto::setPaymentStatus);
                    paymentDto.setId(UUID.randomUUID());
                    return paymentDto;
                }).toList();
                paymentService.createBulk(paymentDtoList);
                pageable.next();
            } while (cacheList.hasNext());
        }
    }

    @Override
    public String getPaymentImportProcessStatus(String importPaymentProcessId) {
        return redisTemplate.opsForValue().get(importPaymentProcessId);
    }

    @Override
    public PaginatedResponse getPaymentImportErrors(String importProcessId, Pageable pageable) {
        Page<PaymentImportError> page = paymentImportErrorRepository.findAllByImportProcessId(importProcessId, pageable);
        return new PaginatedResponse(page.getContent(), page.getTotalPages(), page.getNumberOfElements(), page.getTotalElements(), page.getSize(), page.getNumber());
    }
}
