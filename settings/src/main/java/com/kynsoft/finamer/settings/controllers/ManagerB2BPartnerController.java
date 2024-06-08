package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageB2BPartner.create.CreateManagerB2BPartnerCommand;
import com.kynsoft.finamer.settings.application.command.manageB2BPartner.create.CreateManagerB2BPartnerMessage;
import com.kynsoft.finamer.settings.application.command.manageB2BPartner.create.CreateManagerB2BPartnerRequest;
import com.kynsoft.finamer.settings.application.command.manageB2BPartner.delete.DeleteManagerB2BPartnerCommand;
import com.kynsoft.finamer.settings.application.command.manageB2BPartner.delete.DeleteManagerB2BPartnerMessage;
import com.kynsoft.finamer.settings.application.command.manageB2BPartner.update.UpdateManagerB2BPartnerCommand;
import com.kynsoft.finamer.settings.application.command.manageB2BPartner.update.UpdateManagerB2BPartnerMessage;
import com.kynsoft.finamer.settings.application.command.manageB2BPartner.update.UpdateManagerB2BPartnerRequest;
import com.kynsoft.finamer.settings.application.query.manageB2BPartner.getById.FindManagerB2BPartnerByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageB2BPartner.search.GetSearchManagerB2BPartnerQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerB2BPartnerResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-b2b-partner")
public class ManagerB2BPartnerController {

    private final IMediator mediator;

    public ManagerB2BPartnerController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManagerB2BPartnerMessage> create(@RequestBody CreateManagerB2BPartnerRequest request) {
        CreateManagerB2BPartnerCommand createCommand = CreateManagerB2BPartnerCommand.fromRequest(request);
        CreateManagerB2BPartnerMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManagerB2BPartnerByIdQuery query = new FindManagerB2BPartnerByIdQuery(id);
        ManagerB2BPartnerResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManagerB2BPartnerCommand command = new DeleteManagerB2BPartnerCommand(id);
        DeleteManagerB2BPartnerMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManagerB2BPartnerQuery query = new GetSearchManagerB2BPartnerQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManagerB2BPartnerRequest request) {

        UpdateManagerB2BPartnerCommand command = UpdateManagerB2BPartnerCommand.fromRequest(request, id);
        UpdateManagerB2BPartnerMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
