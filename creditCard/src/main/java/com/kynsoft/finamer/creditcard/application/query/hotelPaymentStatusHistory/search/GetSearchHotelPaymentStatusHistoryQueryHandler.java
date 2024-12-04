package com.kynsoft.finamer.creditcard.application.query.hotelPaymentStatusHistory.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IHotelPaymentStatusHistoryService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchHotelPaymentStatusHistoryQueryHandler implements IQueryHandler<GetSearchHotelPaymentStatusHistoryQuery, PaginatedResponse> {

    private final IHotelPaymentStatusHistoryService hotelPaymentStatusHistoryService;

    public GetSearchHotelPaymentStatusHistoryQueryHandler(IHotelPaymentStatusHistoryService hotelPaymentStatusHistoryService) {
        this.hotelPaymentStatusHistoryService = hotelPaymentStatusHistoryService;
    }

    @Override
    public PaginatedResponse handle(GetSearchHotelPaymentStatusHistoryQuery query) {
        return this.hotelPaymentStatusHistoryService.search(query.getPageable(), query.getFilter());
    }
}
