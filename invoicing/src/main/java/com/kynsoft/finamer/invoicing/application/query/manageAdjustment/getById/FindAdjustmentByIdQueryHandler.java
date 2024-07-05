package com.kynsoft.finamer.invoicing.application.query.manageAdjustment.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageAdjustmentResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAdjustmentService;
import org.springframework.stereotype.Component;

@Component
public class FindAdjustmentByIdQueryHandler implements IQueryHandler<FindAdjustmentByIdQuery, ManageAdjustmentResponse>  {

    private final IManageAdjustmentService service;

    public FindAdjustmentByIdQueryHandler(IManageAdjustmentService service) {
        this.service = service;
    }

    @Override
    public ManageAdjustmentResponse handle(FindAdjustmentByIdQuery query) {
        ManageAdjustmentDto response = service.findById(query.getId());

        return new ManageAdjustmentResponse(response);
    }
}
