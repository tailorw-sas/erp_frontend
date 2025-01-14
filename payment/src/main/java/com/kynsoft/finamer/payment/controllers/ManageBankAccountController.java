package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.query.manageBankAccount.getByAccountNumberAndHotel.FindManageBankAccountByIdQuery;
import com.kynsoft.finamer.payment.application.query.manageBankAccount.search.GetSearchManageBankAccountQuery;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManageBankAccountResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manage-bank-account")
public class ManageBankAccountController {

    private final IMediator mediator;

    public ManageBankAccountController(IMediator mediator) {

        this.mediator = mediator;
    }

    @GetMapping(path = "/number/{number}/hotel/{hotelCode}")
    public ResponseEntity<?> getByGenId(@PathVariable String number, @PathVariable String hotelCode) {

        FindManageBankAccountByIdQuery query = new FindManageBankAccountByIdQuery(number, hotelCode);
        ManageBankAccountResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageBankAccountQuery query = new GetSearchManageBankAccountQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }
}
