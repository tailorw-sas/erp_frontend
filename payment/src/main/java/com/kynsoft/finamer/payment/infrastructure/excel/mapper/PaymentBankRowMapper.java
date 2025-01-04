package com.kynsoft.finamer.payment.infrastructure.excel.mapper;

import com.kynsof.share.core.application.excel.mapper.IMapper;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentBankRow;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class PaymentBankRowMapper implements IMapper<PaymentImportCache, PaymentBankRow> {

    @Override
    public PaymentBankRow toObject(PaymentImportCache obj) {
        PaymentBankRow paymentBankRow = new PaymentBankRow();
        paymentBankRow.setBankAccount(obj.getBankAccount());
        paymentBankRow.setRemarks(obj.getRemarks());
        paymentBankRow.setManageHotelCode(obj.getHotel());
        paymentBankRow.setManageAgencyCode(obj.getAgency());
        paymentBankRow.setTransactionDate(obj.getTransactionDate());
        paymentBankRow.setImportProcessId(obj.getImportProcessId());
        paymentBankRow.setImportType(obj.getImportType());
        paymentBankRow.setAmount(Double.parseDouble(obj.getPaymentAmount()));
        paymentBankRow.setRowNumber(obj.getRowNumber());
        return paymentBankRow;
    }

    @Override
    public PaymentImportCache toEntity(PaymentBankRow obj) {
        return new PaymentImportCache(obj);
    }

    @Override
    public List<PaymentBankRow> toObjectList(List<PaymentImportCache> listObj) {
        return listObj.stream().map(this::toObject).toList();
    }

    @Override
    public List<PaymentImportCache> toEntityList(List<PaymentBankRow> list) {
        return list.stream().map(this::toEntity).toList();
    }
}
