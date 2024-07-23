package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create.CreateAdjustmentCommand;
import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create.CreateAdjustmentMessage;
import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.create.CreateAdjustmentRequest;
import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.delete.DeleteAdjustmentCommand;
import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.delete.DeleteAdjustmentMessage;
import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.update.UpdateAdjustmentCommand;
import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.update.UpdateAdjustmentMessage;
import com.kynsoft.finamer.invoicing.application.command.manageAdjustment.update.UpdateAdjustmentRequest;
import com.kynsoft.finamer.invoicing.application.query.manageAdjustment.getById.FindAdjustmentByIdQuery;
import com.kynsoft.finamer.invoicing.application.query.manageAdjustment.search.GetSearchAdjustmentQuery;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageAdjustmentResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-adjustment")
public class AdjustmentController {

    private final IMediator mediator;

    public AdjustmentController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateAdjustmentMessage> create(@RequestBody CreateAdjustmentRequest request) {
        CreateAdjustmentCommand createCommand = CreateAdjustmentCommand.fromRequest(request);
        CreateAdjustmentMessage response = mediator.send(createCommand);

       

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindAdjustmentByIdQuery query = new FindAdjustmentByIdQuery(id);
        ManageAdjustmentResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteAdjustmentCommand command = new DeleteAdjustmentCommand(id);
        DeleteAdjustmentMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchAdjustmentQuery query = new GetSearchAdjustmentQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateAdjustmentRequest request) {

        UpdateAdjustmentCommand command = UpdateAdjustmentCommand.fromRequest(request, id);
        UpdateAdjustmentMessage response = mediator.send(command);

       

        return ResponseEntity.ok(response);
    }
}
