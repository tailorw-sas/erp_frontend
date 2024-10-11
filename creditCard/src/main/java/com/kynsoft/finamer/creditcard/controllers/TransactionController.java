package com.kynsoft.finamer.creditcard.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.ManageStatusTransaction.update.UpdateManageStatusTransactionCommand;
import com.kynsoft.finamer.creditcard.application.command.ManageStatusTransaction.update.UpdateManageStatusTransactionCommandMessage;
import com.kynsoft.finamer.creditcard.application.command.ManageStatusTransaction.update.UpdateManageStatusTransactionCommandRequest;
import com.kynsoft.finamer.creditcard.application.command.adjustmentTransaction.create.CreateAdjustmentTransactionCommand;
import com.kynsoft.finamer.creditcard.application.command.adjustmentTransaction.create.CreateAdjustmentTransactionMessage;
import com.kynsoft.finamer.creditcard.application.command.adjustmentTransaction.create.CreateAdjustmentTransactionRequest;
import com.kynsoft.finamer.creditcard.application.command.manageRedirectTransactionPayment.CreateRedirectTransactionPaymentCommand;
import com.kynsoft.finamer.creditcard.application.command.manageRedirectTransactionPayment.CreateRedirectTransactionPaymentCommandMessage;
import com.kynsoft.finamer.creditcard.application.command.manageRedirectTransactionPayment.CreateRedirectTransactionPaymentRequest;
import com.kynsoft.finamer.creditcard.application.command.manualTransaction.create.CreateManualTransactionCommand;
import com.kynsoft.finamer.creditcard.application.command.manualTransaction.create.CreateManualTransactionMessage;
import com.kynsoft.finamer.creditcard.application.command.manualTransaction.create.CreateManualTransactionRequest;
import com.kynsoft.finamer.creditcard.application.command.refundTransaction.create.CreateRefundTransactionCommand;
import com.kynsoft.finamer.creditcard.application.command.refundTransaction.create.CreateRefundTransactionMessage;
import com.kynsoft.finamer.creditcard.application.command.refundTransaction.create.CreateRefundTransactionRequest;
import com.kynsoft.finamer.creditcard.application.command.sendMail.SendMailCommand;
import com.kynsoft.finamer.creditcard.application.command.sendMail.SendMailMessage;
import com.kynsoft.finamer.creditcard.application.command.sendMail.SendMailRequest;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.TransactionResponse;
import com.kynsoft.finamer.creditcard.application.query.transaction.getById.FindTransactionByIdQuery;
import com.kynsoft.finamer.creditcard.application.query.transaction.search.GetSearchTransactionQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final IMediator mediator;

    public TransactionController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("/manual")
    public ResponseEntity<?> createManual(@RequestBody CreateManualTransactionRequest request) {
        CreateManualTransactionCommand command = CreateManualTransactionCommand.fromRequest(request);
        CreateManualTransactionMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/adjustment")
    public ResponseEntity<?> createAdjustment(@RequestBody CreateAdjustmentTransactionRequest request) {
        CreateAdjustmentTransactionCommand command = CreateAdjustmentTransactionCommand.fromRequest(request);
        CreateAdjustmentTransactionMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refund")
    public ResponseEntity<?> createRefund(@RequestBody CreateRefundTransactionRequest request) {
        CreateRefundTransactionCommand command = CreateRefundTransactionCommand.fromRequest(request);
        CreateRefundTransactionMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        FindTransactionByIdQuery query = new FindTransactionByIdQuery(id);
        TransactionResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);
        GetSearchTransactionQuery query = new GetSearchTransactionQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend/email/payment-link")
    public ResponseEntity<?> send(@RequestBody SendMailRequest request) {
        SendMailCommand command = SendMailCommand.fromRequest(request);
        SendMailMessage response = this.mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/redirectTypeLink")
    public ResponseEntity<?> getPaymentForm(@RequestBody CreateRedirectTransactionPaymentRequest request) {

        CreateRedirectTransactionPaymentCommand redirectTransactionPaymentCommand = CreateRedirectTransactionPaymentCommand.builder()
                .Token(request.getToken())
                .build();
        CreateRedirectTransactionPaymentCommandMessage createRedirectCommandMessage = mediator.send(redirectTransactionPaymentCommand);
        return ResponseEntity.ok(createRedirectCommandMessage);
    }

    @PostMapping("/updateTransactionPayment")
    public ResponseEntity<?> updateTransactionPayment(@RequestBody UpdateManageStatusTransactionCommandRequest request) {
        UpdateManageStatusTransactionCommand updateManageStatusTransactionCommandRequest = UpdateManageStatusTransactionCommand.builder()
                .session(request.getSession())
                .build();

        UpdateManageStatusTransactionCommandMessage response = mediator.send(updateManageStatusTransactionCommandRequest);
        return ResponseEntity.ok(response);
    }

}
