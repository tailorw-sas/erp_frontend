package com.kynsoft.finamer.invoicing.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create.CreateRoomRateCommand;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create.CreateRoomRateMessage;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.create.CreateRoomRateRequest;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.delete.DeleteRoomRateCommand;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.delete.DeleteRoomRateMessage;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.update.UpdateRoomRateCommand;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.update.UpdateRoomRateMessage;
import com.kynsoft.finamer.invoicing.application.command.manageRoomRate.update.UpdateRoomRateRequest;
import com.kynsoft.finamer.invoicing.application.query.manageRoomRate.getById.FindRoomRateByIdQuery;
import com.kynsoft.finamer.invoicing.application.query.manageRoomRate.search.GetSearchRoomRateQuery;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageRoomRateResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-room-rate")
public class RoomRateController {

    private final IMediator mediator;

    public RoomRateController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateRoomRateMessage> create(@RequestBody CreateRoomRateRequest request) {
        CreateRoomRateCommand createCommand = CreateRoomRateCommand.fromRequest(request);
        CreateRoomRateMessage response = mediator.send(createCommand);

    

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindRoomRateByIdQuery query = new FindRoomRateByIdQuery(id);
        ManageRoomRateResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteRoomRateCommand command = new DeleteRoomRateCommand(id);
        DeleteRoomRateMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchRoomRateQuery query = new GetSearchRoomRateQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateRoomRateRequest request) {

        UpdateRoomRateCommand command = UpdateRoomRateCommand.fromRequest(request, id);
        UpdateRoomRateMessage response = mediator.send(command);

      
        
        return ResponseEntity.ok(response);
    }
}
