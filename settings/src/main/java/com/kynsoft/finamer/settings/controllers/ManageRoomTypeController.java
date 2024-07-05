package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageRoomType.create.CreateManageRoomTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageRoomType.create.CreateManageRoomTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageRoomType.create.CreateManageRoomTypeRequest;
import com.kynsoft.finamer.settings.application.command.manageRoomType.delete.DeleteManageRoomTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageRoomType.delete.DeleteManageRoomTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageRoomType.update.UpdateManageRoomTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageRoomType.update.UpdateManageRoomTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageRoomType.update.UpdateManageRoomTypeRequest;
import com.kynsoft.finamer.settings.application.query.manageRoomType.getById.FindManageRoomTypeByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageRoomType.search.GetSearchManageRoomTypeQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageRoomTypeResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-room-type")
public class ManageRoomTypeController {

    private final IMediator mediator;

    public ManageRoomTypeController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManageRoomTypeRequest request){
        CreateManageRoomTypeCommand command = CreateManageRoomTypeCommand.fromRequest(request);
        CreateManageRoomTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageRoomTypeByIdQuery query = new FindManageRoomTypeByIdQuery(id);
        ManageRoomTypeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageRoomTypeCommand command = new DeleteManageRoomTypeCommand(id);
        DeleteManageRoomTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageRoomTypeQuery query = new GetSearchManageRoomTypeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageRoomTypeRequest request){
        UpdateManageRoomTypeCommand command = UpdateManageRoomTypeCommand.fromRequest(request, id);
        UpdateManageRoomTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
