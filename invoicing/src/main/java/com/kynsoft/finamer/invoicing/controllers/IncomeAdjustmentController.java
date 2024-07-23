package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create.CreateIncomeAdjustmentCommand;
import com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create.CreateIncomeAdjustmentMessage;
import com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.create.CreateIncomeAdjustmentRequest;
import com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.delete.DeleteIncomeAdjustmentCommand;
import com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.delete.DeleteIncomeAdjustmentMessage;
import com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.update.UpdateIncomeAdjustmentCommand;
import com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.update.UpdateIncomeAdjustmentMessage;
import com.kynsoft.finamer.invoicing.application.command.incomeAdjustment.update.UpdateIncomeAdjustmentRequest;
import com.kynsoft.finamer.invoicing.application.query.incomeAdjustment.getById.FindIncomeAdjustmentByIdQuery;
import com.kynsoft.finamer.invoicing.application.query.incomeAdjustment.search.GetSearchIncomeAdjustmentQuery;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.IncomeAdjustmentResponse;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/income-adjustment")
public class IncomeAdjustmentController {

    private final IMediator mediator;

    public IncomeAdjustmentController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateIncomeAdjustmentMessage> create(@RequestBody CreateIncomeAdjustmentRequest request) {
        CreateIncomeAdjustmentCommand createCommand = CreateIncomeAdjustmentCommand.fromRequest(request);
        CreateIncomeAdjustmentMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteIncomeAdjustmentCommand command = new DeleteIncomeAdjustmentCommand(id);
        DeleteIncomeAdjustmentMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateIncomeAdjustmentRequest request) {

        UpdateIncomeAdjustmentCommand command = UpdateIncomeAdjustmentCommand.fromRequest(request, id);
        UpdateIncomeAdjustmentMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindIncomeAdjustmentByIdQuery query = new FindIncomeAdjustmentByIdQuery(id);
        IncomeAdjustmentResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchIncomeAdjustmentQuery query = new GetSearchIncomeAdjustmentQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
