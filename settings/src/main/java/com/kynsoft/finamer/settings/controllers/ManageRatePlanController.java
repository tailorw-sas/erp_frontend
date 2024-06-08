package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageRatePlan.create.CreateManageRatePlanCommand;
import com.kynsoft.finamer.settings.application.command.manageRatePlan.create.CreateManageRatePlanMessage;
import com.kynsoft.finamer.settings.application.command.manageRatePlan.create.CreateManageRatePlanRequest;
import com.kynsoft.finamer.settings.application.command.manageRatePlan.delete.DeleteManageRatePlanCommand;
import com.kynsoft.finamer.settings.application.command.manageRatePlan.delete.DeleteManageRatePlanMessage;
import com.kynsoft.finamer.settings.application.command.manageRatePlan.update.UpdateManageRatePlanCommand;
import com.kynsoft.finamer.settings.application.command.manageRatePlan.update.UpdateManageRatePlanMessage;
import com.kynsoft.finamer.settings.application.command.manageRatePlan.update.UpdateManageRatePlanRequest;
import com.kynsoft.finamer.settings.application.query.manageRatePlan.getById.FindManageRateClientByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageRatePlan.search.GetSearchManageRatePlanQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageRatePlanResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-rate-plan")
public class ManageRatePlanController {

    private final IMediator mediator;

    public ManageRatePlanController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManageRatePlanMessage> create(@RequestBody CreateManageRatePlanRequest request) {
        CreateManageRatePlanCommand createCommand = CreateManageRatePlanCommand.fromRequest(request);
        CreateManageRatePlanMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ManageRatePlanResponse> getById(@PathVariable UUID id) {

        FindManageRateClientByIdQuery query = new FindManageRateClientByIdQuery(id);
        ManageRatePlanResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<DeleteManageRatePlanMessage> deleteById(@PathVariable UUID id) {

        DeleteManageRatePlanCommand command = new DeleteManageRatePlanCommand(id);
        DeleteManageRatePlanMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageRatePlanQuery query = new GetSearchManageRatePlanQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<UpdateManageRatePlanMessage> update(@PathVariable UUID id, @RequestBody UpdateManageRatePlanRequest request) {

        UpdateManageRatePlanCommand command = UpdateManageRatePlanCommand.fromRequest(request, id);
        UpdateManageRatePlanMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
