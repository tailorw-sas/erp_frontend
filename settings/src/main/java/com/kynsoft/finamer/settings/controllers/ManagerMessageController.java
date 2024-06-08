package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.managerMessage.create.CreateManagerMessageCommand;
import com.kynsoft.finamer.settings.application.command.managerMessage.create.CreateManagerMessageMessage;
import com.kynsoft.finamer.settings.application.command.managerMessage.create.CreateManagerMessageRequest;
import com.kynsoft.finamer.settings.application.command.managerMessage.delete.DeleteManagerMessageCommand;
import com.kynsoft.finamer.settings.application.command.managerMessage.delete.DeleteManagerMessageMessage;
import com.kynsoft.finamer.settings.application.command.managerMessage.update.UpdateManagerMessageCommand;
import com.kynsoft.finamer.settings.application.command.managerMessage.update.UpdateManagerMessageMessage;
import com.kynsoft.finamer.settings.application.command.managerMessage.update.UpdateManagerMessageRequest;
import com.kynsoft.finamer.settings.application.query.managerMessage.getById.FindManagerMessageByIdQuery;
import com.kynsoft.finamer.settings.application.query.managerMessage.search.GetSearchManagerMessageQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerMessageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-message")
public class ManagerMessageController {
    
    private final IMediator mediator;

    public ManagerMessageController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManagerMessageMessage> create(@RequestBody CreateManagerMessageRequest request) {
        CreateManagerMessageCommand createCommand = CreateManagerMessageCommand.fromRequest(request);
        CreateManagerMessageMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManagerMessageByIdQuery query = new FindManagerMessageByIdQuery(id);
        ManagerMessageResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManagerMessageCommand command = new DeleteManagerMessageCommand(id);
        DeleteManagerMessageMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManagerMessageQuery query = new GetSearchManagerMessageQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManagerMessageRequest request) {

        UpdateManagerMessageCommand command = UpdateManagerMessageCommand.fromRequest(request, id);
        UpdateManagerMessageMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
