package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageMerchantCommission.create.CreateManageMerchantCommissionCommand;
import com.kynsoft.finamer.settings.application.command.manageMerchantCommission.create.CreateManageMerchantCommissionMessage;
import com.kynsoft.finamer.settings.application.command.manageMerchantCommission.create.CreateManageMerchantCommissionRequest;
import com.kynsoft.finamer.settings.application.command.manageMerchantCommission.delete.DeleteManageMerchantCommissionCommand;
import com.kynsoft.finamer.settings.application.command.manageMerchantCommission.delete.DeleteManageMerchantCommissionMessage;
import com.kynsoft.finamer.settings.application.command.manageMerchantCommission.update.UpdateManageMerchantCommissionCommand;
import com.kynsoft.finamer.settings.application.command.manageMerchantCommission.update.UpdateManageMerchantCommissionMessage;
import com.kynsoft.finamer.settings.application.command.manageMerchantCommission.update.UpdateManageMerchantCommissionRequest;
import com.kynsoft.finamer.settings.application.query.manageMerchantCommission.getById.FindManageMerchantCommissionByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageMerchantCommission.search.GetSearchManageMerchantCommissionQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageMerchantCommissionResponse;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manage-merchant-commission")
public class ManageMerchantCommissionController {

    private final IMediator mediator;

    public ManageMerchantCommissionController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManageMerchantCommissionMessage> create(@RequestBody CreateManageMerchantCommissionRequest request) {
        CreateManageMerchantCommissionCommand createCommand = CreateManageMerchantCommissionCommand.fromRequest(request);
        CreateManageMerchantCommissionMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManageMerchantCommissionByIdQuery query = new FindManageMerchantCommissionByIdQuery(id);
        ManageMerchantCommissionResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManageMerchantCommissionCommand command = new DeleteManageMerchantCommissionCommand(id);
        DeleteManageMerchantCommissionMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageMerchantCommissionQuery query = new GetSearchManageMerchantCommissionQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageMerchantCommissionRequest request) {

        UpdateManageMerchantCommissionCommand command = UpdateManageMerchantCommissionCommand.fromRequest(request, id);
        UpdateManageMerchantCommissionMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
