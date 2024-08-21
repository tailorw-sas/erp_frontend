package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.query.manageBooking.search.GetSearchManageBookingQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment/manage-booking")
public class ManageBookingController {

    private final IMediator mediator;

    public ManageBookingController(IMediator mediator) {

        this.mediator = mediator;
    }
    
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageBookingQuery query = new GetSearchManageBookingQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
