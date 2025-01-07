package com.kynsoft.finamer.insis.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.innsistConnectionParams.assignTradingCompany.AssignTradingCompanyConnectionCommand;
import com.kynsoft.finamer.insis.application.command.innsistConnectionParams.assignTradingCompany.AssignTradingCompanyConnectionMessage;
import com.kynsoft.finamer.insis.application.command.innsistConnectionParams.assignTradingCompany.AssignTradingCompanyConnectionRequest;
import com.kynsoft.finamer.insis.application.command.innsistConnectionParams.unassingTradingCompany.UnassingTradingCompanyConnectionCommand;
import com.kynsoft.finamer.insis.application.command.innsistConnectionParams.unassingTradingCompany.UnassingTradingCompanyConnectionMessage;
import com.kynsoft.finamer.insis.application.command.innsistConnectionParams.unassingTradingCompany.UnassingTradingCompanyConnectionRequest;
import com.kynsoft.finamer.insis.application.query.innsistConnectionParamsQuery.getById.FindInnsistConnectionParamsByIdQuery;
import com.kynsoft.finamer.insis.application.query.innsistConnectionParamsQuery.getByTradingCompany.FindInnsistConnectionParamsByTradingCompanyQuery;
import com.kynsoft.finamer.insis.application.query.innsistConnectionParamsQuery.getTradingCompanyAssociated.GetTradingCompanyAssociatedQuery;
import com.kynsoft.finamer.insis.application.query.innsistConnectionParamsQuery.hasTradingCompanyAssociated.CheckTradingCompanyAssociatedQuery;
import com.kynsoft.finamer.insis.application.query.innsistConnectionParamsQuery.search.InnsistConnectionParamsQuery;
import com.kynsoft.finamer.insis.application.query.objectResponse.innsistConnectionParams.HasTradingCompanyAssociatedResponse;
import com.kynsoft.finamer.insis.application.query.objectResponse.innsistConnectionParams.InnsistConnectionParamsResponse;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageTradingCompany.ManageTradingCompanyResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/innsist-connection-params")
public class InnsistConnectionParamsController {
    private final IMediator mediator;

    public InnsistConnectionParamsController(IMediator mediator){
        this.mediator = mediator;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id){
        FindInnsistConnectionParamsByIdQuery query = new FindInnsistConnectionParamsByIdQuery(id);

        InnsistConnectionParamsResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{tradingCompanyId}/trading-company")
    public ResponseEntity<?> getByTradingCompany(@PathVariable UUID tradingCompanyId){
        FindInnsistConnectionParamsByTradingCompanyQuery query = new FindInnsistConnectionParamsByTradingCompanyQuery(tradingCompanyId);

        InnsistConnectionParamsResponse response = mediator.send(query);
        if(response != null){
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(path = "/{id}/has-trading-company-associated")
    public ResponseEntity<?> checkTradingCompanyAssociated(@PathVariable UUID id){
        CheckTradingCompanyAssociatedQuery query = new CheckTradingCompanyAssociatedQuery(id);

        HasTradingCompanyAssociatedResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}/innsist-connection-has-trading-company")
    public ResponseEntity<?> getTradingCompanyByConnectionId(@PathVariable UUID id){
        GetTradingCompanyAssociatedQuery query = new GetTradingCompanyAssociatedQuery(id);

        ManageTradingCompanyResponse response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

    @PostMapping("search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request){
        Pageable pageable = PageableUtil.createPageable(request);

        InnsistConnectionParamsQuery query = new InnsistConnectionParamsQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/assign")
    public ResponseEntity<AssignTradingCompanyConnectionMessage> assignTradingCompany(@RequestBody AssignTradingCompanyConnectionRequest request){
        AssignTradingCompanyConnectionCommand command = AssignTradingCompanyConnectionCommand.fromRequest(request);
        mediator.send(command);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unassign")
    public ResponseEntity<UnassingTradingCompanyConnectionMessage> unassignTradingCompany(@RequestBody UnassingTradingCompanyConnectionRequest request){
        UnassingTradingCompanyConnectionCommand command = UnassingTradingCompanyConnectionCommand.fromRequest(request);
        mediator.send(command);
        return ResponseEntity.ok().build();
    }
}
