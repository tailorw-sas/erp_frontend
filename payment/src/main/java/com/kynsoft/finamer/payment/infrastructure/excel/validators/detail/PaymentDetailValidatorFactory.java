package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ICache;
import com.kynsof.share.core.application.excel.validator.IImportControl;
import com.kynsof.share.core.application.excel.validator.IValidatorFactory;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.RowErrorField;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.projection.PaymentProjection;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.AntiToIncomeRow;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.excel.error.PaymentDetailRowError;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.excel.event.error.detail.PaymentImportDetailErrorEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.SecurityImportValidators;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.anti.PaymentImportAmountValidator;
import com.kynsoft.finamer.payment.infrastructure.excel.validators.anti.PaymentTransactionIdValidator;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class PaymentDetailValidatorFactory extends IValidatorFactory<PaymentDetailRow> {

    private final SecurityImportValidators securityImportValidators;

    private PaymentDetailAmountBalanceValidator paymentDetailAmountBalanceValidator;
    private PaymentDetailAntiAmountValidator paymentDetailAntiAmountValidator;
    private PaymentDetailAntiBookingValidator paymentDetailAntiBookingValidator;
    private PaymentDetailOtherDeductionsValidator paymentDetailOtherDeductionsValidator;
    private PaymentDetailExistPaymentValidator paymentDetailExistPaymentValidator;
    private PaymentDetailsBookingFieldValidator paymentDetailsBookingFieldValidator;
    private PaymentImportDetailAmountValidator paymentImportDetailAmountValidator;
    private PaymentDetailsNoApplyDepositValidator paymentDetailsNoApplyDepositValidator;
    private PaymentDetailAntiExistsValidator paymentDetailAntiExistsValidator;
    private PaymentDetailBelongToSamePayment paymentDetailBelongToSamePayment;
    private Cache cache;

    public PaymentDetailValidatorFactory(ApplicationEventPublisher paymentEventPublisher,
                                         PaymentImportCacheRepository paymentImportCacheRepository,
                                         SecurityImportValidators securityImportValidators
    ) {
        super(paymentEventPublisher);
        this.securityImportValidators = securityImportValidators;
    }

    @Override
    public void createValidators(){}

    @Override
    public void createValidators(ICache iCache) {
        this.cache = (Cache) iCache;

        paymentDetailAmountBalanceValidator = new PaymentDetailAmountBalanceValidator(cache);
        paymentDetailAntiAmountValidator = new PaymentDetailAntiAmountValidator(cache);
        paymentDetailAntiBookingValidator = new PaymentDetailAntiBookingValidator(cache);
        paymentDetailOtherDeductionsValidator = new PaymentDetailOtherDeductionsValidator(cache);

        paymentDetailExistPaymentValidator= new PaymentDetailExistPaymentValidator(applicationEventPublisher, cache);
        paymentDetailsBookingFieldValidator = new PaymentDetailsBookingFieldValidator(applicationEventPublisher, cache);
        paymentImportDetailAmountValidator = new PaymentImportDetailAmountValidator(applicationEventPublisher, cache);
        paymentDetailsNoApplyDepositValidator= new PaymentDetailsNoApplyDepositValidator(applicationEventPublisher, cache);
        paymentDetailAntiExistsValidator = new PaymentDetailAntiExistsValidator(applicationEventPublisher, cache);

        paymentDetailBelongToSamePayment = new PaymentDetailBelongToSamePayment(applicationEventPublisher, cache);


    }

    @Override
    public boolean validate(PaymentDetailRow toValidate){
        return false;
    }

    @Override
    public boolean validate(List<PaymentDetailRow> toValidateList) {
        int errors = 0;

        paymentDetailAmountBalanceValidator.validate(toValidateList, rowErrorFieldList);
        paymentDetailAntiAmountValidator.validate(toValidateList, rowErrorFieldList);
        paymentDetailAntiBookingValidator.validate(toValidateList, rowErrorFieldList);
        paymentDetailOtherDeductionsValidator.validate(toValidateList, rowErrorFieldList);
        errors += rowErrorFieldList.size();

        for(PaymentDetailRow toValidate : toValidateList){
            List<ErrorField> errorsList = new ArrayList<>();
            errors += paymentDetailExistPaymentValidator.validate(toValidate, errorsList) ? 0 : 1;
            errors += paymentDetailsBookingFieldValidator.validate(toValidate, errorsList) ? 0 : 1;
            errors += this.validatePaymentAmount(toValidate) ? 0 : 1;
            errors += paymentDetailsNoApplyDepositValidator.validate(toValidate,errorsList) ? 0 : 1;
            errors += paymentDetailAntiExistsValidator.validate(toValidate, errorsList) ? 0 : 1;
            errors += this.validateAsExternalPaymentId(toValidate) ? 0 : 1;

            PaymentDto paymentDto = cache.getPaymentByPaymentId(Long.parseLong(toValidate.getPaymentId()));
            if(Objects.nonNull(paymentDto)){
                errors += this.securityImportValidators.validateAgency(toValidate.getAgencys(), paymentDto.getAgency().getId(), errorsList) ? 0 : 1;
                errors += this.securityImportValidators.validateHotel(toValidate.getHotels(), paymentDto.getHotel().getId(), errorsList) ? 0 : 1;
            }

            if(!errorsList.isEmpty()){
                addErrorFieldToRowErrorField(rowErrorFieldList, errorsList, toValidate.getRowNumber());
            }
        }

        if (this.hasListErrors()) {
            Map<Integer, PaymentDetailRow> paymentDetailRowMap = getPaymentDetailListMap(toValidateList);
            rowErrorFieldList.forEach(rowError -> {
                PaymentDetailRow paymentRow = paymentDetailRowMap.get(rowError.getRowNumber());

                PaymentImportDetailErrorEvent paymentImportErrorEvent = new PaymentImportDetailErrorEvent(
                        new PaymentDetailRowError(null,
                                rowError.getRowNumber(),
                                paymentRow.getImportProcessId(),
                                rowError.getErrorFieldList(),
                                paymentRow));
                this.sendErrorEvent(paymentImportErrorEvent);
            });
        }

        boolean result = errors == 0;
        this.clearRowErrors();
        return result;
    }


    private Map<Integer, PaymentDetailRow> getPaymentDetailListMap(List<PaymentDetailRow> paymentDetailRows){
        return paymentDetailRows.stream()
                .collect(Collectors.toMap(PaymentDetailRow::getRowNumber, paymentRow -> paymentRow));
    }

    private boolean validatePaymentAmount(PaymentDetailRow toValidate){
        if (Objects.isNull(toValidate.getAnti())){
            return paymentImportDetailAmountValidator.validate(toValidate,errorFieldList);
        } return true;
    }

    private boolean validateAsExternalPaymentId(PaymentDetailRow toValidate){
        if (Objects.nonNull(toValidate.getExternalPaymentId())){
            return paymentDetailBelongToSamePayment.validate(toValidate,errorFieldList);
        } return true;
    }
}
