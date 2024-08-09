package com.kynsoft.finamer.payment.infrastructure.excel.event;

import com.kynsoft.finamer.payment.domain.excel.PaymentImportError;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentAntiRowError;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentBankRowError;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentDetailRowError;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentExpenseRowError;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportErrorRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportAntiErrorRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportBankErrorRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportDetailErrorRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.error.PaymentImportExpenseErrorRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentImportErrorHandler implements ApplicationListener<PaymentImportErrorEvent> {
    private final PaymentImportBankErrorRepository bankErrorRepository;

    private final PaymentImportExpenseErrorRepository expenseErrorRepository;

    private final PaymentImportAntiErrorRepository antiErrorRepository;

    private final PaymentImportDetailErrorRepository detailErrorRepository;

    public PaymentImportErrorHandler(PaymentImportBankErrorRepository bankErrorRepository,
                                     PaymentImportExpenseErrorRepository expenseErrorRepository,
                                     PaymentImportAntiErrorRepository antiErrorRepository,
                                     PaymentImportDetailErrorRepository detailErrorRepository) {
        this.bankErrorRepository = bankErrorRepository;
        this.expenseErrorRepository = expenseErrorRepository;
        this.antiErrorRepository = antiErrorRepository;
        this.detailErrorRepository = detailErrorRepository;

    }

    @Override
    public void onApplicationEvent(PaymentImportErrorEvent event) {
        Object errorSource = event.getError();

        if (errorSource instanceof PaymentBankRowError) {
            bankErrorRepository.save((PaymentBankRowError) errorSource);
        }
        if (errorSource instanceof PaymentExpenseRowError) {
            expenseErrorRepository.save((PaymentExpenseRowError) errorSource);
        }
        if (errorSource instanceof PaymentDetailRowError) {
            detailErrorRepository.save((PaymentDetailRowError) errorSource);
        }
        if (errorSource instanceof PaymentAntiRowError) {
            antiErrorRepository.save((PaymentAntiRowError) errorSource);
        }
    }
}
