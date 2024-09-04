package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.attachmentStatusHistory.create.CreateAttachmentStatusHistoryCommand;
import com.kynsoft.finamer.invoicing.application.command.invoiceStatusHistory.create.CreateInvoiceStatusHistoryCommand;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentMessage;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.calculateInvoiceAmount.CalculateInvoiceAmountCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.create.CreateInvoiceCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.create.CreateInvoiceMessage;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.create.CreateInvoiceRequest;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.createBulk.CreateBulkInvoiceCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.createBulk.CreateBulkInvoiceMessage;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.createBulk.CreateBulkInvoiceRequest;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.delete.DeleteInvoiceCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.delete.DeleteInvoiceMessage;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.newCredit.CreateNewCreditCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.newCredit.CreateNewCreditMessage;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.newCredit.CreateNewCreditRequest;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.partialClone.PartialCloneInvoiceCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.partialClone.PartialCloneInvoiceMessage;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.partialClone.PartialCloneInvoiceRequest;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.totalClone.TotalCloneInvoiceCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.totalClone.TotalCloneInvoiceMessage;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.totalClone.TotalCloneInvoiceRequest;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.UpdateInvoiceCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.UpdateInvoiceMessage;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.UpdateInvoiceRequest;
import com.kynsoft.finamer.invoicing.application.query.manageInvoice.export.ExportInvoiceQuery;
import com.kynsoft.finamer.invoicing.application.query.manageInvoice.getById.FindInvoiceByIdQuery;
import com.kynsoft.finamer.invoicing.application.query.manageInvoice.search.GetSearchInvoiceQuery;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ExportInvoiceResponse;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageInvoiceResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/manage-invoice")
public class InvoiceController {

    private final IMediator mediator;

    public InvoiceController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateInvoiceMessage> create(@RequestBody CreateInvoiceRequest request) {
        CreateInvoiceCommand createCommand = CreateInvoiceCommand.fromRequest(request);
        CreateInvoiceMessage response = mediator.send(createCommand);

        FindInvoiceByIdQuery query = new FindInvoiceByIdQuery(response.getId());
        ManageInvoiceResponse resp = mediator.send(query);

        this.mediator.send(
                new UpdateInvoiceCommand(response.getId(), null, null, null, null, null, null, null, null, null, null));
        return ResponseEntity.ok(response);
    }

    @PostMapping("bulk")
    public ResponseEntity<CreateBulkInvoiceMessage> createBulk(@RequestBody CreateBulkInvoiceRequest request) {

        CreateBulkInvoiceCommand command = CreateBulkInvoiceCommand.fromRequest(request);

        CreateBulkInvoiceMessage message = this.mediator.send(command);

        this.mediator.send(
                new CalculateInvoiceAmountCommand(message.getId(), command.getBookingCommands().stream().map(b -> {
                    return b.getId();
                }).collect(Collectors.toList()), command.getRoomRateCommands().stream().map(rr -> {
                    return rr.getId();
                }).collect(Collectors.toList())));

        this.mediator.send(new CreateInvoiceStatusHistoryCommand(message.getId(), command.getEmployee()));

        for (CreateAttachmentMessage attachmentMessage : message.getAttachmentMessages()) {
            this.mediator.send(new CreateAttachmentStatusHistoryCommand(attachmentMessage.getId()));
        }

        return ResponseEntity.ok(message);

    }

    @PostMapping("total-clone")
    public ResponseEntity<TotalCloneInvoiceMessage> totalCloneInvoice(@RequestBody TotalCloneInvoiceRequest request) {

        TotalCloneInvoiceCommand command = TotalCloneInvoiceCommand.fromRequest(request);

        TotalCloneInvoiceMessage message = this.mediator.send(command);

        this.mediator.send(new CreateInvoiceStatusHistoryCommand(message.getId(), command.getEmployee()));

        for (CreateAttachmentMessage attachmentMessage : message.getAttachmentMessages()) {
            this.mediator.send(new CreateAttachmentStatusHistoryCommand(attachmentMessage.getId()));
        }

        return ResponseEntity.ok(message);

    }

    @PostMapping("partial-clone")
    public ResponseEntity<PartialCloneInvoiceMessage> createBulk(@RequestBody PartialCloneInvoiceRequest request) {

        PartialCloneInvoiceCommand command = PartialCloneInvoiceCommand.fromRequest(request);

        PartialCloneInvoiceMessage message = this.mediator.send(command);

        this.mediator.send(
                new CalculateInvoiceAmountCommand(message.getCloned(), command.getBookings(), command.getRoomRates()));

        this.mediator.send(new CreateInvoiceStatusHistoryCommand(message.getCloned(), command.getEmployee()));

        for (UUID attacmhment : command.getAttachments()) {
            this.mediator.send(new CreateAttachmentStatusHistoryCommand(attacmhment));
        }

        return ResponseEntity.ok(message);

    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindInvoiceByIdQuery query = new FindInvoiceByIdQuery(id);
        ManageInvoiceResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteInvoiceCommand command = new DeleteInvoiceCommand(id);
        DeleteInvoiceMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchInvoiceQuery query = new GetSearchInvoiceQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/export")
    public ResponseEntity<?> export(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        ExportInvoiceQuery query = new ExportInvoiceQuery(pageable, request.getFilter(), request.getQuery());
        ExportInvoiceResponse data = mediator.send(query);

        final byte[] bytes = data.getStream().toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateInvoiceRequest request) {

        UpdateInvoiceCommand command = UpdateInvoiceCommand.fromRequest(request, id);
        UpdateInvoiceMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/new-credit")
    public ResponseEntity<?> newCredit(@RequestBody CreateNewCreditRequest request){
        CreateNewCreditCommand command = CreateNewCreditCommand.fromRequest(request);
        CreateNewCreditMessage response = this.mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
