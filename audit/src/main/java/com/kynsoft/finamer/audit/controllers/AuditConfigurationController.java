package com.kynsoft.finamer.audit.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.audit.application.command.configuration.update.UpdateConfigurationCommand;
import com.kynsoft.finamer.audit.application.command.configuration.update.UpdateConfigurationMessage;
import com.kynsoft.finamer.audit.application.command.configuration.update.UpdateConfigurationRequest;
import com.kynsoft.finamer.audit.application.query.configuration.getall.GetAllConfigurationQuery;
import com.kynsoft.finamer.audit.application.query.configuration.getbyid.GetConfigurationByIdQuery;
import com.kynsoft.finamer.audit.application.query.configuration.getbyid.GetConfigurationByIdResponse;
import com.kynsoft.finamer.audit.application.query.configuration.search.SearchConfigurationQuery;
import com.kynsoft.finamer.audit.application.query.configuration.search.SearchConfigurationResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/audit/config/")
public class AuditConfigurationController {
    private final IMediator mediator;
    public AuditConfigurationController(IMediator mediator) {
        this.mediator = mediator;
    }

    @GetMapping()
    public ResponseEntity<GetConfigurationByIdResponse> getAll(Pageable pageable) {
        GetAllConfigurationQuery query = new GetAllConfigurationQuery(pageable);
        return mediator.send(query);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetConfigurationByIdQuery> getById(@PathVariable UUID id) {
        GetConfigurationByIdQuery query = new GetConfigurationByIdQuery(id);
        return mediator.send(query);
    }

    @PostMapping("/search")
    public ResponseEntity<SearchConfigurationResponse> search(SearchRequest searchRequest) {
        Pageable pageable = PageableUtil.createPageable(searchRequest);
        SearchConfigurationQuery searchConfigurationQuery = new SearchConfigurationQuery(pageable,searchRequest.getFilter(),searchRequest.getQuery());
        return mediator.send(searchConfigurationQuery);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UpdateConfigurationMessage> update(@PathVariable UUID id, UpdateConfigurationRequest request) {
        UpdateConfigurationCommand updateConfigurationCommand = new UpdateConfigurationCommand(request, id);
        return mediator.send(updateConfigurationCommand);
    }
}
