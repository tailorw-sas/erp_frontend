package com.kynsoft.finamer.payment.application.query.shareFile.search;


import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.domain.services.IPaymentShareFileService;
import com.kynsoft.finamer.payment.domain.services.ITestService;
import org.springframework.stereotype.Component;

@Component
public class GetShareFileQueryHandler implements IQueryHandler<GetShareFileQuery, PaginatedResponse> {
    private final IPaymentShareFileService service;
    
    public GetShareFileQueryHandler(IPaymentShareFileService service) {
        this.service = service;
    }

    @Override
    public PaginatedResponse handle(GetShareFileQuery query) {

        return this.service.search(query.getPageable(),query.getFilter());
    }
}
