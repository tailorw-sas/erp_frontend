package com.kynsoft.finamer.insis.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.query.roomRate.search.GetSearchRoomRateQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/room-rate")
public class RoomRateController {

    private final IMediator mediator;

    public RoomRateController(IMediator mediator){
        this.mediator = mediator;
    }

    @PostMapping("search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request){
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchRoomRateQuery query = new GetSearchRoomRateQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }
}
