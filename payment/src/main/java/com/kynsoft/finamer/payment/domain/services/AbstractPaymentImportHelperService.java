package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.application.excel.validator.IImportControl;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;

public abstract class AbstractPaymentImportHelperService {

    protected int  totalProcessRow;
    protected AbstractPaymentImportHelperService() {
    }

    public abstract void readExcel(ReaderConfiguration readerConfiguration, Object request);

    public void readExcel(ReaderConfiguration readerConfiguration, Object request, IImportControl importControl){
        readExcel(readerConfiguration, request);
    }

    public abstract void cachingPaymentImport(Row paymentRow);

    public abstract void clearPaymentImportCache(String importProcessId);

    public abstract void readPaymentCacheAndSave(Object request);

    public abstract PaginatedResponse getPaymentImportErrors(String importProcessId, Pageable pageable);

    public UUID getPaymentIdStoreFromCache(String paymentId){
        return null;
    }

    public int getTotalProcessRow() {
        return totalProcessRow;
    }
}
