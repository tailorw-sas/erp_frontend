package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.invoiceReconcileImport.InvoiceReconcileImportCommand;
import com.kynsoft.finamer.invoicing.domain.services.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/manage-invoice")
public class InvoiceReconcileImportController {

    private final StorageService storageService;
    private final IMediator mediator;

    public InvoiceReconcileImportController(StorageService storageService,
                                            IMediator mediator) {
        this.storageService = storageService;
        this.mediator = mediator;
    }

    @PostMapping("/import-reconcile")
    public Flux<ResponseEntity<?>> importReconcileFromFile(@RequestPart("files") Flux<FilePart> files,
                                                           @RequestPart("importProcessId") String importProcessId) {

        return storageService
                .store(files)
                .flatMap(other -> {
                            InvoiceReconcileImportCommand command = new InvoiceReconcileImportCommand(importProcessId);
                            return Mono.just(ResponseEntity.ok(mediator.send(command)));
                        }
                );
    }
}
