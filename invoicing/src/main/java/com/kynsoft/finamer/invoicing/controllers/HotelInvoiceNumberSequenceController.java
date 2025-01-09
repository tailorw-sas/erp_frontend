package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.hotelInvoiceNumberSequence.run.CreateHotelInvoiceNumberSequenceRunCommand;
import com.kynsoft.finamer.invoicing.application.command.hotelInvoiceNumberSequence.updateByCode.UpdateCodeAndValueCommand;
import com.kynsoft.finamer.invoicing.application.command.hotelInvoiceNumberSequence.updateByCode.UpdateCodeAndValuerRequest;
import com.kynsoft.finamer.invoicing.application.command.hotelInvoinceNumberSequence.create.CreateHotelInvoiceNumberSequenceCommand;
import com.kynsoft.finamer.invoicing.application.command.hotelInvoinceNumberSequence.create.CreateHotelInvoiceNumberSequenceMessage;
import com.kynsoft.finamer.invoicing.application.command.hotelInvoinceNumberSequence.create.CreateHotelInvoiceNumberSequenceRequest;
import com.kynsoft.finamer.invoicing.application.query.hotelInvoiceNumberSequence.getByCodeHotel.FindHotelInvoiceNumberSequenceByIdQuery;
import com.kynsoft.finamer.invoicing.application.query.hotelInvoiceNumberSequence.getByTradingCode.FindHotelInvoiceNumberSequenceByTradingCodeQuery;
import com.kynsoft.finamer.invoicing.application.query.hotelInvoiceNumberSequence.search.GetSearchHotelInvoiceNumberSequenceQuery;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.HotelInvoiceNumberSequenceResponse;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotel-invoice-number-sequence")
public class HotelInvoiceNumberSequenceController {

    private final IMediator mediator;

    public HotelInvoiceNumberSequenceController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateHotelInvoiceNumberSequenceRequest request){
        CreateHotelInvoiceNumberSequenceCommand command = CreateHotelInvoiceNumberSequenceCommand.fromRequest(request);
        CreateHotelInvoiceNumberSequenceMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody UpdateCodeAndValuerRequest request){
        UpdateCodeAndValueCommand command = new UpdateCodeAndValueCommand(request.getHotels(), request.getTradings());
        mediator.send(command);

        return ResponseEntity.ok("OK");
    }

    @GetMapping(path = "/hotel/{code}/invoice-type/{invoiceType}")
    public ResponseEntity<?> getByhotelCode(@PathVariable String code, @PathVariable EInvoiceType invoiceType) {

        FindHotelInvoiceNumberSequenceByIdQuery query = new FindHotelInvoiceNumberSequenceByIdQuery(code, invoiceType);
        HotelInvoiceNumberSequenceResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchHotelInvoiceNumberSequenceQuery query = new GetSearchHotelInvoiceNumberSequenceQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/trading/{code}/invoice-type/{invoiceType}")
    public ResponseEntity<?> getByTradingCode(@PathVariable String code, @PathVariable EInvoiceType invoiceType) {

        FindHotelInvoiceNumberSequenceByTradingCodeQuery query = new FindHotelInvoiceNumberSequenceByTradingCodeQuery(code, invoiceType);
        HotelInvoiceNumberSequenceResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/run")
    public ResponseEntity<?> getByRun() {

        CreateHotelInvoiceNumberSequenceRunCommand query = new CreateHotelInvoiceNumberSequenceRunCommand();
        mediator.send(query);

        return ResponseEntity.ok("OK");
    }

}