package com.kynsoft.finamer.payment.infrastructure.excel.mapper;

import com.kynsof.share.core.application.excel.mapper.IMapper;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentExpenseRow;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class PaymentRowMapper implements IMapper<PaymentImportCache, PaymentExpenseRow> {
    @Override
    public PaymentExpenseRow toObject(PaymentImportCache obj) {
        PaymentExpenseRow paymentExpenseRow = new PaymentExpenseRow();
        paymentExpenseRow.setImportProcessId(obj.getImportProcessId());
        paymentExpenseRow.setAmount(Double.parseDouble(obj.getPaymentAmount()));
        paymentExpenseRow.setTransactionDate(obj.getTransactionDate());
        paymentExpenseRow.setRemarks(obj.getRemarks());
        paymentExpenseRow.setImportType(obj.getImportType());
        paymentExpenseRow.setManageAgencyCode(obj.getAgency());
        paymentExpenseRow.setManageHotelCode(obj.getHotel());
        paymentExpenseRow.setRowNumber(obj.getRowNumber());
        return paymentExpenseRow;
    }

    @Override
    public PaymentImportCache toEntity(PaymentExpenseRow obj) {
        return new PaymentImportCache(obj);
    }

    @Override
    public List<PaymentExpenseRow> toObjectList(List<PaymentImportCache> listObj) {
        return listObj.stream().map(this::toObject).toList();
    }

    @Override
    public List<PaymentImportCache> toEntityList(List<PaymentExpenseRow> list) {
        return list.stream().map(this::toEntity).toList();
    }
}
