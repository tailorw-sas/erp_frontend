package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageChargeType.create.CreateManagerChargeTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageChargeType.create.CreateManagerChargeTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageChargeType.create.CreateManagerChargeTypeRequest;
import com.kynsoft.finamer.settings.application.command.manageChargeType.delete.DeleteManagerChargeTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageChargeType.delete.DeleteManagerChargeTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageChargeType.update.UpdateManagerChargeTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageChargeType.update.UpdateManagerChargeTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageChargeType.update.UpdateManagerChargeTypeRequest;
import com.kynsoft.finamer.settings.application.query.managerChargeType.getById.FindManagerChargeTypeByIdQuery;
import com.kynsoft.finamer.settings.application.query.managerChargeType.search.GetSearchManagerChargeTypeQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerChargeTypeResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-charge-type")
public class ManagerChargeTypeController {

    private final IMediator mediator;

    public ManagerChargeTypeController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManagerChargeTypeMessage> create(@RequestBody CreateManagerChargeTypeRequest request) {
        CreateManagerChargeTypeCommand createCommand = CreateManagerChargeTypeCommand.fromRequest(request);
        CreateManagerChargeTypeMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManagerChargeTypeByIdQuery query = new FindManagerChargeTypeByIdQuery(id);
        ManagerChargeTypeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManagerChargeTypeCommand command = new DeleteManagerChargeTypeCommand(id);
        DeleteManagerChargeTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManagerChargeTypeQuery query = new GetSearchManagerChargeTypeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManagerChargeTypeRequest request) {

        UpdateManagerChargeTypeCommand command = UpdateManagerChargeTypeCommand.fromRequest(request, id);
        UpdateManagerChargeTypeMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
