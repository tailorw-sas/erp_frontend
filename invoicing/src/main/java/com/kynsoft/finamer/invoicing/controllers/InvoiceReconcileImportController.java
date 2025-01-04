package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.invoiceReconcileImport.importReconcile.InvoiceReconcileImportCommand;
import com.kynsoft.finamer.invoicing.application.command.invoiceReconcileImport.importReconcile.InvoiceReconcileImportRequest;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError.InvoiceReconcileImportErrorQuery;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError.InvoiceReconcileImportErrorRequest;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.InvoiceReconcileImportProcessStatusQuery;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.InvoiceReconcileImportProcessStatusRequest;
import com.kynsof.share.core.domain.service.IStorageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/manage-invoice")
public class InvoiceReconcileImportController {

    private final IStorageService storageService;
    private final IMediator mediator;

    public InvoiceReconcileImportController(IStorageService storageService,
                                            IMediator mediator) {
        this.storageService = storageService;
        this.mediator = mediator;
    }

    @PostMapping("/import-reconcile")
    public ResponseEntity<?> importReconcileFromFile(@RequestPart("files") Flux<FilePart> files,
                                                     @RequestPart("importProcessId") String importProcessId,
                                                     @RequestPart("employee") String employee,
                                                     @RequestPart("employeeId") String employeeId
    ) {
        this.storageService.store(files, importProcessId);
        InvoiceReconcileImportRequest request = new InvoiceReconcileImportRequest(importProcessId, employee, employeeId);
        InvoiceReconcileImportCommand command = new InvoiceReconcileImportCommand(request);
        mediator.send(command);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/import-search")
    public ResponseEntity<?> getImportReconcileError(@RequestBody SearchRequest searchRequest) {
        InvoiceReconcileImportErrorRequest request = new InvoiceReconcileImportErrorRequest(searchRequest.getQuery(),
                PageRequest.of(searchRequest.getPage(), searchRequest.getPageSize()));
        InvoiceReconcileImportErrorQuery query = new InvoiceReconcileImportErrorQuery(request);
        return ResponseEntity.ok(mediator.send(query));
    }

    @GetMapping(path = "/{importProcessId}/import-status")
    public ResponseEntity<?> getImportReconcileProcessStatus(@PathVariable("importProcessId") String importProcessId) {
        InvoiceReconcileImportProcessStatusRequest request = new InvoiceReconcileImportProcessStatusRequest(importProcessId);
        InvoiceReconcileImportProcessStatusQuery query = new InvoiceReconcileImportProcessStatusQuery(request);
        return ResponseEntity.ok(mediator.send(query));
    }
}
