package com.kynsoft.finamer.creditcard.controllers;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.hotelPayment.addTransactions.AddHotelPaymentTransactionsCommand;
import com.kynsoft.finamer.creditcard.application.command.hotelPayment.addTransactions.AddHotelPaymentTransactionsMessage;
import com.kynsoft.finamer.creditcard.application.command.hotelPayment.addTransactions.AddHotelPaymentTransactionsRequest;
import com.kynsoft.finamer.creditcard.application.command.hotelPayment.create.CreateHotelPaymentCommand;
import com.kynsoft.finamer.creditcard.application.command.hotelPayment.create.CreateHotelPaymentMessage;
import com.kynsoft.finamer.creditcard.application.command.hotelPayment.create.CreateHotelPaymentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
