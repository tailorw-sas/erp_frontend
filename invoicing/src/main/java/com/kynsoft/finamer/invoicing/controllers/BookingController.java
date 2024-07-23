package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingMessage;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.create.CreateBookingRequest;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.createMany.CreateManyBookingMessage;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.createMany.CreateManyBookingRequest;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.createMany.CreateManyBookingsCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.delete.DeleteBookingCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.delete.DeleteBookingMessage;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.importbooking.ImportBookingFromFileCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.update.UpdateBookingCommand;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.update.UpdateBookingMessage;
import com.kynsoft.finamer.invoicing.application.command.manageBooking.update.UpdateBookingRequest;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create.CreateRoomRateCommand;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create.CreateRoomRateMessage;
import com.kynsoft.finamer.invoicing.application.query.manageBooking.getById.FindBookingByIdQuery;
import com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking.ImportBookingErrorRequest;
import com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking.ImportBookingFromFileErrorQuery;
import com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking.ImportBookingProcessStatusQuery;
import com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking.ImportBookingProcessStatusRequest;
import com.kynsoft.finamer.invoicing.application.query.manageBooking.search.GetSearchBookingQuery;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageBookingResponse;
import com.kynsoft.finamer.invoicing.domain.excel.ImportBookingRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-booking")
public class BookingController {

    private final IMediator mediator;

    public BookingController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateBookingMessage> create(@RequestBody CreateBookingRequest request) {
        CreateBookingCommand createCommand = CreateBookingCommand.fromRequest(request);
        CreateBookingMessage response = mediator.send(createCommand);
        CreateRoomRateCommand createRoomRateCommand = new CreateRoomRateCommand(request.getCheckIn(),
                request.getCheckOut(), request.getInvoiceAmount(), request.getRoomNumber(), request.getAdults(),
                request.getChildren(), request.getRateAdult(), request.getRateChild(), request.getHotelAmount(),
                request.getDescription(), response.getId(), UUID.randomUUID());
        CreateRoomRateMessage roomRateMessage = mediator.send(createRoomRateCommand);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/many")
    public ResponseEntity<CreateManyBookingMessage> createMany(@RequestBody CreateManyBookingRequest request) {
        CreateManyBookingsCommand createCommand = CreateManyBookingsCommand.fromRequest(request);
        CreateManyBookingMessage response = mediator.send(createCommand);

        for (int i = 0; i < createCommand.getBookings().size(); i++) {
            CreateBookingCommand element = createCommand.getBookings().get(i);
            CreateRoomRateCommand createRoomRateCommand = new CreateRoomRateCommand(element.getCheckIn(),
                    element.getCheckOut(), element.getInvoiceAmount(), element.getRoomNumber(), element.getAdults(),
                    element.getChildren(), element.getRateAdult(), element.getRateChild(), element.getHotelAmount(),
                    element.getDescription(), element.getId(), UUID.randomUUID());
            mediator.send(createRoomRateCommand);

        }

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindBookingByIdQuery query = new FindBookingByIdQuery(id);
        ManageBookingResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteBookingCommand command = new DeleteBookingCommand(id);
        DeleteBookingMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchBookingQuery query = new GetSearchBookingQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateBookingRequest request) {

        UpdateBookingCommand command = UpdateBookingCommand.fromRequest(request, id);
        UpdateBookingMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/import")
    public ResponseEntity<?> importBooking(@RequestBody ImportBookingRequest request) {
        ImportBookingFromFileCommand importBookingFromFileCommand = new ImportBookingFromFileCommand(request);
        return ResponseEntity.ok(mediator.send(importBookingFromFileCommand));
    }

    @PostMapping(path = "/import-search")
    public ResponseEntity<?> getImportBookingError(@RequestBody SearchRequest searchRequest) {
        ImportBookingErrorRequest request = new ImportBookingErrorRequest(searchRequest.getQuery(), PageRequest.of(searchRequest.getPage(), searchRequest.getPageSize()));
        ImportBookingFromFileErrorQuery importBookingFromFileErrorQuery = new ImportBookingFromFileErrorQuery(request);
        return ResponseEntity.ok(mediator.send(importBookingFromFileErrorQuery));
    }

    @GetMapping(path = "/import-status/{importProcessId}")
    public ResponseEntity<?> getImportBookingProcessStatus(@PathVariable("importProcessId") String importProcessId) {
        ImportBookingProcessStatusRequest request = new ImportBookingProcessStatusRequest(importProcessId);
        ImportBookingProcessStatusQuery importBookingProcessStatusQuery = new ImportBookingProcessStatusQuery(request);
        return ResponseEntity.ok(mediator.send(importBookingProcessStatusQuery));
    }
}
