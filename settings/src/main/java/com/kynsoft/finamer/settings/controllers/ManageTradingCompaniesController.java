package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageTradingCompanies.create.CreateManageTradingCompaniesCommand;
import com.kynsoft.finamer.settings.application.command.manageTradingCompanies.create.CreateManageTradingCompaniesMessage;
import com.kynsoft.finamer.settings.application.command.manageTradingCompanies.create.CreateManageTradingCompaniesRequest;
import com.kynsoft.finamer.settings.application.command.manageTradingCompanies.delete.DeleteManageTradingCompaniesCommand;
import com.kynsoft.finamer.settings.application.command.manageTradingCompanies.delete.DeleteManageTradingCompaniesMessage;
import com.kynsoft.finamer.settings.application.command.manageTradingCompanies.update.UpdateManageTradingCompaniesCommand;
import com.kynsoft.finamer.settings.application.command.manageTradingCompanies.update.UpdateManageTradingCompaniesMessage;
import com.kynsoft.finamer.settings.application.command.manageTradingCompanies.update.UpdateManageTradingCompaniesRequest;
import com.kynsoft.finamer.settings.application.query.manageTradingCompanies.getById.FindManageTradingCompaniesByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageTradingCompanies.search.GetSearchManageTradingCompaniesQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageTradingCompaniesResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-trading-companies")
public class ManageTradingCompaniesController {

    private final IMediator mediator;

    public ManageTradingCompaniesController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManageTradingCompaniesRequest request){
        CreateManageTradingCompaniesCommand command = CreateManageTradingCompaniesCommand.fromRequest(request);
        CreateManageTradingCompaniesMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindManageTradingCompaniesByIdQuery query = new FindManageTradingCompaniesByIdQuery(id);
        ManageTradingCompaniesResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManageTradingCompaniesCommand command = new DeleteManageTradingCompaniesCommand(id);
        DeleteManageTradingCompaniesMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageTradingCompaniesQuery query = new GetSearchManageTradingCompaniesQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManageTradingCompaniesRequest request){
        UpdateManageTradingCompaniesCommand command = UpdateManageTradingCompaniesCommand.fromRequest(request, id);
        UpdateManageTradingCompaniesMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }
}
