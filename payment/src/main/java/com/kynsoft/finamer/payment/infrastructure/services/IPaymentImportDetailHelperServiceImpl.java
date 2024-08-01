package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailRequest;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportError;
import com.kynsoft.finamer.payment.domain.excel.bean.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportDetailHelperService;
import com.kynsoft.finamer.payment.infrastructure.excel.PaymentCacheFactory;
import com.kynsoft.finamer.payment.infrastructure.excel.mapper.AntiIncomeRowMapper;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.anti.PaymentAntiValidatorFactory;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportErrorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class IPaymentImportDetailHelperServiceImpl implements IPaymentImportDetailHelperService {

    private final PaymentImportCacheRepository paymentImportCacheRepository;
    private final PaymentImportErrorRepository paymentImportErrorRepository;

    private final PaymentAntiValidatorFactory paymentAntiValidatorFactory;
    private final RedisTemplate<String, String> redisTemplate;
    private final IPaymentDetailService paymentDetailService;
    private final AntiIncomeRowMapper antiIncomeRowMapper;


    public IPaymentImportDetailHelperServiceImpl(PaymentImportCacheRepository paymentImportCacheRepository,
                                                 PaymentImportErrorRepository paymentImportErrorRepository,
                                                 PaymentAntiValidatorFactory paymentAntiValidatorFactory,
                                                 RedisTemplate<String, String> redisTemplate,
                                                 IPaymentDetailService paymentDetailService,
                                                 AntiIncomeRowMapper antiIncomeRowMapper) {
        this.paymentImportCacheRepository = paymentImportCacheRepository;
        this.paymentImportErrorRepository = paymentImportErrorRepository;
        this.paymentAntiValidatorFactory = paymentAntiValidatorFactory;
        this.redisTemplate = redisTemplate;
        this.paymentDetailService = paymentDetailService;
        this.antiIncomeRowMapper = antiIncomeRowMapper;
    }

    @Override
    public void readExcelForAntiIncome(ReaderConfiguration readerConfiguration, PaymentImportDetailRequest request) {
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
                List<PaymentDetailDto> parentDetails = new ArrayList<>();
                cacheList = paymentImportCacheRepository.findAllByImportProcessId(importProcessId, pageable);
                List<PaymentDetailDto> toSave = cacheList.map(paymentImportCache -> {
                    PaymentDetailDto parentPaymentDetail = paymentDetailService.findByGenId(Integer.parseInt(paymentImportCache.getTransactionId()));
                    PaymentDetailDto antiIncome = antiIncomeRowMapper.toObject(paymentImportCache).toAggregate();
                    antiIncome.setPayment(parentPaymentDetail.getPayment());
                    antiIncome.setTransactionType(parentPaymentDetail.getTransactionType());
                    antiIncome.setStatus(Status.ACTIVE);
                    antiIncome.setParentId(parentPaymentDetail.getPaymentDetailId());
                    antiIncome.setId(UUID.randomUUID());
                    List<PaymentDetailDto> children = new ArrayList<>(parentPaymentDetail.getChildren());
                    children.add(antiIncome);
                    parentPaymentDetail.setChildren(children);
                    parentPaymentDetail.setApplyDepositValue(Objects.nonNull(parentPaymentDetail.getApplyDepositValue() )? parentPaymentDetail.getApplyDepositValue():0- antiIncome.getAmount());
                    parentDetails.add(parentPaymentDetail);
                    return antiIncome;
                }).toList();
                paymentDetailService.bulk(toSave);
                paymentDetailService.bulk(parentDetails);
                pageable.next();
            } while (cacheList.hasNext());
        }
    }

    @Override
    public String getPaymentImportProcessStatus(String importProcessId) {
        return redisTemplate.opsForValue().get(importProcessId);
    }

    @Override
    public PaginatedResponse getPaymentImportErrors(String importProcessId, Pageable pageable) {
        Page<PaymentImportError> page = paymentImportErrorRepository.findAllByImportProcessId(importProcessId, pageable);
        return new PaginatedResponse(page.getContent(), page.getTotalPages(), page.getNumberOfElements(), page.getTotalElements(), page.getSize(), page.getNumber());
    }

}
