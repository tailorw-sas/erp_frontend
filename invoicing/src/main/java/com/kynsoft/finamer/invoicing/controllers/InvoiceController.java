package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create.CreateAdjustmentCommand;
import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create.CreateAdjustmentMessage;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingMessage;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.create.CreateInvoiceCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.create.CreateInvoiceMessage;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.create.CreateInvoiceRequest;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.createBulk.CreateBulkInvoiceCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.createBulk.CreateBulkInvoiceMessage;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.createBulk.CreateBulkInvoiceRequest;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.delete.DeleteInvoiceCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.delete.DeleteInvoiceMessage;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.UpdateInvoiceCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.UpdateInvoiceMessage;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.UpdateInvoiceRequest;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create.CreateRoomRateCommand;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create.CreateRoomRateMessage;
import com.kynsoft.finamer.invoicing.application.query.manageInvoice.getById.FindInvoiceByIdQuery;
import com.kynsoft.finamer.invoicing.application.query.manageInvoice.search.GetSearchInvoiceQuery;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageInvoiceResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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

        return ResponseEntity.ok(response);
    }

    @PostMapping("bulk")

    public ResponseEntity<CreateBulkInvoiceMessage> createBulk(@RequestBody CreateBulkInvoiceRequest request) {

        CreateBulkInvoiceCommand command = CreateBulkInvoiceCommand.fromRequest(request);
        CreateInvoiceMessage message = this.mediator.send(command.getInvoiceCommand());

        List<CreateBookingMessage> bookingResponse = new LinkedList<>();
        List<CreateRoomRateMessage> roomRateMessages = new LinkedList<>();
        List<CreateAdjustmentMessage> adjustmentMessages = new LinkedList<>();

        for (int i = 0; i < command.getBookingCommands().size(); i++) {

            CreateBookingCommand bookingCommand = command.getBookingCommands().get(i);

            CreateBookingMessage resp = this.mediator.send(bookingCommand);

            bookingResponse.add(resp);

        }

        for (int i = 0; i < command.getRoomRateCommands().size(); i++) {

            CreateRoomRateCommand roomRateCommand = command.getRoomRateCommands().get(i);

            CreateRoomRateMessage resp = this.mediator.send(roomRateCommand);
            roomRateMessages.add(resp);

        }

        for (int i = 0; i < command.getAdjustmentCommands().size(); i++) {

            CreateAdjustmentCommand adjustmentCommand = command.getAdjustmentCommands().get(i);

            CreateAdjustmentMessage resp = this.mediator.send(adjustmentCommand);
            adjustmentMessages.add(resp);

        }

        return ResponseEntity.ok(new CreateBulkInvoiceMessage(
                message.getId(), bookingResponse, roomRateMessages, adjustmentMessages));

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
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize())
                .withSort(Sort.by("createdAt").ascending());

        GetSearchInvoiceQuery query = new GetSearchInvoiceQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateInvoiceRequest request) {

        UpdateInvoiceCommand command = UpdateInvoiceCommand.fromRequest(request, id);
        UpdateInvoiceMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
