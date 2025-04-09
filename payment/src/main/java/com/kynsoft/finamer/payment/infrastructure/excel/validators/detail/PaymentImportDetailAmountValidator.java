package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.application.excel.validator.ICache;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.projection.PaymentProjection;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.infrastructure.services.http.BookingHttpGenIdService;
import com.kynsoft.finamer.payment.infrastructure.services.http.helper.BookingImportAutomaticeHelperServiceImpl;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PaymentImportDetailAmountValidator extends ExcelRuleValidator<PaymentDetailRow> {

    private final Cache cache;

    protected PaymentImportDetailAmountValidator(ApplicationEventPublisher applicationEventPublisher,
                                                 Cache cache) {
        super(applicationEventPublisher);
        this.cache = cache;
    }

    @Override
    public boolean validate(PaymentDetailRow obj, List<ErrorField> errorFieldList) {
        if (Objects.isNull(obj.getBalance())) {
            errorFieldList.add(new ErrorField("Balance", "Payment Amount can't be empty"));
            return false;
        }

        boolean valid = obj.getBalance() > 0;
        if (!valid) {
            errorFieldList.add(new ErrorField("Balance", "Payment Amount must be greater than 0"));
            return false;
        }

        PaymentDto paymentDto = this.cache.getPaymentByPaymentId(Long.parseLong(obj.getPaymentId()));
        if(Objects.isNull(paymentDto)){
            errorFieldList.add(new ErrorField("Payment Id", "The Payment not found."));
            return false;
        }

        ManagePaymentTransactionTypeDto transactionTypeDto = this.cache.getManageTransactionTypeByCode(obj.getTransactionType().trim());
        if(Objects.isNull(transactionTypeDto)){
            errorFieldList.add(new ErrorField("Transaction type", "Transaction type not found."));
            return false;
        }

        if (transactionTypeDto.getDeposit()) {
            errorFieldList.add(new ErrorField("transactionType", "The transaction type is deposit."));
            return false;
        }

        if (transactionTypeDto.getCash() && (paymentDto.getPaymentBalance() < obj.getBalance())) {//Si es de tipo cash, el balance debe ser menor o igual que el payment balance.
            errorFieldList.add(new ErrorField("Balance", "The balance is greater than the amount balance of the payment."));
            return false;
        }

        if (Objects.isNull(obj.getBookId())){
            List<ManageBookingDto> bookingByCouponList = this.cache.getBookingsByCoupon(obj.getCoupon());
            if(Objects.isNull(bookingByCouponList)){
                errorFieldList.add(new ErrorField("bookingId", "The booking not exist."));
            }

            /*if (Objects.nonNull(obj.getCoupon()) && Objects.nonNull(bookingByCouponList) && bookingByCouponList.size() > 1) {
                errorFieldList.add(new ErrorField("coupon", "Payment was not applied because the coupon is duplicated."));
            }*/
        }else{
            ManageBookingDto bookingDto = this.cache.getBooking(Long.valueOf(obj.getBookId()));
            if(Objects.isNull(bookingDto)){
                errorFieldList.add(new ErrorField("bookingId", "The booking not exist."));
                return false;
            }

            //Si es de tipo other deduction, el booking balance debe ser mayor o igual que el balance del excel.
            //TODO: duda, creo que para el other deduction debia ser igual estricto. el balnce del booking y el del excel????
            if (!transactionTypeDto.getCash() && !transactionTypeDto.getDeposit() && !transactionTypeDto.getApplyDeposit() && (bookingDto.getAmountBalance() < obj.getBalance())) {
                errorFieldList.add(new ErrorField("bookingId", "The amount to apply is greater than the balance of the booking."));
                return false;
            }
        }

        return true;
    }
}
