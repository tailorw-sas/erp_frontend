package com.kynsoft.finamer.creditcard.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.hotelPayment.addTransactions.AddHotelPaymentTransactionsCommand;
import com.kynsoft.finamer.creditcard.application.command.hotelPayment.addTransactions.AddHotelPaymentTransactionsMessage;
import com.kynsoft.finamer.creditcard.application.command.hotelPayment.addTransactions.AddHotelPaymentTransactionsRequest;
import com.kynsoft.finamer.creditcard.application.command.hotelPayment.create.CreateHotelPaymentCommand;
import com.kynsoft.finamer.creditcard.application.command.hotelPayment.create.CreateHotelPaymentMessage;
import com.kynsoft.finamer.creditcard.application.command.hotelPayment.create.CreateHotelPaymentRequest;
import com.kynsoft.finamer.creditcard.application.query.hotelPayment.search.GetSearchHotelPaymentQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotel-payment")
public class HotelPaymentController {

    private final IMediator mediator;

    public HotelPaymentController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody CreateHotelPaymentRequest request) {
        CreateHotelPaymentCommand createCommand = CreateHotelPaymentCommand.fromRequest(request);
        CreateHotelPaymentMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-transactions")
    public ResponseEntity<?> addTransactions(@RequestBody AddHotelPaymentTransactionsRequest request) {

        AddHotelPaymentTransactionsCommand command = AddHotelPaymentTransactionsCommand.fromRequest(request);
        AddHotelPaymentTransactionsMessage response = this.mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchHotelPaymentQuery query = new GetSearchHotelPaymentQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
