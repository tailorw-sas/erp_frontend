package com.kynsoft.finamer.settings.application.query.managePaymentAttachmentStatus.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagePaymentAttachmentStatusResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentAttachmentStatusService;
import org.springframework.stereotype.Component;

@Component
public class FindPaymentAttachmentStatusByIdQueryHandler implements IQueryHandler<FindPaymentAttachmentStatusByIdQuery, ManagePaymentAttachmentStatusResponse> {
    
    private final IManagePaymentAttachmentStatusService service;
    
    public FindPaymentAttachmentStatusByIdQueryHandler(final IManagePaymentAttachmentStatusService service) {
        this.service = service;
    }
    
    @Override
    public ManagePaymentAttachmentStatusResponse handle(FindPaymentAttachmentStatusByIdQuery query) {
        ManagePaymentAttachmentStatusDto dto = service.findById(query.getId());
        return new ManagePaymentAttachmentStatusResponse(dto);
    }
}
