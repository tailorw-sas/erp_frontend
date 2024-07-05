package com.kynsoft.finamer.creditcard.application.query.manageMerchantHotelEnrolle.search;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantHotelEnrolleService;
import org.springframework.stereotype.Component;

@Component
public class GetSearchManageMerchantHotelEnrolleQueryHandler implements IQueryHandler<GetSearchManageMerchantHotelEnrolleQuery, PaginatedResponse> {

    private final IManageMerchantHotelEnrolleService service;

    public GetSearchManageMerchantHotelEnrolleQueryHandler(IManageMerchantHotelEnrolleService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetSearchManageMerchantHotelEnrolleQuery query) {
        return service.search(query.getPageable(), query.getFilter());
    }
}
