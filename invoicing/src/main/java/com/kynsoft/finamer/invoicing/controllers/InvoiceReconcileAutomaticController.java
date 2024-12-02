package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.invoiceReconcilePdf.InvoiceReconcilePdfCommand;
import com.kynsoft.finamer.invoicing.application.command.invoiceReconcilePdf.InvoiceReconcilePdfMessage;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileAuto.InvoiceReconcileAutomaticCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileAuto.InvoiceReconcileAutomaticRequest;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.automatic.InvoiceReconcileAutomaticImportProcessStatusQuery;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.automatic.InvoiceReconcileAutomaticImportProcessStatusRequest;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError.automatic.InvoiceReconcileAutomaticImportErrorQuery;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError.automatic.InvoiceReconcileAutomaticImportErrorRequest;
import org.aspectj.bridge.IMessage;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/manage-invoice")
public class InvoiceReconcileAutomaticController {

    private final IMediator mediator;

    public InvoiceReconcileAutomaticController(IMediator mediator) {
        this.mediator = mediator;

    }

    @PostMapping(value = "/import-reconcile-auto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<?>> importReconcileAutomaticFromFile(@RequestPart("file") FilePart filePart,
                                                                    @RequestPart("importProcessId") String importProcessId,
                                                                    @RequestPart("employee") String employee,
                                                                    @RequestPart("employeeId") String employeeId,
                                                                    @RequestPart("invoiceIds") String invoiceIdString

    ) {
        // Dividir los IDs de facturas
        String[] invoiceIds = invoiceIdString != null ? invoiceIdString.split(",") : new String[0];

        // Se retira el "RETURN" DataBufferUtils.join(filePart.content()) hasta que se arregle lo de JasperReports
        return DataBufferUtils.join(filePart.content())
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    InvoiceReconcileAutomaticRequest request = new InvoiceReconcileAutomaticRequest(importProcessId, employeeId, employee, invoiceIds, bytes);
                    InvoiceReconcileAutomaticCommand command = new InvoiceReconcileAutomaticCommand(request);
                    try {
                        IMessage message = mediator.send(command);
                        return Mono.just(message);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                })
                .flatMap(message -> {
                    byte[] pdfBytes = new byte[1024 * 1024];
                    try {
                        InvoiceReconcilePdfCommand command = new InvoiceReconcilePdfCommand(invoiceIds, pdfBytes);
                        InvoiceReconcilePdfMessage request = this.mediator.send(command);
                        byte[] pdfData = request.getPdfData();
                        return Mono.just(
                                ResponseEntity.ok()
                                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=booking.pdf")
                                        .contentType(MediaType.APPLICATION_PDF)
                                        .body(pdfData)
                        );
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
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

    //Call to create a PDF for the invoiceUuid (No borrar por el momento para test el print pdf)
  /*  @PostMapping(path = "/import-reconcile-to-pdf")
    public ResponseEntity<byte[]> importReconcilePDF(@RequestBody List<TestInvoicePdfDto> invoicings) throws IOException {

*//*        byte[] buffer = new byte[1024 * 1024];
       InvoiceReconcilePdfCommand command = new InvoiceReconcilePdfCommand(id, buffer);
       InvoiceReconcilePdfMessage  request= this.mediator.send(command);*//*

        try {
            byte[] pdfBytes = testPdfService.concatenateMyPDFs(invoicings);

            // Configura la respuesta HTTP con el contenido PDF
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=booking.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }

    }*/
}