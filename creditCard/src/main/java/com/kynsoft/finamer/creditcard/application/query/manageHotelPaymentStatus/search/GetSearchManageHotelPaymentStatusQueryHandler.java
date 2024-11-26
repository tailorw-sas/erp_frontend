package com.kynsoft.finamer.creditcard.application.query.manageHotelPaymentStatus.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IManageHotelPaymentStatusService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageHotelPaymentStatusQueryHandler implements IQueryHandler<GetSearchManageHotelPaymentStatusQuery, PaginatedResponse> {

    private final IManageHotelPaymentStatusService service;

    public GetSearchManageHotelPaymentStatusQueryHandler(IManageHotelPaymentStatusService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageHotelPaymentStatusQuery query) {
        return this.service.search(query.getPageable(), query.getFilter());
    }
}
