package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageHotel.create.CreateManageHotelCommand;
import com.kynsoft.finamer.settings.application.command.manageHotel.create.CreateManageHotelMessage;
import com.kynsoft.finamer.settings.application.command.manageHotel.create.CreateManageHotelRequest;
import com.kynsoft.finamer.settings.application.command.manageHotel.delete.DeleteManageHotelCommand;
import com.kynsoft.finamer.settings.application.command.manageHotel.delete.DeleteManageHotelMessage;
import com.kynsoft.finamer.settings.application.command.manageHotel.update.UpdateManageHotelCommand;
import com.kynsoft.finamer.settings.application.command.manageHotel.update.UpdateManageHotelMessage;
import com.kynsoft.finamer.settings.application.command.manageHotel.update.UpdateManageHotelRequest;
import com.kynsoft.finamer.settings.application.query.manageHotel.getById.FindManageHotelByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageHotel.search.GetSearchManageHotelQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageHotelResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-hotel")
public class ManageHotelController {

    private final IMediator mediator;

    public ManageHotelController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManageHotelRequest request){
        CreateManageHotelCommand command = CreateManageHotelCommand.fromRequest(request);
        CreateManageHotelMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageHotelByIdQuery query = new FindManageHotelByIdQuery(id);
        ManageHotelResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageHotelCommand command = new DeleteManageHotelCommand(id);
        DeleteManageHotelMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageHotelQuery query = new GetSearchManageHotelQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageHotelRequest request){
        UpdateManageHotelCommand command = UpdateManageHotelCommand.fromRequest(request, id);
        UpdateManageHotelMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
