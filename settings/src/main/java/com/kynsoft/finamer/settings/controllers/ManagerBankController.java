package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageBank.create.CreateManagerBankCommand;
import com.kynsoft.finamer.settings.application.command.manageBank.create.CreateManagerBankMessage;
import com.kynsoft.finamer.settings.application.command.manageBank.create.CreateManagerBankRequest;
import com.kynsoft.finamer.settings.application.command.manageBank.delete.DeleteManagerBankCommand;
import com.kynsoft.finamer.settings.application.command.manageBank.delete.DeleteManagerBankMessage;
import com.kynsoft.finamer.settings.application.command.manageBank.update.UpdateManagerBankCommand;
import com.kynsoft.finamer.settings.application.command.manageBank.update.UpdateManagerBankMessage;
import com.kynsoft.finamer.settings.application.command.manageBank.update.UpdateManagerBankRequest;
import com.kynsoft.finamer.settings.application.query.managerBank.getById.FindManagerBankByIdQuery;
import com.kynsoft.finamer.settings.application.query.managerBank.search.GetSearchManagerBankQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerBankResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-bank")
public class ManagerBankController {

    private final IMediator mediator;

    public ManagerBankController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManagerBankMessage> create(@RequestBody CreateManagerBankRequest request) {
        CreateManagerBankCommand createCommand = CreateManagerBankCommand.fromRequest(request);
        CreateManagerBankMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManagerBankByIdQuery query = new FindManagerBankByIdQuery(id);
        ManagerBankResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManagerBankCommand command = new DeleteManagerBankCommand(id);
        DeleteManagerBankMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManagerBankQuery query = new GetSearchManagerBankQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManagerBankRequest request) {

        UpdateManagerBankCommand command = UpdateManagerBankCommand.fromRequest(request, id);
        UpdateManagerBankMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
