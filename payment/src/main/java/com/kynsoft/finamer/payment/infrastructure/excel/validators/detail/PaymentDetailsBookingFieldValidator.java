package com.kynsoft.finamer.payment.infrastructure.excel.validators.detail;

import com.kynsof.share.core.application.excel.validator.ExcelRuleValidator;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.excel.Cache;
import com.kynsoft.finamer.payment.domain.excel.bean.detail.PaymentDetailRow;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.infrastructure.services.http.BookingHttpGenIdService;
import com.kynsoft.finamer.payment.infrastructure.services.http.helper.BookingImportAutomaticeHelperServiceImpl;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PaymentDetailsBookingFieldValidator extends ExcelRuleValidator<PaymentDetailRow> {

    private final Cache cache;

    protected PaymentDetailsBookingFieldValidator(ApplicationEventPublisher applicationEventPublisher,
                                                  Cache cache) {
        super(applicationEventPublisher);
        this.cache = cache;
    }

    @Override
    public boolean validate(PaymentDetailRow obj, List<ErrorField> errorFieldList) {
        PaymentDto payment = this.cache.getPaymentByPaymentId(Long.parseLong(obj.getPaymentId()));

        if(Objects.isNull(payment)){
            errorFieldList.add(new ErrorField("Payment Id", "The payment not exist."));
            return false;
        }

        if(Objects.nonNull(obj.getBookId())){
            ManageBookingDto booking = this.cache.getBooking(Long.parseLong(obj.getBookId()));

            if(Objects.isNull(booking)){
                errorFieldList.add(new ErrorField("bookingId", "The booking not exist."));
                return false;
            }

            if(Objects.nonNull(booking.getInvoice())
                    && Objects.nonNull(booking.getInvoice().getAgency())
                    && Objects.nonNull(booking.getInvoice().getAgency().getClient())
            ){
                if(!booking.getInvoice().getAgency().getClient().getCode().equals(payment.getClient().getCode())){
                    errorFieldList.add(new ErrorField("bookingClient", "The booking is not valid because it does not belong to the same client as the payment."));
                    return false;
                }
            }

            //Set<ManageHotelDto> hotels = new HashSet<>();
            //TODO Para aplicar un pago puedo coger una factura de un hotel del mismo trading company y no desde otro hotel
            /*if(Objects.nonNull(booking.getInvoice()) && Objects.nonNull(booking.getInvoice().getHotel())
             && booking.getInvoice().getHotel().getApplyByTradingCompany()){

            }*/
            return true;
        }

        if (Objects.isNull(obj.getCoupon()) || obj.getCoupon().trim().isEmpty()) {
            errorFieldList.add(new ErrorField("Cuopon", "The cuopon field must not be empty"));
            return false;
        }else{
            List<ManageBookingDto> bookingByCuopon = this.cache.getBookingsByCoupon(obj.getCoupon());
            if(Objects.nonNull(bookingByCuopon) && bookingByCuopon.size() == 1){
                if(!bookingByCuopon.get(0).getInvoice().getAgency().getClient().getCode().equals(payment.getClient().getCode())){
                    errorFieldList.add(new ErrorField("bookingClient", "The booking is not valid because it does not belong to the same client as the payment."));
                    return false;
                }
            }
        }

        return true;
    }
}
