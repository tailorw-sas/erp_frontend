package com.kynsoft.finamer.payment.infrastructure.excel.mapper;

import com.kynsof.share.core.application.excel.mapper.IMapper;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.PaymentRow;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class PaymentRowMapper implements IMapper<PaymentImportCache, PaymentRow> {
    @Override
    public PaymentRow toObject(PaymentImportCache obj) {
        PaymentRow paymentRow = new PaymentRow();
        paymentRow.setImportProcessId(obj.getImportProcessId());
        paymentRow.setPaymentExp(Double.parseDouble(obj.getPaymentAmount()));
        paymentRow.setTransactionDate(obj.getTransactionDate());
        paymentRow.setRemarks(obj.getRemarks());
        paymentRow.setImportType(obj.getImportType());
        paymentRow.setAgencyCode(obj.getAgency());
        paymentRow.setHotelCode(obj.getHotel());
        return paymentRow;
    }

    @Override
    public PaymentImportCache toEntity(PaymentRow obj) {
        return new PaymentImportCache(obj);
    }

    @Override
    public List<PaymentRow> toObjectList(List<PaymentImportCache> listObj) {
        return listObj.stream().map(this::toObject).toList();
    }

    @Override
    public List<PaymentImportCache> toEntityList(List<PaymentRow> list) {
        return list.stream().map(this::toEntity).toList();
    }
}
