package com.kynsoft.finamer.payment.infrastructure.excel.mapper;

import com.kynsof.share.core.application.excel.mapper.IMapper;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentExpenseBookingRow;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentExpenseBookingRowMapper implements IMapper<PaymentImportCache, PaymentExpenseBookingRow> {

    @Override
    public PaymentExpenseBookingRow toObject(PaymentImportCache obj) {
        return null;
    }

    @Override
    public PaymentImportCache toEntity(PaymentExpenseBookingRow obj) {
        return null;
    }

    @Override
    public List<PaymentExpenseBookingRow> toObjectList(List<PaymentImportCache> listObj) {
        return null;
    }

    @Override
    public List<PaymentImportCache> toEntityList(List<PaymentExpenseBookingRow> list) {
        return null;
    }
}
