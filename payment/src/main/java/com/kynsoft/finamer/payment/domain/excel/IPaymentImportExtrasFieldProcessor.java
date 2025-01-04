package com.kynsoft.finamer.payment.domain.excel;

public interface IPaymentImportExtrasFieldProcessor<T>{
    T addExtrasField(T rowError);
}
