package com.kynsoft.finamer.creditcard.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantConfig.create.CreateManageMerchantConfigCommand;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantConfig.create.CreateManageMerchantConfigMessage;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantConfig.create.CreateManageMerchantConfigRequest;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantConfig.delete.DeleteManagerMerchantConfigCommand;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantConfig.delete.DeleteManagerMerchantConfigMessage;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantConfig.update.UpdateManageMerchantConfigCommand;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantConfig.update.UpdateManageMerchantConfigMessage;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantConfig.update.UpdateManageMerchantConfigRequest;
import com.kynsoft.finamer.creditcard.application.query.managerMerchantConfig.getById.FindManagerMerchantConfigByIdQuery;
import com.kynsoft.finamer.creditcard.application.query.managerMerchantConfig.search.GetSearchManagerMerchantConfigQuery;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManagerMerchantConfigResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/merchant-config")
public class ManageMerchantConfigController {

    private final IMediator mediator;

    public ManageMerchantConfigController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManageMerchantConfigMessage> create(@RequestBody CreateManageMerchantConfigRequest request) {
        CreateManageMerchantConfigCommand merchantConfigCommand = CreateManageMerchantConfigCommand.fromRequest(request);
        CreateManageMerchantConfigMessage response = mediator.send(merchantConfigCommand);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageMerchantConfigRequest request) {
        UpdateManageMerchantConfigCommand command = UpdateManageMerchantConfigCommand.fromRequest(request, id);
        UpdateManageMerchantConfigMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchManagerMerchantConfigQuery query = new GetSearchManagerMerchantConfigQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManagerMerchantConfigByIdQuery query = new FindManagerMerchantConfigByIdQuery(id);
        ManagerMerchantConfigResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable(value = "id") UUID id) {
        DeleteManagerMerchantConfigCommand command = new DeleteManagerMerchantConfigCommand(id);
        DeleteManagerMerchantConfigMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
