package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageBankAccount.create.CreateManageBankAccountCommand;
import com.kynsoft.finamer.settings.application.command.manageBankAccount.create.CreateManageBankAccountMessage;
import com.kynsoft.finamer.settings.application.command.manageBankAccount.create.CreateManageBankAccountRequest;
import com.kynsoft.finamer.settings.application.command.manageBankAccount.delete.DeleteManageBankAccountCommand;
import com.kynsoft.finamer.settings.application.command.manageBankAccount.delete.DeleteManageBankAccountMessage;
import com.kynsoft.finamer.settings.application.command.manageBankAccount.update.UpdateManageBankAccountCommand;
import com.kynsoft.finamer.settings.application.command.manageBankAccount.update.UpdateManageBankAccountMessage;
import com.kynsoft.finamer.settings.application.command.manageBankAccount.update.UpdateManageBankAccountRequest;
import com.kynsoft.finamer.settings.application.query.manageBankAccount.getById.FindManageBankAccountByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageBankAccount.search.GetSearchManageBankAccountQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageBankAccountResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-bank-account")
public class ManageBankAccountController {

    private final IMediator mediator;

    public ManageBankAccountController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManageBankAccountRequest request){
        CreateManageBankAccountCommand command = CreateManageBankAccountCommand.fromRequest(request);
        CreateManageBankAccountMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageBankAccountByIdQuery query = new FindManageBankAccountByIdQuery(id);
        ManageBankAccountResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageBankAccountCommand command = new DeleteManageBankAccountCommand(id);
        DeleteManageBankAccountMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageBankAccountQuery query = new GetSearchManageBankAccountQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageBankAccountRequest request){
        UpdateManageBankAccountCommand command = UpdateManageBankAccountCommand.fromRequest(request, id);
        UpdateManageBankAccountMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
