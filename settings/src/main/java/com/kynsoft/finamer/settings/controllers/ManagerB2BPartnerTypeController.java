package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageB2BPartnerType.create.CreateManageB2BPartnerTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageB2BPartnerType.create.CreateManageB2BPartnerTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageB2BPartnerType.create.CreateManageB2BPartnerTypeRequest;
import com.kynsoft.finamer.settings.application.command.manageB2BPartnerType.delete.DeleteManageB2BPartnerTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageB2BPartnerType.delete.DeleteManageB2BPartnerTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageB2BPartnerType.update.UpdateManageB2BPartnerTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageB2BPartnerType.update.UpdateManageB2BPartnerTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageB2BPartnerType.update.UpdateManageB2BPartnerTypeRequest;
import com.kynsoft.finamer.settings.application.query.manageB2BPartnerType.getById.FindManagerB2BPartnerTypeByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageB2BPartnerType.search.GetSearchManagerB2BPartnerTypeQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageB2BPartnerTypeResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-b2b-partner-type")
public class ManagerB2BPartnerTypeController {

    private final IMediator mediator;

    public ManagerB2BPartnerTypeController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManageB2BPartnerTypeMessage> create(@RequestBody CreateManageB2BPartnerTypeRequest request) {
        CreateManageB2BPartnerTypeCommand createCommand = CreateManageB2BPartnerTypeCommand.fromRequest(request);
        CreateManageB2BPartnerTypeMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManagerB2BPartnerTypeByIdQuery query = new FindManagerB2BPartnerTypeByIdQuery(id);
        ManageB2BPartnerTypeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManageB2BPartnerTypeCommand command = new DeleteManageB2BPartnerTypeCommand(id);
        DeleteManageB2BPartnerTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManagerB2BPartnerTypeQuery query = new GetSearchManagerB2BPartnerTypeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageB2BPartnerTypeRequest request) {

        UpdateManageB2BPartnerTypeCommand command = UpdateManageB2BPartnerTypeCommand.fromRequest(request, id);
        UpdateManageB2BPartnerTypeMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
