package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class PaymentDetailAntiExistsValidator extends ExcelRuleValidator<PaymentDetailRow> {

    private final Cache cache;

    public PaymentDetailAntiExistsValidator(ApplicationEventPublisher applicationEventPublisher,
                                            Cache cache){
        super(applicationEventPublisher);
        this.cache = cache;
    }

    @Override
    public boolean validate(PaymentDetailRow obj, List<ErrorField> errorFieldList) {
        ManagePaymentTransactionTypeDto transactionType = this.cache.getManageTransactionTypeByCode(obj.getTransactionType());
        if(Objects.isNull(transactionType)){
            errorFieldList.add(new ErrorField("Transaction type", "Transaction type not found."));
            return false;
        }

        if(transactionType.getApplyDeposit()){
            if (Objects.isNull(obj.getAnti())) {
                errorFieldList.add(new ErrorField("Transaction id", "Transaction id can't be empty."));
                return false;
            }

            PaymentDetailDto paymentDetailDto = cache.getPaymentDetailByPaymentDetailId(obj.getAnti().longValue());
            if(Objects.isNull(paymentDetailDto)){
                errorFieldList.add(new ErrorField("Transaction id", "Payment Details not found: " + obj.getAnti().intValue()));
                return false;
            }

            if (!paymentDetailDto.getTransactionType().getDeposit()) {
                errorFieldList.add(new ErrorField("Transaction id", "Transaction isn't deposit type"));
                return false;
            }

            if(Objects.nonNull(obj.getBookId())){
                ManageBookingDto bookingDto = cache.getBooking(Long.parseLong(obj.getBookId()));
                if(Objects.isNull(bookingDto)){
                    return validateBookingCuopon(obj, errorFieldList);
                }
            }else{
                return validateBookingCuopon(obj, errorFieldList);
            }
        }

        return true;
    }

    private boolean validateBookingCuopon(PaymentDetailRow obj, List<ErrorField> errorFieldList){
        if(Objects.isNull(obj.getCoupon()) && Objects.isNull(obj.getBookId())){
            errorFieldList.add(new ErrorField("Booking", "Both Booking Id or Coupon Number are required and must not be empty."));
            return false;
        }

        if(Objects.isNull(obj.getCoupon())){
            errorFieldList.add(new ErrorField("Booking", "Coupon must not be empty when BookId is empty"));
            return false;
        }

        List<ManageBookingDto> bookingsByCuopon = cache.getBookingsByCoupon(obj.getCoupon());
        if (Objects.isNull(bookingsByCuopon) || bookingsByCuopon.isEmpty()){
            errorFieldList.add(new ErrorField("Booking", "Booking not found"));
            return false;
        }

        return true;
    }
}
