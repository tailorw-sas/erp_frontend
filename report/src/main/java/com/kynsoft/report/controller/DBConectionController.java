package com.kynsoft.report.controller;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.dbconection.create.CreateDBConectionCommand;
import com.kynsoft.report.applications.command.dbconection.create.CreateDBConectionMessage;
import com.kynsoft.report.applications.command.dbconection.create.CreateDBConectionRequest;
import com.kynsoft.report.applications.command.dbconection.delete.DeleteDBConectionCommand;
import com.kynsoft.report.applications.command.dbconection.delete.DeleteDBConectionMessage;
import com.kynsoft.report.applications.command.dbconection.update.UpdateDBConectionCommand;
import com.kynsoft.report.applications.command.dbconection.update.UpdateDBConectionMessage;
import com.kynsoft.report.applications.command.dbconection.update.UpdateDBConectionRequest;
import com.kynsoft.report.applications.query.dbconection.getById.DBConectionResponse;
import com.kynsoft.report.applications.query.dbconection.getById.FindDBConectionByIdQuery;
import com.kynsoft.report.applications.query.dbconection.search.GetSearchDBConectionQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/db-connection")
public class DBConectionController {

    private final IMediator mediator;

    public DBConectionController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateDBConectionRequest request) {
        CreateDBConectionCommand createCommand = CreateDBConectionCommand.fromRequest(request);
        CreateDBConectionMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindDBConectionByIdQuery query = new FindDBConectionByIdQuery(id);
        DBConectionResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchDBConectionQuery query = new GetSearchDBConectionQuery(pageable,
                request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);

        return ResponseEntity.ok(data);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") UUID id, @RequestBody UpdateDBConectionRequest request) {

        UpdateDBConectionCommand command = UpdateDBConectionCommand.fromRequest(request, id);
        UpdateDBConectionMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        DeleteDBConectionCommand query = new DeleteDBConectionCommand(id);
        DeleteDBConectionMessage response = mediator.send(query);

        return ResponseEntity.ok(response);
    }
}
