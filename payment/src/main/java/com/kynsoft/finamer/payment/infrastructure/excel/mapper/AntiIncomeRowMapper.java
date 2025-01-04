package com.kynsoft.finamer.payment.infrastructure.excel.mapper;

import com.kynsof.share.core.application.excel.mapper.IMapper;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.AntiToIncomeRow;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class AntiIncomeRowMapper implements IMapper<PaymentImportCache, AntiToIncomeRow> {

    @Override
    public AntiToIncomeRow toObject(PaymentImportCache obj) {
        AntiToIncomeRow antiToIncomeRow = new AntiToIncomeRow();
        antiToIncomeRow.setAmount(Double.parseDouble(obj.getPaymentAmount()));
        antiToIncomeRow.setRemarks(obj.getRemarks());
        antiToIncomeRow.setTransactionId(Double.parseDouble(obj.getTransactionId()));
        antiToIncomeRow.setImportType(obj.getImportType());
        antiToIncomeRow.setImportProcessId(obj.getImportProcessId());
        antiToIncomeRow.setRowNumber(obj.getRowNumber());
        return antiToIncomeRow;
    }

    @Override
    public PaymentImportCache toEntity(AntiToIncomeRow obj) {
        return new PaymentImportCache(obj);
    }

    @Override
    public List<AntiToIncomeRow> toObjectList(List<PaymentImportCache> listObj) {
        return listObj.stream().map(this::toObject).toList();
    }

    @Override
    public List<PaymentImportCache> toEntityList(List<AntiToIncomeRow> list) {
        return list.stream().map(this::toEntity).toList();
    }
}
