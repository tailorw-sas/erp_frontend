package com.kynsoft.finamer.invoicing.controllers;


import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.invoiceReconcileImport.importReconcile.InvoiceReconcileImportCommand;
import com.kynsoft.finamer.invoicing.application.command.invoiceReconcileImport.importReconcile.InvoiceReconcileImportRequest;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileAuto.InvoiceReconcileAutomaticCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileAuto.InvoiceReconcileAutomaticRequest;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.InvoiceReconcileImportProcessStatusQuery;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.InvoiceReconcileImportProcessStatusRequest;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.automatic.InvoiceReconcileAutomaticImportProcessStatusQuery;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.automatic.InvoiceReconcileAutomaticImportProcessStatusRequest;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError.InvoiceReconcileImportErrorQuery;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError.InvoiceReconcileImportErrorRequest;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError.automatic.InvoiceReconcileAutomaticImportErrorQuery;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError.automatic.InvoiceReconcileAutomaticImportErrorRequest;
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
public class InvoiceReconcileAutomaticController {

    private final IMediator mediator;

    public InvoiceReconcileAutomaticController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("/import-reconcile-auto")
    public ResponseEntity<?> importReconcileAutomaticFromFile(@RequestPart("files") FilePart files,
                                                     @RequestPart("importProcessId") String importProcessId,
                                                     @RequestPart("employee") String employee,
                                                     @RequestPart("employeeId") String employeeId
    ) {
        InvoiceReconcileAutomaticRequest request = new InvoiceReconcileAutomaticRequest(importProcessId, employee, employeeId,);
        InvoiceReconcileAutomaticCommand command = new InvoiceReconcileAutomaticCommand(request);
        mediator.send(command);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/import-search-auto")
    public ResponseEntity<?> getImportReconcileAutomaticError(@RequestBody SearchRequest searchRequest) {
        InvoiceReconcileAutomaticImportErrorRequest request = new InvoiceReconcileAutomaticImportErrorRequest(searchRequest.getQuery(),
                PageRequest.of(searchRequest.getPage(), searchRequest.getPageSize()));
        InvoiceReconcileAutomaticImportErrorQuery query = new InvoiceReconcileAutomaticImportErrorQuery(request);
        return ResponseEntity.ok(mediator.send(query));
    }

    @GetMapping(path = "/{importProcessId}/import-status-auto")
    public ResponseEntity<?> getImportReconcileAutomaticProcessStatus(@PathVariable("importProcessId") String importProcessId) {
        InvoiceReconcileAutomaticImportProcessStatusRequest request = new InvoiceReconcileAutomaticImportProcessStatusRequest(importProcessId);
        InvoiceReconcileAutomaticImportProcessStatusQuery query = new InvoiceReconcileAutomaticImportProcessStatusQuery(request);
        return ResponseEntity.ok(mediator.send(query));
    }
}
