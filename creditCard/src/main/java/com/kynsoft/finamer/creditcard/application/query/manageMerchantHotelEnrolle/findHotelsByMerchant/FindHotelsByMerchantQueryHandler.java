package com.kynsoft.finamer.creditcard.application.query.manageMerchantHotelEnrolle.findHotelsByMerchant;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.HotelsByMerchantResponse;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageHotelResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantHotelEnrolleService;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantService;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class FindHotelsByMerchantQueryHandler implements IQueryHandler<FindHotelsByMerchantQuery, HotelsByMerchantResponse> {

    private final IManageMerchantHotelEnrolleService service;

    private final IManageMerchantService merchantService;

    public FindHotelsByMerchantQueryHandler(IManageMerchantHotelEnrolleService service, IManageMerchantService merchantService) {
        this.service = service;
        this.merchantService = merchantService;
    }

    @Override
    public HotelsByMerchantResponse handle(FindHotelsByMerchantQuery query) {
        ManageMerchantDto merchantDto = this.merchantService.findById(query.getManageMerchant());

        return new HotelsByMerchantResponse(this.service.findHotelsByManageMerchant(merchantDto).stream().map(ManageHotelResponse::new).collect(Collectors.toList()));
    }
}
