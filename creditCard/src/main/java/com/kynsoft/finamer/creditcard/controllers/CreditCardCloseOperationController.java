package com.kynsoft.finamer.creditcard.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.create.CreateCreditCardCloseOperationCommand;
import com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.create.CreateCreditCardCloseOperationMessage;
import com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.create.CreateCreditCardCloseOperationRequest;
import com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.delete.DeleteCreditCardCloseOperationCommand;
import com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.delete.DeleteCreditCardCloseOperationMessage;
import com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.update.UpdateCreditCardCloseOperationCommand;
import com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.update.UpdateCreditCardCloseOperationMessage;
import com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.update.UpdateCreditCardCloseOperationRequest;
import com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.updateAll.UpdateAllCreditCardCloseOperationCommand;
import com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.updateAll.UpdateAllCreditCardCloseOperationMessage;
import com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.updateAll.UpdateAllCreditCardCloseOperationRequest;
import com.kynsoft.finamer.creditcard.application.query.creditCardCloseOperation.getById.FindCreditCardCloseOperationByIdQuery;
import com.kynsoft.finamer.creditcard.application.query.creditCardCloseOperation.search.GetSearchCreditCardCloseOperationQuery;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.CreditCardCloseOperationResponse;

import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credit-card-close-operation")
public class CreditCardCloseOperationController {

    private final IMediator mediator;

    public CreditCardCloseOperationController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateCreditCardCloseOperationMessage> create(@RequestBody CreateCreditCardCloseOperationRequest request) {
        CreateCreditCardCloseOperationCommand createCommand = CreateCreditCardCloseOperationCommand.fromRequest(request);
        CreateCreditCardCloseOperationMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteCreditCardCloseOperationCommand command = new DeleteCreditCardCloseOperationCommand(id);
        DeleteCreditCardCloseOperationMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PutMapping(path = "/all")
    public ResponseEntity<?> update(@RequestBody UpdateAllCreditCardCloseOperationRequest request) {

        UpdateAllCreditCardCloseOperationCommand command = UpdateAllCreditCardCloseOperationCommand.fromRequest(request);
        UpdateAllCreditCardCloseOperationMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateCreditCardCloseOperationRequest request) {

        UpdateCreditCardCloseOperationCommand command = UpdateCreditCardCloseOperationCommand.fromRequest(request, id);
        UpdateCreditCardCloseOperationMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindCreditCardCloseOperationByIdQuery query = new FindCreditCardCloseOperationByIdQuery(id);
        CreditCardCloseOperationResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchCreditCardCloseOperationQuery query = new GetSearchCreditCardCloseOperationQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
