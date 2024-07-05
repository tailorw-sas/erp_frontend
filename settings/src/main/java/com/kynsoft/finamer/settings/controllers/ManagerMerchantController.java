package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageMerchant.create.CreateManagerMerchantCommand;
import com.kynsoft.finamer.settings.application.command.manageMerchant.create.CreateManagerMerchantMessage;
import com.kynsoft.finamer.settings.application.command.manageMerchant.create.CreateManagerMerchantRequest;
import com.kynsoft.finamer.settings.application.command.manageMerchant.delete.DeleteManagerMerchantCommand;
import com.kynsoft.finamer.settings.application.command.manageMerchant.delete.DeleteManagerMerchantMessage;
import com.kynsoft.finamer.settings.application.command.manageMerchant.update.UpdateManagerMerchantCommand;
import com.kynsoft.finamer.settings.application.command.manageMerchant.update.UpdateManagerMerchantMessage;
import com.kynsoft.finamer.settings.application.command.manageMerchant.update.UpdateManagerMerchantRequest;
import com.kynsoft.finamer.settings.application.query.managerMerchant.getById.FindManagerMerchantByIdQuery;
import com.kynsoft.finamer.settings.application.query.managerMerchant.search.GetSearchManagerMerchantQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerMerchantResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-merchant")
public class ManagerMerchantController {

    private final IMediator mediator;

    public ManagerMerchantController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManagerMerchantMessage> create(@RequestBody CreateManagerMerchantRequest request) {
        CreateManagerMerchantCommand createCommand = CreateManagerMerchantCommand.fromRequest(request);
        CreateManagerMerchantMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManagerMerchantByIdQuery query = new FindManagerMerchantByIdQuery(id);
        ManagerMerchantResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManagerMerchantCommand command = new DeleteManagerMerchantCommand(id);
        DeleteManagerMerchantMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManagerMerchantQuery query = new GetSearchManagerMerchantQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManagerMerchantRequest request) {

        UpdateManagerMerchantCommand command = UpdateManagerMerchantCommand.fromRequest(request, id);
        UpdateManagerMerchantMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
