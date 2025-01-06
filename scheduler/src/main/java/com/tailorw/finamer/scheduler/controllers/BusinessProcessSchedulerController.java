package com.tailorw.finamer.scheduler.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.tailorw.finamer.scheduler.application.command.businessProcessScheduler.create.CreateBusinessProcessSchedulerCommand;
import com.tailorw.finamer.scheduler.application.command.businessProcessScheduler.create.CreateBusinessProcessSchedulerMessage;
import com.tailorw.finamer.scheduler.application.command.businessProcessScheduler.create.CreateBusinessProcessSchedulerRequest;
import com.tailorw.finamer.scheduler.application.command.businessProcessScheduler.delete.DeleteBusinessProcessSchedulerCommand;
import com.tailorw.finamer.scheduler.application.command.businessProcessScheduler.delete.DeleteBusinessProcessSchedulerMessage;
import com.tailorw.finamer.scheduler.application.command.businessProcessScheduler.update.UpdateBusinessProcessSchedulerCommand;
import com.tailorw.finamer.scheduler.application.command.businessProcessScheduler.update.UpdateBusinessProcessSchedulerMessage;
import com.tailorw.finamer.scheduler.application.command.businessProcessScheduler.update.UpdateBusinessProcessSchedulerRequest;
import com.tailorw.finamer.scheduler.application.query.businessProcessScheduler.getById.GetBusinessProcessSchedulerByIdQuery;
import com.tailorw.finamer.scheduler.application.query.businessProcessScheduler.search.GetSearchBusinessProcessSchedulerQuery;
import com.tailorw.finamer.scheduler.application.query.objectResponse.BusinessProcessSchedulerResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/business-process-scheduler")
public class BusinessProcessSchedulerController {

    private final IMediator mediator;

    public BusinessProcessSchedulerController(IMediator mediator){
        this.mediator = mediator;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id){
        GetBusinessProcessSchedulerByIdQuery query = new GetBusinessProcessSchedulerByIdQuery(id);
        BusinessProcessSchedulerResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateBusinessProcessSchedulerMessage> create(@RequestBody CreateBusinessProcessSchedulerRequest request){
        CreateBusinessProcessSchedulerCommand command = CreateBusinessProcessSchedulerCommand.fromRequest(request);
        CreateBusinessProcessSchedulerMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UpdateBusinessProcessSchedulerMessage> update(@PathVariable UUID id, @RequestBody UpdateBusinessProcessSchedulerRequest request){
        UpdateBusinessProcessSchedulerCommand command = UpdateBusinessProcessSchedulerCommand.fromRequest(id, request);
        UpdateBusinessProcessSchedulerMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteBusinessProcessSchedulerMessage> delete(@PathVariable UUID id){
        DeleteBusinessProcessSchedulerCommand command = new DeleteBusinessProcessSchedulerCommand(id);
        DeleteBusinessProcessSchedulerMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request){
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchBusinessProcessSchedulerQuery query = new GetSearchBusinessProcessSchedulerQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }
}
