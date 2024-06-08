package com.kynsof.identity.controller;

import com.kynsof.identity.application.command.customer.create.CreateCustomerCommand;
import com.kynsof.identity.application.command.customer.create.CreateCustomerMessage;
import com.kynsof.identity.application.command.customer.create.CreateCustomerRequest;
import com.kynsof.identity.application.command.customer.delete.DeleteCustomerCommand;
import com.kynsof.identity.application.command.customer.delete.DeleteCustomerMessage;
import com.kynsof.identity.application.command.customer.deleteAll.DeleteAllCustomerCommand;
import com.kynsof.identity.application.command.customer.deleteAll.DeleteAllCustomerMessage;
import com.kynsof.identity.application.command.customer.deleteAll.DeleteAllCustomerRequest;
import com.kynsof.identity.application.command.customer.update.UpdateCustomerCommand;
import com.kynsof.identity.application.command.customer.update.UpdateCustomerMessage;
import com.kynsof.identity.application.command.customer.update.UpdateCustomerRequest;
import com.kynsof.identity.application.command.walletTransaction.create.CreateWalletTransactionCommand;
import com.kynsof.identity.application.command.walletTransaction.create.CreateWalletTransactionMessage;
import com.kynsof.identity.application.command.walletTransaction.create.CreateWalletTransactionRequest;
import com.kynsof.identity.application.query.customer.getbyid.CustomerResponse;
import com.kynsof.identity.application.query.customer.getbyid.FindCustomerByIdQuery;
import com.kynsof.identity.application.query.customer.search.GetSearchCustomerQuery;
import com.kynsof.identity.application.query.wallet.getByCustomerId.FindByCustomerIdQuery;
import com.kynsof.identity.application.query.wallet.getByCustomerId.WalletResponse;
import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final IMediator mediator;

    public CustomerController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody CreateCustomerRequest request) {
        CreateCustomerCommand createCommand = CreateCustomerCommand.fromRequest(request);
        CreateCustomerMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/wallet/balance")
    public ResponseEntity<?> createWalletBalance(@AuthenticationPrincipal Jwt jwt, @RequestBody CreateWalletTransactionRequest request) {
        String userId = jwt.getClaim("sub");
        CreateWalletTransactionCommand createCommand = CreateWalletTransactionCommand.fromRequest(UUID.fromString(userId), request);
        CreateWalletTransactionMessage response = mediator.send(createCommand);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/wallet/balance")
    public ResponseEntity<?> getWalletBalance(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaim("sub");
        FindByCustomerIdQuery createCommand = new FindByCustomerIdQuery(UUID.fromString(userId));
        WalletResponse response = mediator.send(createCommand);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") UUID id, @RequestBody UpdateCustomerRequest request) {

        UpdateCustomerCommand command = UpdateCustomerCommand.fromRequest(request, id);
        UpdateCustomerMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindCustomerByIdQuery query = new FindCustomerByIdQuery(id);
        CustomerResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchCustomerQuery query = new GetSearchCustomerQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteCustomerCommand command = new DeleteCustomerCommand(id);
        DeleteCustomerMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete-all")
    public ResponseEntity<?> delete(@RequestBody DeleteAllCustomerRequest request) {

        DeleteAllCustomerCommand command = new DeleteAllCustomerCommand(request.getCustomers());
        DeleteAllCustomerMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

}
