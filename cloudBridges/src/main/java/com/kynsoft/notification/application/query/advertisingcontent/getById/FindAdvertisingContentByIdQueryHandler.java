package com.kynsoft.notification.application.query.advertisingcontent.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.notification.domain.dto.AdvertisingContentDto;
import com.kynsoft.notification.domain.service.IAdvertisingContentService;
import org.springframework.stereotype.Component;

@Component
public class FindAdvertisingContentByIdQueryHandler implements IQueryHandler<FindAdvertisingContentByIdQuery, AdvertisingContentResponse>  {

    private final IAdvertisingContentService service;

    public FindAdvertisingContentByIdQueryHandler(IAdvertisingContentService service) {
        this.service = service;
    }

    @Override
    public AdvertisingContentResponse handle(FindAdvertisingContentByIdQuery query) {
        AdvertisingContentDto response = service.findById(query.getId());

        return new AdvertisingContentResponse(response);
    }

}
