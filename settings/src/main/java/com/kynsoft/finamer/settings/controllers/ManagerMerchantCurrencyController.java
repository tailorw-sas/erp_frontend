package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.managerMerchantCurrency.create.CreateManagerMerchantCurrencyCommand;
import com.kynsoft.finamer.settings.application.command.managerMerchantCurrency.create.CreateManagerMerchantCurrencyMessage;
import com.kynsoft.finamer.settings.application.command.managerMerchantCurrency.create.CreateManagerMerchantCurrencyRequest;
import com.kynsoft.finamer.settings.application.command.managerMerchantCurrency.delete.DeleteManagerMerchantCurrencyCommand;
import com.kynsoft.finamer.settings.application.command.managerMerchantCurrency.delete.DeleteManagerMerchantCurrencyMessage;
import com.kynsoft.finamer.settings.application.command.managerMerchantCurrency.update.UpdateManagerMerchantCurrencyCommand;
import com.kynsoft.finamer.settings.application.command.managerMerchantCurrency.update.UpdateManagerMerchantCurrencyMessage;
import com.kynsoft.finamer.settings.application.command.managerMerchantCurrency.update.UpdateManagerMerchantCurrencyRequest;
import com.kynsoft.finamer.settings.application.query.managerMerchantCurrency.getById.FindManagerMerchantCurrencyByIdQuery;
import com.kynsoft.finamer.settings.application.query.managerMerchantCurrency.search.GetSearchManagerMerchantCurrencyQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerMerchantCurrencyResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-merchant-currency")
public class ManagerMerchantCurrencyController {

    private final IMediator mediator;

    public ManagerMerchantCurrencyController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManagerMerchantCurrencyMessage> create(@RequestBody CreateManagerMerchantCurrencyRequest request) {
        CreateManagerMerchantCurrencyCommand createCommand = CreateManagerMerchantCurrencyCommand.fromRequest(request);
        CreateManagerMerchantCurrencyMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManagerMerchantCurrencyByIdQuery query = new FindManagerMerchantCurrencyByIdQuery(id);
        ManagerMerchantCurrencyResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManagerMerchantCurrencyCommand command = new DeleteManagerMerchantCurrencyCommand(id);
        DeleteManagerMerchantCurrencyMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManagerMerchantCurrencyQuery query = new GetSearchManagerMerchantCurrencyQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManagerMerchantCurrencyRequest request) {

        UpdateManagerMerchantCurrencyCommand command = UpdateManagerMerchantCurrencyCommand.fromRequest(request, id);
        UpdateManagerMerchantCurrencyMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

}
