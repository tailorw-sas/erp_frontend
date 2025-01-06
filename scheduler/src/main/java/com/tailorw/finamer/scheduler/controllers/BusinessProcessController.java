package com.tailorw.finamer.scheduler.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.tailorw.finamer.scheduler.application.command.businessProcess.create.CreateBusinessProcessCommand;
import com.tailorw.finamer.scheduler.application.command.businessProcess.create.CreateBusinessProcessMessage;
import com.tailorw.finamer.scheduler.application.command.businessProcess.create.CreateBusinessProcessRequest;
import com.tailorw.finamer.scheduler.application.command.businessProcess.delete.DeleteBusinessProcessCommand;
import com.tailorw.finamer.scheduler.application.command.businessProcess.delete.DeleteBusinessProcessMessage;
import com.tailorw.finamer.scheduler.application.command.businessProcess.update.UpdateBusinessProcessCommand;
import com.tailorw.finamer.scheduler.application.command.businessProcess.update.UpdateBusinessProcessMessage;
import com.tailorw.finamer.scheduler.application.command.businessProcess.update.UpdateBusinessProcessRequest;
import com.tailorw.finamer.scheduler.application.query.businessProcess.getById.GetBusinessProcessByIdQuery;
import com.tailorw.finamer.scheduler.application.query.businessProcess.search.GetSearchBusinessProcessQuery;
import com.tailorw.finamer.scheduler.application.query.objectResponse.BusinessProcessResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/business-process")
public class BusinessProcessController {

    private final IMediator mediator;

    public BusinessProcessController(IMediator mediator){
        this.mediator = mediator;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id){
        GetBusinessProcessByIdQuery query = new GetBusinessProcessByIdQuery(id);
        BusinessProcessResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request){
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchBusinessProcessQuery query = new GetSearchBusinessProcessQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateBusinessProcessMessage> create(@RequestBody CreateBusinessProcessRequest request){
        CreateBusinessProcessCommand command = CreateBusinessProcessCommand.fromRequest(request);
        CreateBusinessProcessMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateBusinessProcessRequest request){
        UpdateBusinessProcessCommand command = UpdateBusinessProcessCommand.fromRequest(id, request);
        UpdateBusinessProcessMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id){
        DeleteBusinessProcessCommand command = new DeleteBusinessProcessCommand(id);
        DeleteBusinessProcessMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
