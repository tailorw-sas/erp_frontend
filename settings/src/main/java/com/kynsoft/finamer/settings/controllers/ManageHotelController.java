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
import com.kynsoft.finamer.settings.application.query.manageAgency.findAllGrouped.FindAllManageAgencyGroupedQuery;
import com.kynsoft.finamer.settings.application.query.manageHotel.findAllGrouped.FindAllHotelsGroupedQuery;
import com.kynsoft.finamer.settings.application.query.manageHotel.getById.FindManageHotelByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageHotel.search.GetSearchManageHotelQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageHotelResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageAgencyGroup.ManageAgencyGroupedResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageHotelGroup.ManageHotelGroupedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public ResponseEntity<?> search(@AuthenticationPrincipal Jwt jwt, @RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        String userId = jwt.getClaim("sub");
        UUID employeeId = UUID.fromString(userId);
        //UUID employeeId = UUID.fromString("637ee5cb-1e36-4917-a0a9-5874bc8bea04");
        GetSearchManageHotelQuery query = new GetSearchManageHotelQuery(pageable, request.getFilter(), request.getQuery(), employeeId);
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageHotelRequest request){
        UpdateManageHotelCommand command = UpdateManageHotelCommand.fromRequest(request, id);
        UpdateManageHotelMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all-grouped")
    public ResponseEntity<?> getAll() {
        FindAllHotelsGroupedQuery query = new FindAllHotelsGroupedQuery();
        ManageHotelGroupedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }
}
