package com.kynsoft.finamer.creditcard.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.addTransactions.AddTransactionsCommand;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.addTransactions.AddTransactionsMessage;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.addTransactions.AddTransactionsRequest;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.create.CreateBankReconciliationCommand;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.create.CreateBankReconciliationMessage;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.create.CreateBankReconciliationRequest;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.removeTransactions.RemoveReconciliationCommand;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.removeTransactions.RemoveReconciliationMessage;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.removeTransactions.RemoveReconciliationRequest;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.update.UpdateBankReconciliationCommand;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.update.UpdateBankReconciliationMessage;
import com.kynsoft.finamer.creditcard.application.command.manageBankReconciliation.update.UpdateBankReconciliationRequest;
import com.kynsoft.finamer.creditcard.application.query.manageBankReconciliation.ManageBankReconciliationResponse;
import com.kynsoft.finamer.creditcard.application.query.manageBankReconciliation.getById.FindManageBankReconciliationByIdQuery;
import com.kynsoft.finamer.creditcard.application.query.manageBankReconciliation.search.GetSearchManageBankReconciliationQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/bank-reconciliation")
public class ManageBankReconciliationController {

    private final IMediator mediator;

    public ManageBankReconciliationController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody CreateBankReconciliationRequest request) {
        CreateBankReconciliationCommand createCommand = CreateBankReconciliationCommand.fromRequest(request);
        CreateBankReconciliationMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManageBankReconciliationByIdQuery query = new FindManageBankReconciliationByIdQuery(id);
        ManageBankReconciliationResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageBankReconciliationQuery query = new GetSearchManageBankReconciliationQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateBankReconciliationRequest request) {

        UpdateBankReconciliationCommand command = UpdateBankReconciliationCommand.fromRequest(id, request);
        UpdateBankReconciliationMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/unbind")
    public ResponseEntity<?> unbind(@RequestBody RemoveReconciliationRequest request) {

        RemoveReconciliationCommand command = RemoveReconciliationCommand.fromRequest(request);
        RemoveReconciliationMessage response = this.mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-transactions")
    public ResponseEntity<?> addTransactions(@RequestBody AddTransactionsRequest request) {

        AddTransactionsCommand command = AddTransactionsCommand.fromRequest(request);
        AddTransactionsMessage response = this.mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
