package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.query.attachmentStatusHistory.getById.FindAttachmentStatusHistoryByIdQuery;
import com.kynsoft.finamer.payment.application.query.attachmentStatusHistory.search.GetSearchAttachmentStatusHistoryQuery;
import com.kynsoft.finamer.payment.application.query.objectResponse.AttachmentStatusHistoryResponse;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attachment-status-history")
public class AttachmentStatusHistoryController {

    private final IMediator mediator;

    public AttachmentStatusHistoryController(IMediator mediator) {
        this.mediator = mediator;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindAttachmentStatusHistoryByIdQuery query = new FindAttachmentStatusHistoryByIdQuery(id);
        AttachmentStatusHistoryResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchAttachmentStatusHistoryQuery query = new GetSearchAttachmentStatusHistoryQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
