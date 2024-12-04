package com.kynsoft.finamer.creditcard.application.query.hotelPaymentStatusHistory.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.HotelPaymentStatusHistoryResponse;
import com.kynsoft.finamer.creditcard.domain.services.IHotelPaymentStatusHistoryService;
import org.springframework.stereotype.Component;

@Component
public class FindHotelPaymentStatusHistoryByIdQueryHandler implements IQueryHandler<FindHotelPaymentStatusHistoryByIdQuery, HotelPaymentStatusHistoryResponse> {

    private final IHotelPaymentStatusHistoryService hotelPaymentStatusHistoryService;

    public FindHotelPaymentStatusHistoryByIdQueryHandler(IHotelPaymentStatusHistoryService hotelPaymentStatusHistoryService) {
        this.hotelPaymentStatusHistoryService = hotelPaymentStatusHistoryService;
    }

    @Override
    public HotelPaymentStatusHistoryResponse handle(FindHotelPaymentStatusHistoryByIdQuery query) {
        return new HotelPaymentStatusHistoryResponse(this.hotelPaymentStatusHistoryService.findById(query.getId()));
    }
}
