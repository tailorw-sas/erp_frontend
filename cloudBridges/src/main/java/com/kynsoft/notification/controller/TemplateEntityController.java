package com.kynsoft.notification.controller;

import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.notification.application.command.templateEntity.create.CreateTemplateEntityCommand;
import com.kynsoft.notification.application.command.templateEntity.create.CreateTemplateEntityMessage;
import com.kynsoft.notification.application.command.templateEntity.create.CreateTemplateEntityRequest;
import com.kynsoft.notification.application.command.templateEntity.delete.DeleteTemplateEntityCommand;
import com.kynsoft.notification.application.command.templateEntity.delete.DeleteTemplateEntityMessage;
import com.kynsoft.notification.application.command.templateEntity.update.UpdateTemplateEntityCommand;
import com.kynsoft.notification.application.command.templateEntity.update.UpdateTemplateEntityMessage;
import com.kynsoft.notification.application.command.templateEntity.update.UpdateTemplateEntityRequest;
import com.kynsoft.notification.application.query.templateEntity.getById.FindTemplateEntityByIdQuery;
import com.kynsoft.notification.application.query.templateEntity.getById.TemplateEntityResponse;
import com.kynsoft.notification.application.query.templateEntity.search.GetSearchTemplateEntityQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/template")
public class TemplateEntityController {

    private final IMediator mediator;

    public TemplateEntityController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<CreateTemplateEntityMessage> create(@RequestBody CreateTemplateEntityRequest request) {
        CreateTemplateEntityCommand createCommand = CreateTemplateEntityCommand.fromRequest(request);
        CreateTemplateEntityMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<UpdateTemplateEntityMessage> update(@PathVariable UUID id, @RequestBody UpdateTemplateEntityRequest request) {
        UpdateTemplateEntityCommand command = UpdateTemplateEntityCommand.fromRequest(id, request);
        UpdateTemplateEntityMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());
        GetSearchTemplateEntityQuery query = new GetSearchTemplateEntityQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<TemplateEntityResponse> getById(@PathVariable UUID id) {

        FindTemplateEntityByIdQuery query = new FindTemplateEntityByIdQuery(id);
        TemplateEntityResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {

        DeleteTemplateEntityCommand query = new DeleteTemplateEntityCommand(id);
        DeleteTemplateEntityMessage response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

}
