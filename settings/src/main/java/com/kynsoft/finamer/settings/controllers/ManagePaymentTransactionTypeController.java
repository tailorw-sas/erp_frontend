package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.request.SortTypeEnum;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;

import com.kynsoft.finamer.settings.application.command.managePaymentTransactionType.create.CreateManagePaymentTransactionTypeCommand;
import com.kynsoft.finamer.settings.application.command.managePaymentTransactionType.create.CreateManagePaymentTransactionTypeMessage;
import com.kynsoft.finamer.settings.application.command.managePaymentTransactionType.create.CreateManagePaymentTransactionTypeRequest;
import com.kynsoft.finamer.settings.application.command.managePaymentTransactionType.delete.DeleteManagePaymentTransactionTypeCommand;
import com.kynsoft.finamer.settings.application.command.managePaymentTransactionType.delete.DeleteManagePaymentTransactionTypeMessage;
import com.kynsoft.finamer.settings.application.command.managePaymentTransactionType.update.UpdateManagePaymentTransactionTypeCommand;
import com.kynsoft.finamer.settings.application.command.managePaymentTransactionType.update.UpdateManagePaymentTransactionTypeMessage;
import com.kynsoft.finamer.settings.application.command.managePaymentTransactionType.update.UpdateManagePaymentTransactionTypeRequest;
import com.kynsoft.finamer.settings.application.query.managePaymentTransactionType.getById.FindManagePaymentTransactionTypeByIdQuery;
import com.kynsoft.finamer.settings.application.query.managePaymentTransactionType.search.GetManagePaymentTransactionTypeQuery;
import com.kynsoft.finamer.settings.application.query.managePaymentTransactionType.searchForPayment.GetManagePaymentTransactionTypeForPaymentQuery;
import com.kynsoft.finamer.settings.application.query.managePaymentTransactionType.searchForPayment.SeachDefaultRequest;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagePaymentTransactionTypeResponse;
import java.util.ArrayList;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/manage-payment-transaction-type")
public class ManagePaymentTransactionTypeController {

    private final IMediator mediator;

    public ManagePaymentTransactionTypeController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody CreateManagePaymentTransactionTypeRequest request) {
        CreateManagePaymentTransactionTypeCommand createCommand = CreateManagePaymentTransactionTypeCommand.fromRequest(request);
        CreateManagePaymentTransactionTypeMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManagePaymentTransactionTypeByIdQuery query = new FindManagePaymentTransactionTypeByIdQuery(id);
        ManagePaymentTransactionTypeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManagePaymentTransactionTypeCommand command = new DeleteManagePaymentTransactionTypeCommand(id);
        DeleteManagePaymentTransactionTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetManagePaymentTransactionTypeQuery query = new GetManagePaymentTransactionTypeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/search-for-payment")
    public ResponseEntity<?> searchForPayment(@RequestBody SeachDefaultRequest request) {
        Sort sort = Sort.unsorted();
        Pageable pageable = PageRequest.of(0, 1000, sort);

        GetManagePaymentTransactionTypeForPaymentQuery query = new GetManagePaymentTransactionTypeForPaymentQuery(pageable, request.getApplyDeposit(), request.getDeposit(), request.getDefaults());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManagePaymentTransactionTypeRequest request) {

        UpdateManagePaymentTransactionTypeCommand command = UpdateManagePaymentTransactionTypeCommand.fromRequest(request, id);
        UpdateManagePaymentTransactionTypeMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
