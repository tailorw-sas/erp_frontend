package com.kynsoft.finamer.payment.application.query.attachmentStatusHistory.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.application.query.objectResponse.AttachmentStatusHistoryResponse;
import com.kynsoft.finamer.payment.domain.dto.AttachmentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.services.IAttachmentStatusHistoryService;
import org.springframework.stereotype.Component;

@Component
public class FindAttachmentStatusHistoryByIdQueryHandler implements IQueryHandler<FindAttachmentStatusHistoryByIdQuery, AttachmentStatusHistoryResponse>  {

    private final IAttachmentStatusHistoryService service;

    public FindAttachmentStatusHistoryByIdQueryHandler(IAttachmentStatusHistoryService service) {
        this.service = service;
    }

    @Override
    public AttachmentStatusHistoryResponse handle(FindAttachmentStatusHistoryByIdQuery query) {
        AttachmentStatusHistoryDto response = service.findById(query.getId());

        return new AttachmentStatusHistoryResponse(response);
    }
}
