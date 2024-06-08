package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.managePaymentSource.create.CreateManagePaymentSourceCommand;
import com.kynsoft.finamer.settings.application.command.managePaymentSource.create.CreateManagePaymentSourceMessage;
import com.kynsoft.finamer.settings.application.command.managePaymentSource.create.CreateManagePaymentSourceRequest;
import com.kynsoft.finamer.settings.application.command.managePaymentSource.delete.DeleteManagePaymentSourceCommand;
import com.kynsoft.finamer.settings.application.command.managePaymentSource.delete.DeleteManagePaymentSourceMessage;
import com.kynsoft.finamer.settings.application.command.managePaymentSource.update.UpdateManagePaymentSourceCommand;
import com.kynsoft.finamer.settings.application.command.managePaymentSource.update.UpdateManagePaymentSourceMessage;
import com.kynsoft.finamer.settings.application.command.managePaymentSource.update.UpdateManagePaymentSourceRequest;
import com.kynsoft.finamer.settings.application.query.managePaymentResource.getById.FindManagePaymentSourceByIdQuery;
import com.kynsoft.finamer.settings.application.query.managePaymentResource.search.GetSearchPaymentSourceQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagePaymentSourceResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-payment-source")
public class ManagePaymentSourceController {

    private final IMediator mediator;

    public ManagePaymentSourceController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody CreateManagePaymentSourceRequest request) {
        CreateManagePaymentSourceCommand createCommand = CreateManagePaymentSourceCommand.fromRequest(request);
        CreateManagePaymentSourceMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManagePaymentSourceByIdQuery query = new FindManagePaymentSourceByIdQuery(id);
        ManagePaymentSourceResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManagePaymentSourceCommand command = new DeleteManagePaymentSourceCommand(id);
        DeleteManagePaymentSourceMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchPaymentSourceQuery query = new GetSearchPaymentSourceQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManagePaymentSourceRequest request) {

        UpdateManagePaymentSourceCommand command = UpdateManagePaymentSourceCommand.fromRequest(request, id);
        UpdateManagePaymentSourceMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
