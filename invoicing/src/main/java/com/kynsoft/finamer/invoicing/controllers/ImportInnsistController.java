package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.kafka.entity.importInnsist.ImportInnsistKafka;
import com.kynsoft.finamer.invoicing.infrastructure.services.ImportInnsistServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/import-innsist")
public class ImportInnsistController {

    private final ImportInnsistServiceImpl service;
    public ImportInnsistController(ImportInnsistServiceImpl service) {
        this.service = service;
    }

    @PostMapping("")
    public ResponseEntity<?> search(@RequestBody ImportInnsistKafka request) {
        this.service.createInvoiceFromGroupedBooking(request);
        return ResponseEntity.ok("okok");
    }
}
