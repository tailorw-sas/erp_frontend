package com.kynsoft.finamer.creditcard.application.query.manageHotelPaymentStatus.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageHotelPaymentStatusResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelPaymentStatusDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageHotelPaymentStatusService;
import org.springframework.stereotype.Component;

@Component
public class FindManageHotelPaymentStatusByIdQueryHandler implements IQueryHandler<FindManageHotelPaymentStatusByIdQuery, ManageHotelPaymentStatusResponse> {

    private final IManageHotelPaymentStatusService service;

    public FindManageHotelPaymentStatusByIdQueryHandler(IManageHotelPaymentStatusService service) {
        this.service = service;
    }

    @Override
    public ManageHotelPaymentStatusResponse handle(FindManageHotelPaymentStatusByIdQuery query) {
        ManageHotelPaymentStatusDto dto = this.service.findById(query.getId());
        return new ManageHotelPaymentStatusResponse(dto);
    }
}
