package com.kynsoft.finamer.payment.application.query.manageBooking.getByListIds;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManageBookingResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class GetBookingByListIdsQueryHandler implements IQueryHandler<GetBookingByListIdsQuery, ManageBookingResponse>  {

    private final IManageBookingService service;

    public GetBookingByListIdsQueryHandler(IManageBookingService service) {
        this.service = service;
    }

    @Override
    public ManageBookingResponse handle(GetBookingByListIdsQuery query) {
        List<ManageBookingDto> bookings = this.service.findByBookingIdIn(List.of(query.getId()));
        ManageBookingResponse response = new ManageBookingResponse(bookings.get(0));

        return response;
    }
}
