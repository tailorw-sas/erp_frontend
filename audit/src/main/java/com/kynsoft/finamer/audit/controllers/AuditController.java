package com.kynsoft.finamer.audit.controllers;


import com.kynsoft.finamer.audit.application.query.audit.getall.AuditResponse;
import com.kynsoft.finamer.audit.application.query.audit.getall.FindAllAuditQuery;
import com.kynsoft.finamer.audit.infrastructure.bus.IMediator;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final IMediator mediator;

    public AuditController(IMediator mediator) {
        this.mediator = mediator;
    }

    @GetMapping("/")
    public ResponseEntity<AuditResponse> findAll(Pageable pageable){
        FindAllAuditQuery findAllAuditQuery = new FindAllAuditQuery(pageable);
        return mediator.send(findAllAuditQuery);
    }
}
