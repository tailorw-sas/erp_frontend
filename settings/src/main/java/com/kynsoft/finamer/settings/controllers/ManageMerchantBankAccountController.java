package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageMerchantBankAccount.create.CreateManageMerchantBankAccountCommand;
import com.kynsoft.finamer.settings.application.command.manageMerchantBankAccount.create.CreateManageMerchantBankAccountMessage;
import com.kynsoft.finamer.settings.application.command.manageMerchantBankAccount.create.CreateManageMerchantBankAccountRequest;
import com.kynsoft.finamer.settings.application.command.manageMerchantBankAccount.delete.DeleteManageMerchantBankAccountCommand;
import com.kynsoft.finamer.settings.application.command.manageMerchantBankAccount.delete.DeleteManageMerchantBankAccountMessage;
import com.kynsoft.finamer.settings.application.command.manageMerchantBankAccount.update.UpdateManageMerchantBankAccountCommand;
import com.kynsoft.finamer.settings.application.command.manageMerchantBankAccount.update.UpdateManageMerchantBankAccountMessage;
import com.kynsoft.finamer.settings.application.command.manageMerchantBankAccount.update.UpdateManageMerchantBankAccountRequest;
import com.kynsoft.finamer.settings.application.query.manageMerchantBankAccount.getById.FindManageMerchantBankAccountByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageMerchantBankAccount.search.GetSearchManageMerchantBankAccountQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageMerchantBankAccountResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-merchant-bank-account")
public class ManageMerchantBankAccountController {

    private final IMediator mediator;

    public ManageMerchantBankAccountController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManageMerchantBankAccountMessage> create(@RequestBody CreateManageMerchantBankAccountRequest request) {
        CreateManageMerchantBankAccountCommand createCommand = CreateManageMerchantBankAccountCommand.fromRequest(request);
        CreateManageMerchantBankAccountMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManageMerchantBankAccountByIdQuery query = new FindManageMerchantBankAccountByIdQuery(id);
        ManageMerchantBankAccountResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManageMerchantBankAccountCommand command = new DeleteManageMerchantBankAccountCommand(id);
        DeleteManageMerchantBankAccountMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageMerchantBankAccountQuery query = new GetSearchManageMerchantBankAccountQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageMerchantBankAccountRequest request) {

        UpdateManageMerchantBankAccountCommand command = UpdateManageMerchantBankAccountCommand.fromRequest(request, id);
        UpdateManageMerchantBankAccountMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
