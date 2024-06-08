package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.managerCurrency.create.CreateManagerCurrencyCommand;
import com.kynsoft.finamer.settings.application.command.managerCurrency.create.CreateManagerCurrencyMessage;
import com.kynsoft.finamer.settings.application.command.managerCurrency.create.CreateManagerCurrencyRequest;
import com.kynsoft.finamer.settings.application.command.managerCurrency.delete.DeleteManagerCurrencyCommand;
import com.kynsoft.finamer.settings.application.command.managerCurrency.delete.DeleteManagerCurrencyMessage;
import com.kynsoft.finamer.settings.application.command.managerCurrency.update.UpdateManagerCurrencyCommand;
import com.kynsoft.finamer.settings.application.command.managerCurrency.update.UpdateManagerCurrencyMessage;
import com.kynsoft.finamer.settings.application.command.managerCurrency.update.UpdateManagerCurrencyRequest;
import com.kynsoft.finamer.settings.application.query.managerCurrency.getById.FindManagerCurrencyByIdQuery;
import com.kynsoft.finamer.settings.application.query.managerCurrency.search.GetSearchManagerCurrencyQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerCurrencyResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-currency")
public class ManagerCurrencyController {

    private final IMediator mediator;

    public ManagerCurrencyController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManagerCurrencyMessage> create(@RequestBody CreateManagerCurrencyRequest request) {
        CreateManagerCurrencyCommand createCommand = CreateManagerCurrencyCommand.fromRequest(request);
        CreateManagerCurrencyMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManagerCurrencyByIdQuery query = new FindManagerCurrencyByIdQuery(id);
        ManagerCurrencyResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManagerCurrencyCommand command = new DeleteManagerCurrencyCommand(id);
        DeleteManagerCurrencyMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManagerCurrencyQuery query = new GetSearchManagerCurrencyQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManagerCurrencyRequest request) {

        UpdateManagerCurrencyCommand command = UpdateManagerCurrencyCommand.fromRequest(request, id);
        UpdateManagerCurrencyMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
