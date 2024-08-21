package com.kynsoft.finamer.creditcard.application.query.manageMerchantHotelEnrolle.findHotelsByMerchant;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantHotelEnrolleService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

@Component
public class FindHotelsByMerchantQueryHandler implements IQueryHandler<FindHotelsByMerchantQuery, PaginatedResponse> {

    private final IManageMerchantHotelEnrolleService service;

    private final IManageMerchantService merchantService;

    public FindHotelsByMerchantQueryHandler(IManageMerchantHotelEnrolleService service, IManageMerchantService merchantService) {
        this.service = service;
        this.merchantService = merchantService;
    }

    @Override
    public PaginatedResponse handle(FindHotelsByMerchantQuery query) {
        return this.service.findHotelsByManageMerchant(query.getPageable(), query.getFilter());
    }
}
