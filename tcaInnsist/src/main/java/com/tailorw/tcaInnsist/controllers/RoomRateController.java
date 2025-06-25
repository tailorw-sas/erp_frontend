package com.tailorw.tcaInnsist.controllers;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.tailorw.tcaInnsist.application.command.rate.sycnRateByInvoiceDate.SycnRateByInvoiceDateCommand;
import com.tailorw.tcaInnsist.application.command.rate.sycnRateByInvoiceDate.SycnRateByInvoiceDateMessage;
import com.tailorw.tcaInnsist.application.command.rate.sycnRateByInvoiceDate.SycnRateByInvoiceDateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room-rate")
public class RoomRateController {

    private final IMediator mediator;

    public RoomRateController(IMediator mediator){
        this.mediator = mediator;
    }

    @GetMapping("/sync")
    public ResponseEntity<?> sync(@RequestBody SycnRateByInvoiceDateRequest request){
        SycnRateByInvoiceDateCommand command = SycnRateByInvoiceDateCommand.fromRequest(request);
        SycnRateByInvoiceDateMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
