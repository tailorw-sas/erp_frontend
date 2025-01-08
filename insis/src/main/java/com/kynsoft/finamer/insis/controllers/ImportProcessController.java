package com.kynsoft.finamer.insis.controllers;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.query.importProcess.getById.GetImportProcessByIdQuery;
import com.kynsoft.finamer.insis.application.query.objectResponse.importProcess.ImportProcessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/import-process")
public class ImportProcessController {

    private final IMediator mediator;

    public ImportProcessController(IMediator mediator){
        this.mediator = mediator;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id){
        GetImportProcessByIdQuery query = new GetImportProcessByIdQuery(id);
        ImportProcessResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }
}
