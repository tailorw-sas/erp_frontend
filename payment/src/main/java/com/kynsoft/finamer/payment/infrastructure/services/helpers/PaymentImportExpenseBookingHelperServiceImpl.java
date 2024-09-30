package com.kynsoft.finamer.payment.infrastructure.services.helpers;

import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsoft.finamer.payment.application.command.paymentImport.payment.PaymentImportRequest;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.excel.PaymentExpenseBookingImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentExpenseBookingRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentExpenseBookingRowError;
import com.kynsoft.finamer.payment.domain.services.AbstractPaymentImportHelperService;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.expenseBooking.PaymentExpenseBookingValidatorFactory;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportExpenseBookingErrorRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.expenseBooking.PaymentExpenseBookingImportCacheRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentImportExpenseBookingHelperServiceImpl extends AbstractPaymentImportHelperService {

    private final PaymentExpenseBookingImportCacheRepository cacheRepository;
    private final PaymentImportExpenseBookingErrorRepository errorRepository;
    private final PaymentExpenseBookingValidatorFactory expenseBookingValidatorFactory;
    private final IManageBookingService bookingService;

    List<String> availableClient;


    public PaymentImportExpenseBookingHelperServiceImpl(PaymentExpenseBookingImportCacheRepository cacheRepository,
                                                        PaymentImportExpenseBookingErrorRepository errorRepository,
                                                        RedisTemplate<String,String> redisTemplate,
                                                        PaymentExpenseBookingValidatorFactory expenseBookingValidatorFactory,
                                                        IManageBookingService bookingService) {
        super(redisTemplate);
        this.cacheRepository = cacheRepository;
        this.errorRepository = errorRepository;
        this.expenseBookingValidatorFactory = expenseBookingValidatorFactory;
        this.bookingService = bookingService;
        this.availableClient = new ArrayList<>();
    }

    public void readExcel(ReaderConfiguration readerConfiguration, Object rawRequest) {
        PaymentImportRequest request = (PaymentImportRequest) rawRequest;
        expenseBookingValidatorFactory.createValidators();
        ExcelBeanReader<PaymentExpenseBookingRow> excelBeanReader = new ExcelBeanReader<>(readerConfiguration, PaymentExpenseBookingRow.class);
        ExcelBean<PaymentExpenseBookingRow> excelBean = new ExcelBean<>(excelBeanReader);
        for (PaymentExpenseBookingRow row : excelBean) {
            row.setImportProcessId(request.getImportProcessId());
            row.setImportType(request.getImportPaymentType().name());
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


    @Override
    public void readPaymentCacheAndSave(Object rawRequest) {
        PaymentImportRequest request = (PaymentImportRequest) rawRequest;
        Map<String,List<PaymentExpenseBookingImportCache>> grouped = this.groupCacheByClient(request.getImportProcessId());
        for (Map.Entry<String, List<PaymentExpenseBookingImportCache>> entry : grouped.entrySet()) {
         //UploadFile
         //Create Attachment
         //Create Payment
         //ApplyPayment
        }

    }

    private String getClientName(String bookingId){
        ManageInvoiceDto manageInvoiceDto = bookingService.findByGenId(Long.parseLong(bookingId)).getInvoice();
        return manageInvoiceDto.getAgency().getClient().getName();
    }

    private Map<String,List<PaymentExpenseBookingImportCache>> groupCacheByClient(String importProcessId){
        Pageable pageable = PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, "id"));
        Map<String,List<PaymentExpenseBookingImportCache>> group = new HashMap<>();
        Page<PaymentExpenseBookingImportCache> elements;
        for (String clientName : availableClient) {
            do {
                elements = cacheRepository.findAllByImportProcessIdAndClientName(importProcessId, clientName, pageable);
                group.merge(clientName, elements.stream().toList(), (oldList, newList) -> {
                    oldList.addAll(newList);
                    return oldList;
                });
                pageable=pageable.next();
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
}
