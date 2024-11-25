package com.kynsoft.finamer.creditcard.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageHotelPaymentStatus.create.CreateManageHotelPaymentStatusCommand;
import com.kynsoft.finamer.creditcard.application.command.manageHotelPaymentStatus.create.CreateManageHotelPaymentStatusMessage;
import com.kynsoft.finamer.creditcard.application.command.manageHotelPaymentStatus.create.CreateManageHotelPaymentStatusRequest;
import com.kynsoft.finamer.creditcard.application.command.manageHotelPaymentStatus.delete.DeleteManageHotelPaymentStatusCommand;
import com.kynsoft.finamer.creditcard.application.command.manageHotelPaymentStatus.delete.DeleteManageHotelPaymentStatusMessage;
import com.kynsoft.finamer.creditcard.application.command.manageHotelPaymentStatus.update.UpdateManageHotelPaymentStatusCommand;
import com.kynsoft.finamer.creditcard.application.command.manageHotelPaymentStatus.update.UpdateManageHotelPaymentStatusMessage;
import com.kynsoft.finamer.creditcard.application.command.manageHotelPaymentStatus.update.UpdateManageHotelPaymentStatusRequest;
import com.kynsoft.finamer.creditcard.application.query.manageHotelPaymentStatus.getById.FindManageHotelPaymentStatusByIdQuery;
import com.kynsoft.finamer.creditcard.application.query.manageHotelPaymentStatus.search.GetSearchManageHotelPaymentStatusQuery;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageHotelPaymentStatusResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-hotel-payment-status")
public class ManageHotelPaymentStatusController {

    private final IMediator mediator;

    public ManageHotelPaymentStatusController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody CreateManageHotelPaymentStatusRequest request) {
        CreateManageHotelPaymentStatusCommand createCommand = CreateManageHotelPaymentStatusCommand.fromRequest(request);
        CreateManageHotelPaymentStatusMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageHotelPaymentStatusByIdQuery query = new FindManageHotelPaymentStatusByIdQuery(id);
        ManageHotelPaymentStatusResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageHotelPaymentStatusCommand command = new DeleteManageHotelPaymentStatusCommand(id);
        DeleteManageHotelPaymentStatusMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageHotelPaymentStatusQuery query = new GetSearchManageHotelPaymentStatusQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageHotelPaymentStatusRequest request) {
        UpdateManageHotelPaymentStatusCommand command = UpdateManageHotelPaymentStatusCommand.fromRequest(request, id);
        UpdateManageHotelPaymentStatusMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
