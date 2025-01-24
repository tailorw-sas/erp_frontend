package com.tailorw.tcaInnsist.controllers;

import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.tailorw.tcaInnsist.application.command.tcaCatalog.validateCatalog.ValidateCatalogCommand;
import com.tailorw.tcaInnsist.application.command.tcaCatalog.validateCatalog.ValidateCatalogMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tca-catalog")
public class TcaCatalogController {

    private final IMediator mediator;

    public TcaCatalogController (IMediator mediator){
        this.mediator = mediator;
    }

    @PostMapping(path = "/validate")
    public ResponseEntity<Void> validateCatalog(){
        ValidateCatalogCommand command = new ValidateCatalogCommand();
        ValidateCatalogMessage response = mediator.send(command);

        return ResponseEntity.ok().build();
    }
}
