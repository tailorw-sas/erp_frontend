package com.tailorw.tcaInnsist.controllers;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.tailorw.tcaInnsist.application.command.rate.sycnRateByInvoiceDate.SycnRateByInvoiceDateCommand;
import com.tailorw.tcaInnsist.application.command.rate.sycnRateByInvoiceDate.SycnRateByInvoiceDateMessage;
import com.tailorw.tcaInnsist.application.command.rate.sycnRateByInvoiceDate.SycnRateByInvoiceDateRequest;
import com.tailorw.tcaInnsist.application.query.objectResponse.SearchRateBetweenInvoiceDateResponse;
import com.tailorw.tcaInnsist.application.query.rate.searchRateBetweenInvoiceDate.SearchRateBetweenInvoiceDateQuery;
import com.tailorw.tcaInnsist.application.query.rate.searchRateBetweenInvoiceDate.SearchRateBetweenInvoiceDateQueryHandler;
import com.tailorw.tcaInnsist.application.query.rate.searchRateBetweenInvoiceDate.SearchRateBetweenInvoiceDateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room-rate")
public class RoomRateController {

    private final IMediator mediator;

    public RoomRateController(IMediator mediator){
        this.mediator = mediator;
    }

    @PostMapping("/sync")
    public ResponseEntity<?> sync(@RequestBody SycnRateByInvoiceDateRequest request){
        SycnRateByInvoiceDateCommand command = SycnRateByInvoiceDateCommand.fromRequest(request);
        SycnRateByInvoiceDateMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search-between-invoice-date")
    public ResponseEntity<?> searchBetweenInvoiceDate(@RequestBody SearchRateBetweenInvoiceDateRequest request){
        SearchRateBetweenInvoiceDateQuery query = new SearchRateBetweenInvoiceDateQuery(request.getProcessId(),
                request.getHotel(),
                request.getToInvoiceDate(),
                request.getFromInvoiceDate());
        SearchRateBetweenInvoiceDateResponse response = this.mediator.send(query);

        return ResponseEntity.ok(response);
    }
}
