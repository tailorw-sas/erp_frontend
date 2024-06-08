package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageAccountType.create.CreateManagerAccountTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageAccountType.create.CreateManagerAccountTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageAccountType.create.CreateManagerAccountTypeRequest;
import com.kynsoft.finamer.settings.application.command.manageAccountType.delete.DeleteManagerAccountTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageAccountType.delete.DeleteManagerAccountTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageAccountType.update.UpdateManagerAccountTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageAccountType.update.UpdateManagerAccountTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageAccountType.update.UpdateManagerAccountTypeRequest;
import com.kynsoft.finamer.settings.application.query.managerAccountType.getById.FindManagerAccountTypeByIdQuery;
import com.kynsoft.finamer.settings.application.query.managerAccountType.search.GetSearchManagerAccountTypeQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerAccountTypeResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-account-type")
public class ManagerAccountTypeController {

    private final IMediator mediator;

    public ManagerAccountTypeController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManagerAccountTypeMessage> create(@RequestBody CreateManagerAccountTypeRequest request) {
        CreateManagerAccountTypeCommand createCommand = CreateManagerAccountTypeCommand.fromRequest(request);
        CreateManagerAccountTypeMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManagerAccountTypeByIdQuery query = new FindManagerAccountTypeByIdQuery(id);
        ManagerAccountTypeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManagerAccountTypeCommand command = new DeleteManagerAccountTypeCommand(id);
        DeleteManagerAccountTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManagerAccountTypeQuery query = new GetSearchManagerAccountTypeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManagerAccountTypeRequest request) {

        UpdateManagerAccountTypeCommand command = UpdateManagerAccountTypeCommand.fromRequest(request, id);
        UpdateManagerAccountTypeMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
