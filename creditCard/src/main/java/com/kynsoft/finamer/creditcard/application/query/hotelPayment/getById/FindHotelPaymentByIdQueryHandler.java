package com.kynsoft.finamer.creditcard.application.query.hotelPayment.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.HotelPaymentResponse;
import com.kynsoft.finamer.creditcard.domain.services.IHotelPaymentService;
import org.springframework.stereotype.Component;

@Component
public class FindHotelPaymentByIdQueryHandler implements IQueryHandler<FindHotelPaymentByIdQuery, HotelPaymentResponse> {

    private final IHotelPaymentService hotelPaymentService;

    public FindHotelPaymentByIdQueryHandler(IHotelPaymentService hotelPaymentService) {
        this.hotelPaymentService = hotelPaymentService;
    }

    @Override
    public HotelPaymentResponse handle(FindHotelPaymentByIdQuery query) {
        return new HotelPaymentResponse(hotelPaymentService.findById(query.getId()));
    }
}
