package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.managerTimeZone.create.CreateManagerTimeZoneCommand;
import com.kynsoft.finamer.settings.application.command.managerTimeZone.create.CreateManagerTimeZoneMessage;
import com.kynsoft.finamer.settings.application.command.managerTimeZone.create.CreateManagerTimeZoneRequest;
import com.kynsoft.finamer.settings.application.command.managerTimeZone.delete.DeleteManagerTimeZoneCommand;
import com.kynsoft.finamer.settings.application.command.managerTimeZone.delete.DeleteManagerTimeZoneMessage;
import com.kynsoft.finamer.settings.application.command.managerTimeZone.update.UpdateManagerTimeZoneCommand;
import com.kynsoft.finamer.settings.application.command.managerTimeZone.update.UpdateManagerTimeZoneMessage;
import com.kynsoft.finamer.settings.application.command.managerTimeZone.update.UpdateManagerTimeZoneRequest;
import com.kynsoft.finamer.settings.application.query.managerTimeZone.getById.FindManagerTimeZoneByIdQuery;
import com.kynsoft.finamer.settings.application.query.managerTimeZone.search.GetSearchManagerTimeZoneQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerTimeZoneResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-time-zone")
public class ManagerTimeZoneController {

    private final IMediator mediator;

    public ManagerTimeZoneController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManagerTimeZoneMessage> create(@RequestBody CreateManagerTimeZoneRequest request) {
        CreateManagerTimeZoneCommand createCommand = CreateManagerTimeZoneCommand.fromRequest(request);
        CreateManagerTimeZoneMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManagerTimeZoneByIdQuery query = new FindManagerTimeZoneByIdQuery(id);
        ManagerTimeZoneResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManagerTimeZoneCommand command = new DeleteManagerTimeZoneCommand(id);
        DeleteManagerTimeZoneMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManagerTimeZoneQuery query = new GetSearchManagerTimeZoneQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManagerTimeZoneRequest request) {

        UpdateManagerTimeZoneCommand command = UpdateManagerTimeZoneCommand.fromRequest(request, id);
        UpdateManagerTimeZoneMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
