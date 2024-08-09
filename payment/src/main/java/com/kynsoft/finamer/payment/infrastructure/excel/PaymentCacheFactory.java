package com.kynsoft.finamer.payment.infrastructure.excel;

import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentBankRow;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.excel.bean.payment.PaymentRow;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;

import java.util.NoSuchElementException;

public class PaymentCacheFactory {

    public static PaymentImportCache getPaymentImportCache(Row row){
        if (row instanceof PaymentBankRow){
            return new PaymentImportCache((PaymentBankRow) row);
        }
        if (row instanceof PaymentRow){
            return new PaymentImportCache((PaymentRow) row);
        }
        if (row instanceof AntiToIncomeRow){
            return new PaymentImportCache((AntiToIncomeRow) row);
        }
        if (row instanceof PaymentDetailRow){
            return new PaymentImportCache((PaymentDetailRow) row);
        }
        throw new NoSuchElementException();
    }
}
