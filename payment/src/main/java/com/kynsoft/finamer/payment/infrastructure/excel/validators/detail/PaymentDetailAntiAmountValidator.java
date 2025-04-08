package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.application.excel.validator.ICache;
import com.kynsof.share.core.application.excel.validator.IImportControl;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.ImportControl;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportCache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportCacheRepository;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Objects;

public class PaymentDetailAntiAmountValidator extends ExcelRuleValidator<PaymentDetailRow> {

    private final PaymentImportCacheRepository paymentImportCacheRepository;

    protected PaymentDetailAntiAmountValidator(ApplicationEventPublisher applicationEventPublisher,
                                               PaymentImportCacheRepository paymentImportCacheRepository) {
        super(applicationEventPublisher);
        this.paymentImportCacheRepository = paymentImportCacheRepository;
    }

    @Override
    public boolean validate(PaymentDetailRow obj, List<ErrorField> errorFieldList) {
        return true;
    }

    @Override
    public boolean validate(PaymentDetailRow obj, List<ErrorField> errorFieldList, ICache icache, IImportControl importControl) {
        Cache cache = (Cache)icache;
        ImportControl control = (ImportControl)importControl;

        ManageBookingDto booking = getBooking(obj, cache);
        if(Objects.isNull(booking)){
            errorFieldList.add(new ErrorField("Booking", "The booking not found or is duplicated"));
            return false;
        }

        PaymentDetailDto paymentDetail = cache.getPaymentDetailByPaymentDetailId(obj.getAnti().longValue());
        if (Objects.nonNull(paymentDetail)) {
            if (Objects.isNull(paymentDetail.getPayment()) || paymentDetail.getPayment().getPaymentId() != Long.parseLong(obj.getPaymentId())) {
                errorFieldList.add(new ErrorField("Payment", "The specified Payment is not associated with the given Payment Detail."));
                return false;
            }

            List<PaymentImportCache> pageCache = paymentImportCacheRepository.findAllByImportProcessId(obj.getImportProcessId());
            double amountTotal = pageCache.stream().filter(Objects::nonNull)
                    .filter(paymentImportCache -> Objects.nonNull(paymentImportCache.getAnti())
                            && !paymentImportCache.getAnti().isEmpty()
                            && Long.parseLong(paymentImportCache.getPaymentId()) == paymentDetail.getPayment().getPaymentId())
                    .map(paymentCache -> Double.parseDouble(paymentCache.getPaymentAmount()))
                    .reduce(0.0, Double::sum);

            if(obj.getBalance() + amountTotal >= booking.getAmountBalance()){
                errorFieldList.add(new ErrorField("Booking Balance", "The selected transaction amount must be less or equal than the invoice balance"));
                control.setShouldStopProcess(true);
                return false;
            }

        }
        
        return true;
    }

    private ManageBookingDto getBooking(PaymentDetailRow obj, Cache cache){
        if(Objects.nonNull(obj.getBookId())){
            return cache.getBooking(Long.parseLong(obj.getBookId()));
        }

        if(Objects.nonNull(obj.getCoupon())){
            List<ManageBookingDto> bookingsByCoupon = cache.getBookingsByCoupon(obj.getCoupon());
            if(bookingsByCoupon.isEmpty()){
                return null;
            }

            if(bookingsByCoupon.size() > 1){
                return null;
            }

            return bookingsByCoupon.get(0);
        }

        return null;
    }
}
