package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.query.manageRatePlan.byCodeAndHotelCode.FindManageRatePlanByCodeAndHotelCodeQuery;
import com.kynsoft.finamer.invoicing.application.query.manageRatePlan.search.GetSearchManageRatePlanQuery;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageRatePlanResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-rate-plan")
public class ManageRatePlanController {

    private final IMediator mediator;

    public ManageRatePlanController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageRatePlanQuery query = new GetSearchManageRatePlanQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/code/{code}/hotel-code/{hotelCode}")
    public ResponseEntity<?> getById(@PathVariable String code, @PathVariable String hotelCode) {

        FindManageRatePlanByCodeAndHotelCodeQuery query = new FindManageRatePlanByCodeAndHotelCodeQuery(code, hotelCode);
        ManageRatePlanResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

}
