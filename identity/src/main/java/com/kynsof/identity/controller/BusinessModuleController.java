package com.kynsof.identity.controller;

import com.kynsof.identity.application.command.businessModule.create.CreateBusinessModuleCommand;
import com.kynsof.identity.application.command.businessModule.create.CreateBusinessModuleMessage;
import com.kynsof.identity.application.command.businessModule.create.CreateBusinessModuleRequest;
import com.kynsof.identity.application.command.businessModule.delete.DeleteBusinessModuleCommand;
import com.kynsof.identity.application.command.businessModule.delete.DeleteBusinessModuleMessage;
import com.kynsof.identity.application.command.businessModule.deleteall.DeleteAllBusinessModuleCommand;
import com.kynsof.identity.application.command.businessModule.deleteall.DeleteAllBusinessModuleMessage;
import com.kynsof.identity.application.command.businessModule.deleteall.DeleteAllBusinessModuleRequest;
import com.kynsof.identity.application.command.businessModule.update.UpdateBusinessModuleCommand;
import com.kynsof.identity.application.command.businessModule.update.UpdateBusinessModuleMessage;
import com.kynsof.identity.application.command.businessModule.update.UpdateBusinessModuleRequest;
import com.kynsof.identity.application.query.businessModule.getbyid.FindBusinessModuleByIdQuery;
import com.kynsof.identity.application.query.businessModule.search.BusinessModuleResponse;
import com.kynsof.identity.application.query.businessModule.search.GetSearchBusinessModuleQuery;
import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/business-module")
public class BusinessModuleController {

    private final IMediator mediator;

    public BusinessModuleController(IMediator mediator){

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateBusinessModuleMessage> create(@RequestBody CreateBusinessModuleRequest request)  {
        CreateBusinessModuleCommand createCommand = CreateBusinessModuleCommand.fromRequest(request);
        CreateBusinessModuleMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<?> update(@RequestBody UpdateBusinessModuleRequest request) {

        UpdateBusinessModuleCommand command = UpdateBusinessModuleCommand.fromRequest(request);
        UpdateBusinessModuleMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindBusinessModuleByIdQuery query = new FindBusinessModuleByIdQuery(id);
        BusinessModuleResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteBusinessModuleMessage> delete(@PathVariable("id") UUID id) {

        DeleteBusinessModuleCommand command = new DeleteBusinessModuleCommand(id);
        DeleteBusinessModuleMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<?> delete(@RequestBody DeleteAllBusinessModuleRequest request) {

        DeleteAllBusinessModuleCommand command = new DeleteAllBusinessModuleCommand(request.getBusinessModules());
        DeleteAllBusinessModuleMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request)
    {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchBusinessModuleQuery query = new GetSearchBusinessModuleQuery(pageable, request.getFilter(),request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

}
