package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageRoomCategory.create.CreateManageRoomCategoryCommand;
import com.kynsoft.finamer.settings.application.command.manageRoomCategory.create.CreateManageRoomCategoryMessage;
import com.kynsoft.finamer.settings.application.command.manageRoomCategory.create.CreateManageRoomCategoryRequest;
import com.kynsoft.finamer.settings.application.command.manageRoomCategory.delete.DeleteManageRoomCategoryCommand;
import com.kynsoft.finamer.settings.application.command.manageRoomCategory.delete.DeleteManageRoomCategoryMessage;
import com.kynsoft.finamer.settings.application.command.manageRoomCategory.update.UpdateManageRoomCategoryCommand;
import com.kynsoft.finamer.settings.application.command.manageRoomCategory.update.UpdateManageRoomCategoryMessage;
import com.kynsoft.finamer.settings.application.command.manageRoomCategory.update.UpdateManageRoomCategoryRequest;
import com.kynsoft.finamer.settings.application.query.manageRoomCategory.getById.FindManageRoomCategoryByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageRoomCategory.search.GetSearchManageRoomCategoryQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageRoomCategoryResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-room-category")
public class ManageRoomCategoryController {

    private final IMediator mediator;

    public ManageRoomCategoryController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManageRoomCategoryRequest request){
        CreateManageRoomCategoryCommand command = CreateManageRoomCategoryCommand.fromRequest(request);
        CreateManageRoomCategoryMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageRoomCategoryByIdQuery query = new FindManageRoomCategoryByIdQuery(id);
        ManageRoomCategoryResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageRoomCategoryCommand command = new DeleteManageRoomCategoryCommand(id);
        DeleteManageRoomCategoryMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageRoomCategoryQuery query = new GetSearchManageRoomCategoryQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageRoomCategoryRequest request){
        UpdateManageRoomCategoryCommand command = UpdateManageRoomCategoryCommand.fromRequest(request, id);
        UpdateManageRoomCategoryMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
