package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageCountry.create.CreateManagerCountryCommand;
import com.kynsoft.finamer.settings.application.command.manageCountry.create.CreateManagerCountryMessage;
import com.kynsoft.finamer.settings.application.command.manageCountry.create.CreateManagerCountryRequest;
import com.kynsoft.finamer.settings.application.command.manageCountry.delete.DeleteManagerCountryCommand;
import com.kynsoft.finamer.settings.application.command.manageCountry.delete.DeleteManagerCountryMessage;
import com.kynsoft.finamer.settings.application.command.manageCountry.update.UpdateManagerCountryCommand;
import com.kynsoft.finamer.settings.application.command.manageCountry.update.UpdateManagerCountryMessage;
import com.kynsoft.finamer.settings.application.command.manageCountry.update.UpdateManagerCountryRequest;
import com.kynsoft.finamer.settings.application.query.managerCountry.getById.FindManagerContryByIdQuery;
import com.kynsoft.finamer.settings.application.query.managerCountry.search.GetSearchManagerCountryQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerCountryResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-country")
public class ManagerCountryController {

    private final IMediator mediator;

    public ManagerCountryController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManagerCountryMessage> create(@RequestBody CreateManagerCountryRequest request) {
        CreateManagerCountryCommand createCommand = CreateManagerCountryCommand.fromRequest(request);
        CreateManagerCountryMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManagerContryByIdQuery query = new FindManagerContryByIdQuery(id);
        ManagerCountryResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManagerCountryCommand command = new DeleteManagerCountryCommand(id);
        DeleteManagerCountryMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManagerCountryQuery query = new GetSearchManagerCountryQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManagerCountryRequest request) {

        UpdateManagerCountryCommand command = UpdateManagerCountryCommand.fromRequest(request, id);
        UpdateManagerCountryMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
