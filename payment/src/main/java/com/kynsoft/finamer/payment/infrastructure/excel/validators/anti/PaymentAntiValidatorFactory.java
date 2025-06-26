package com.kynsoft.finamer.payment.infrastructure.excel.validators.anti;

import com.kynsof.share.core.application.excel.validator.ICache;
import com.kynsof.share.core.application.excel.validator.IValidatorFactory;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailSimpleDto;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentAntiRowError;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentDetailRowError;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.infrastructure.excel.event.error.anti.PaymentImportAntiErrorEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.event.error.detail.PaymentImportDetailErrorEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.SecurityImportValidators;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PaymentAntiValidatorFactory extends IValidatorFactory<AntiToIncomeRow> {

    private PaymentTransactionIdValidator paymentTransactionIdValidator;
    private PaymentImportAmountValidator paymentImportAmountValidator;
    private PaymentTotalAmountValidator paymentTotalAmountValidator;


    private SecurityImportValidators securityImportValidators;

    public PaymentAntiValidatorFactory(ApplicationEventPublisher paymentEventPublisher) {
        super(paymentEventPublisher);
    }

    @Override
    public void createValidators() {

    }

    @Override
    public void createValidators(ICache iCache) {
        Cache cache = (Cache)iCache;

        this.paymentTransactionIdValidator = new PaymentTransactionIdValidator(cache, applicationEventPublisher);
        this.paymentImportAmountValidator = new PaymentImportAmountValidator(applicationEventPublisher);
        this.paymentTotalAmountValidator = new PaymentTotalAmountValidator(cache);
        this.securityImportValidators = new SecurityImportValidators(cache);
    }

    @Override
    public boolean validate(AntiToIncomeRow toValidate) {
        return true;
    }

    @Override
    public boolean validate(List<AntiToIncomeRow> toValidateList){
        int errors = 0;

        for (AntiToIncomeRow toValidateRow : toValidateList) {
            List<ErrorField> errorsList = new ArrayList<>();
            errors += paymentTransactionIdValidator.validate(toValidateRow, errorsList) ? 0 : 1;
            errors += paymentImportAmountValidator.validate(toValidateRow, errorsList) ? 0 : 1;

            this.securityImportValidators.validateAgency(toValidateRow.getTransactionId().longValue(), errorFieldList);
            this.securityImportValidators.validateHotel(toValidateRow.getTransactionId().longValue(), errorFieldList);

            if(!errorsList.isEmpty()){
                addErrorFieldToRowErrorField(rowErrorFieldList, errorsList, toValidateRow.getRowNumber());
            }
        }

        paymentTotalAmountValidator.validate(toValidateList, rowErrorFieldList);
        errors += rowErrorFieldList.size();

        if (this.hasListErrors()) {
            Map<Integer, AntiToIncomeRow> antiToIncomeRowMap = getAntiToIncomeRowsMap(toValidateList);
            rowErrorFieldList.forEach(rowError -> {
                AntiToIncomeRow antiToIncomeRow = antiToIncomeRowMap.get(rowError.getRowNumber());

                PaymentImportAntiErrorEvent paymentImportErrorEvent = new PaymentImportAntiErrorEvent(
                        new PaymentAntiRowError(null,
                                rowError.getRowNumber(),
                                antiToIncomeRow.getImportProcessId(),
                                rowError.getErrorFieldList(),
                                antiToIncomeRow));
                this.sendErrorEvent(paymentImportErrorEvent);
            });
        }

        boolean result = errors == 0;
        this.clearRowErrors();
        return result;
    }

    private Map<Integer, AntiToIncomeRow> getAntiToIncomeRowsMap(List<AntiToIncomeRow> antiToIncomeRows){
        return antiToIncomeRows.stream()
                .collect(Collectors.toMap(AntiToIncomeRow::getRowNumber, antiToIncomeRow -> antiToIncomeRow));
    }

}
