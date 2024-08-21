package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.query.manageHotel.search.GetSearchHotelQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotel")
public class HotelController {

    private final IMediator mediator;

    public HotelController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchHotelQuery query = new GetSearchHotelQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
