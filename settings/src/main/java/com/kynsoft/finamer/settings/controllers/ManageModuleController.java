package com.kynsoft.finamer.settings.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.settings.application.command.manageAlerts.create.CreateAlertCommand;
import com.kynsoft.finamer.settings.application.command.manageAlerts.create.CreateAlertMessage;
import com.kynsoft.finamer.settings.application.command.manageAlerts.create.CreateAlertRequest;
import com.kynsoft.finamer.settings.application.command.manageAlerts.delete.DeleteAlertCommand;
import com.kynsoft.finamer.settings.application.command.manageAlerts.delete.DeleteAlertMessage;
import com.kynsoft.finamer.settings.application.command.manageAlerts.update.UpdateAlertCommand;
import com.kynsoft.finamer.settings.application.command.manageAlerts.update.UpdateAlertMessage;
import com.kynsoft.finamer.settings.application.query.manageAlerts.getById.FindAlertByIdQuery;
import com.kynsoft.finamer.settings.application.query.manageAlerts.search.GetSearchAlertQuery;
import com.kynsoft.finamer.settings.application.query.manageModule.search.GetSearchManageModuleQuery;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageAlertsResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/module")
public class ManageModuleController {

    private final IMediator mediator;

    public ManageModuleController(final IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageModuleQuery query = new GetSearchManageModuleQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

}
