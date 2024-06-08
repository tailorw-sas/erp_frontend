package com.kynsoft.notification.controller;

import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.notification.application.command.mailjetConfiguration.create.CreateMailjetConfigurationCommand;
import com.kynsoft.notification.application.command.mailjetConfiguration.create.CreateMailjetConfigurationMessage;
import com.kynsoft.notification.application.command.mailjetConfiguration.create.CreateMailjetConfigurationRequest;
import com.kynsoft.notification.application.command.mailjetConfiguration.delete.DeleteMailjetConfigurationCommand;
import com.kynsoft.notification.application.command.mailjetConfiguration.delete.DeleteMailjetConfigurationMessage;
import com.kynsoft.notification.application.command.mailjetConfiguration.update.UpdateMailjetConfigurationCommand;
import com.kynsoft.notification.application.command.mailjetConfiguration.update.UpdateMailjetConfigurationMessage;
import com.kynsoft.notification.application.command.mailjetConfiguration.update.UpdateMailjetConfigurationRequest;
import com.kynsoft.notification.application.query.mailjetConfiguration.getById.FindMailjetConfigurationByIdQuery;
import com.kynsoft.notification.application.query.mailjetConfiguration.getById.MailjetConfigurationResponse;
import com.kynsoft.notification.application.query.mailjetConfiguration.search.GetSearchMailjetConfigurationQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/mailjet-config")
public class MailjetConfigEntityController {

    private final IMediator mediator;

    public MailjetConfigEntityController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<CreateMailjetConfigurationMessage> create(@RequestBody CreateMailjetConfigurationRequest request) {
        CreateMailjetConfigurationCommand createCommand = CreateMailjetConfigurationCommand.fromRequest(request);
        CreateMailjetConfigurationMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<UpdateMailjetConfigurationMessage> update(@PathVariable UUID id, @RequestBody UpdateMailjetConfigurationRequest request) {
        UpdateMailjetConfigurationCommand command = UpdateMailjetConfigurationCommand.fromRequest(id, request);
        UpdateMailjetConfigurationMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());
        GetSearchMailjetConfigurationQuery query = new GetSearchMailjetConfigurationQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<MailjetConfigurationResponse> getById(@PathVariable UUID id) {

        FindMailjetConfigurationByIdQuery query = new FindMailjetConfigurationByIdQuery(id);
        MailjetConfigurationResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {

        DeleteMailjetConfigurationCommand query = new DeleteMailjetConfigurationCommand(id);
        DeleteMailjetConfigurationMessage response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

}
