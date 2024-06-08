package com.kynsof.identity.controller;

import com.kynsof.identity.application.command.business.create.CreateBusinessCommand;
import com.kynsof.identity.application.command.business.create.CreateBusinessMessage;
import com.kynsof.identity.application.command.business.create.CreateBusinessRequest;
import com.kynsof.identity.application.command.business.delete.BusinessDeleteCommand;
import com.kynsof.identity.application.command.business.delete.BusinessDeleteMessage;
import com.kynsof.identity.application.command.business.update.UpdateBusinessCommand;
import com.kynsof.identity.application.command.business.update.UpdateBusinessMessage;
import com.kynsof.identity.application.command.business.update.UpdateBusinessRequest;
import com.kynsof.identity.application.query.business.getbyid.BusinessByIdResponse;
import com.kynsof.identity.application.query.business.getbyid.FindBusinessByIdQuery;
import com.kynsof.identity.application.query.business.search.GetSearchBusinessQuery;
import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/business")
public class BusinessController {

    private final IMediator mediator;

    public BusinessController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateBusinessMessage> create(@RequestBody CreateBusinessRequest request) {
        CreateBusinessCommand createCommand = CreateBusinessCommand.fromRequest(request);
        CreateBusinessMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchBusinessQuery query = new GetSearchBusinessQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindBusinessByIdQuery query = new FindBusinessByIdQuery(id);
        BusinessByIdResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {

        BusinessDeleteCommand command = new BusinessDeleteCommand(id);
        BusinessDeleteMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") UUID id, @RequestBody UpdateBusinessRequest request) {

        UpdateBusinessCommand command = UpdateBusinessCommand.fromRequest(request, id);
        UpdateBusinessMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
