package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistKafka;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.importInnsistBooking.ImportBookingFromInnsistCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.importInnsistBooking.ImportBookingFromInnsistMessage;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.importInnsistBooking.ImportInnsistRequest;
import com.kynsoft.finamer.invoicing.infrastructure.services.ImportInnsistServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/import-innsist")
public class ImportInnsistController {

    private final IMediator mediator;
    //private final ImportInnsistServiceImpl service;

    public ImportInnsistController(ImportInnsistServiceImpl service,
                                   IMediator mediator) {
        //this.service = service;
        this.mediator = mediator;
    }

    /*
    @PostMapping("")
    public ResponseEntity<?> search(@RequestBody ImportInnsistKafka request) {
        this.service.createInvoiceFromGroupedBooking(request);
        return ResponseEntity.ok("okok");
    }*/

    @PostMapping("/import")
    public ResponseEntity<?> importFromInnsist(@RequestBody ImportInnsistRequest request){
        ImportBookingFromInnsistCommand command = ImportBookingFromInnsistCommand.fromRequest(request);
        ImportBookingFromInnsistMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
