package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageAlerts.create.CreateAlertCommand;
import com.kynsoft.finamer.settings.application.command.manageAlerts.create.CreateAlertMessage;
import com.kynsoft.finamer.settings.application.command.manageAlerts.create.CreateAlertRequest;
import com.kynsoft.finamer.settings.application.command.manageAlerts.delete.DeleteAlertCommand;
import com.kynsoft.finamer.settings.application.command.manageAlerts.delete.DeleteAlertMessage;
import com.kynsoft.finamer.settings.application.command.manageAlerts.update.UpdateAlertCommand;
import com.kynsoft.finamer.settings.application.command.manageAlerts.update.UpdateAlertMessage;
import com.kynsoft.finamer.settings.application.query.manageAlerts.getById.FindAlertByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageAlerts.search.GetSearchAlertQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageAlertsResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-alert")
public class AlertController {

    private final IMediator mediator;

    public AlertController(final IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateAlertMessage> create(@RequestBody CreateAlertRequest request) {
        CreateAlertCommand command = CreateAlertCommand.fromRequest(request);
        CreateAlertMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindAlertByIdQuery query = new FindAlertByIdQuery(id);
        ManageAlertsResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteAlertCommand command = new DeleteAlertCommand(id);
        DeleteAlertMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchAlertQuery query = new GetSearchAlertQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody CreateAlertRequest request) {

        UpdateAlertCommand command = UpdateAlertCommand.fromRequest(request, id);
        UpdateAlertMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
