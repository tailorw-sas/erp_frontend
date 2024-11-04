package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.income.create.CreateIncomeCommand;
import com.kynsoft.finamer.invoicing.application.command.income.create.CreateIncomeMessage;
import com.kynsoft.finamer.invoicing.application.command.income.create.CreateIncomeRequest;
import com.kynsoft.finamer.invoicing.application.command.income.delete.DeleteIncomeCommand;
import com.kynsoft.finamer.invoicing.application.command.income.delete.DeleteIncomeMessage;
import com.kynsoft.finamer.invoicing.application.command.income.update.UpdateIncomeCommand;
import com.kynsoft.finamer.invoicing.application.command.income.update.UpdateIncomeMessage;
import com.kynsoft.finamer.invoicing.application.command.income.update.UpdateIncomeRequest;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.update.UpdateInvoiceCommand;
import com.kynsoft.finamer.invoicing.application.query.income.getById.FindIncomeByIdQuery;
import com.kynsoft.finamer.invoicing.application.query.income.search.GetSearchIncomeQuery;
import com.kynsoft.finamer.invoicing.application.query.manageInvoice.getById.FindInvoiceByIdQuery;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.IncomeResponse;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageInvoiceResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/income")
public class IncomeController {

    private final IMediator mediator;

    public IncomeController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateIncomeMessage> create(@RequestBody CreateIncomeRequest request) {
        CreateIncomeCommand createCommand = CreateIncomeCommand.fromRequest(request);
        CreateIncomeMessage response = mediator.send(createCommand);

        FindInvoiceByIdQuery query = new FindInvoiceByIdQuery(response.getId());
        ManageInvoiceResponse resp = mediator.send(query);

//        this.mediator.send(new UpdateInvoiceCommand(response.getId(), null, null, null, null));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteIncomeCommand command = new DeleteIncomeCommand(id);
        DeleteIncomeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateIncomeRequest request) {

        UpdateIncomeCommand command = UpdateIncomeCommand.fromRequest(request, id);
        UpdateIncomeMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindIncomeByIdQuery query = new FindIncomeByIdQuery(id);
        IncomeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchIncomeQuery query = new GetSearchIncomeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
