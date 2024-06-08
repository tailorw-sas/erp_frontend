package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageCreditCardType.create.CreateManageCreditCardTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageCreditCardType.create.CreateManageCreditCardTypeRequest;
import com.kynsoft.finamer.settings.application.command.manageCreditCardType.create.CreateManagerCreditCardTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageCreditCardType.delete.DeleteManageCreditCardTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageCreditCardType.delete.DeleteManageCreditCardTypeMessage;
import com.kynsoft.finamer.settings.application.command.manageCreditCardType.update.UpdateManageCreditCardTypeCommand;
import com.kynsoft.finamer.settings.application.command.manageCreditCardType.update.UpdateManageCreditCardTypeRequest;
import com.kynsoft.finamer.settings.application.command.manageCreditCardType.update.UpdateManageCreditCardTypeMessage;
import com.kynsoft.finamer.settings.application.query.managerCreditCardType.getById.FindManageCreditCardTypeByIdQuery;
import com.kynsoft.finamer.settings.application.query.managerCreditCardType.search.GetSearchManageCreditCardTypeQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageCreditCardTypeResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-credit-card-type")
public class ManageCreditCardTypeController {

    private final IMediator mediator;

    public ManageCreditCardTypeController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManagerCreditCardTypeMessage> create(@RequestBody CreateManageCreditCardTypeRequest request) {
        CreateManageCreditCardTypeCommand createCommand = CreateManageCreditCardTypeCommand.fromRequest(request);
        CreateManagerCreditCardTypeMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManageCreditCardTypeByIdQuery query = new FindManageCreditCardTypeByIdQuery(id);
        ManageCreditCardTypeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManageCreditCardTypeCommand command = new DeleteManageCreditCardTypeCommand(id);
        DeleteManageCreditCardTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageCreditCardTypeQuery query = new GetSearchManageCreditCardTypeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageCreditCardTypeRequest request) {

        UpdateManageCreditCardTypeCommand command = UpdateManageCreditCardTypeCommand.fromRequest(request, id);
        UpdateManageCreditCardTypeMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
