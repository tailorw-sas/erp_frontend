package com.kynsoft.finamer.creditcard.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageMerchant.create.CreateManageMerchantCommand;
import com.kynsoft.finamer.creditcard.application.command.manageMerchant.create.CreateManageMerchantMessage;
import com.kynsoft.finamer.creditcard.application.command.manageMerchant.create.CreateManageMerchantRequest;
import com.kynsoft.finamer.creditcard.application.command.manageMerchant.delete.DeleteManageMerchantCommand;
import com.kynsoft.finamer.creditcard.application.command.manageMerchant.delete.DeleteManageMerchantMessage;
import com.kynsoft.finamer.creditcard.application.command.manageMerchant.update.UpdateManageMerchantCommand;
import com.kynsoft.finamer.creditcard.application.command.manageMerchant.update.UpdateManageMerchantMessage;
import com.kynsoft.finamer.creditcard.application.command.manageMerchant.update.UpdateManageMerchantRequest;
import com.kynsoft.finamer.creditcard.application.query.manageMerchant.getById.FindManageMerchantByIdQuery;
import com.kynsoft.finamer.creditcard.application.query.manageMerchant.search.GetSearchManageMerchantQuery;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageMerchantResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-merchant")
public class ManageMerchantController {

    private final IMediator mediator;

    public ManageMerchantController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManageMerchantMessage> create(@RequestBody CreateManageMerchantRequest request) {
        CreateManageMerchantCommand createCommand = CreateManageMerchantCommand.fromRequest(request);
        CreateManageMerchantMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManageMerchantByIdQuery query = new FindManageMerchantByIdQuery(id);
        ManageMerchantResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManageMerchantCommand command = new DeleteManageMerchantCommand(id);
        DeleteManageMerchantMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageMerchantQuery query = new GetSearchManageMerchantQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageMerchantRequest request) {

        UpdateManageMerchantCommand command = UpdateManageMerchantCommand.fromRequest(request, id);
        UpdateManageMerchantMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
