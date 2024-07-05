package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageLanguage.create.CreateManagerLanguageCommand;
import com.kynsoft.finamer.settings.application.command.manageLanguage.create.CreateManagerLanguageMessage;
import com.kynsoft.finamer.settings.application.command.manageLanguage.create.CreateManagerLanguageRequest;
import com.kynsoft.finamer.settings.application.command.manageLanguage.delete.DeleteManagerLanguageCommand;
import com.kynsoft.finamer.settings.application.command.manageLanguage.delete.DeleteManagerLanguageMessage;
import com.kynsoft.finamer.settings.application.command.manageLanguage.update.UpdateManagerLanguageCommand;
import com.kynsoft.finamer.settings.application.command.manageLanguage.update.UpdateManagerLanguageMessage;
import com.kynsoft.finamer.settings.application.command.manageLanguage.update.UpdateManagerLanguageRequest;
import com.kynsoft.finamer.settings.application.query.managerLanguage.getById.FindManagerLanguageByIdQuery;
import com.kynsoft.finamer.settings.application.query.managerLanguage.search.GetSearchManagerLanguageQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerLanguageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-language")
public class ManagerLanguageController {

    private final IMediator mediator;

    public ManagerLanguageController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateManagerLanguageRequest request){
        CreateManagerLanguageCommand command = CreateManagerLanguageCommand.fromRequest(request);
        CreateManagerLanguageMessage message = mediator.send(command);

        return ResponseEntity.ok(message);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id){
        FindManagerLanguageByIdQuery query = new FindManagerLanguageByIdQuery(id);
        ManagerLanguageResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        DeleteManagerLanguageCommand command = new DeleteManagerLanguageCommand(id);
        DeleteManagerLanguageMessage message = mediator.send(command);

        return ResponseEntity.ok(message);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManagerLanguageQuery query = new GetSearchManagerLanguageQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManagerLanguageRequest request){
        UpdateManagerLanguageCommand command = UpdateManagerLanguageCommand.fromRequest(request, id);
        UpdateManagerLanguageMessage message = mediator.send(command);

        return ResponseEntity.ok(message);
    }
}
