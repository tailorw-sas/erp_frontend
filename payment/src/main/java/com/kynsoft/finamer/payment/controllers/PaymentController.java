package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.payment.applyPayment.ApplyPaymentCommand;
import com.kynsoft.finamer.payment.application.command.payment.applyPayment.ApplyPaymentMessage;
import com.kynsoft.finamer.payment.application.command.payment.applyPayment.ApplyPaymentRequest;
import com.kynsoft.finamer.payment.application.command.payment.changeAttachmentStatus.ChangeAttachmentStatusCommand;
import com.kynsoft.finamer.payment.application.command.payment.changeAttachmentStatus.ChangeAttachmentStatusMessage;
import com.kynsoft.finamer.payment.application.command.payment.changeAttachmentStatus.ChangeAttachmentStatusRequest;
import com.kynsoft.finamer.payment.application.command.payment.create.CreatePaymentCommand;
import com.kynsoft.finamer.payment.application.command.payment.create.CreatePaymentMessage;
import com.kynsoft.finamer.payment.application.command.payment.create.CreatePaymentRequest;
import com.kynsoft.finamer.payment.application.command.payment.delete.DeletePaymentCommand;
import com.kynsoft.finamer.payment.application.command.payment.delete.DeletePaymentMessage;
import com.kynsoft.finamer.payment.application.command.payment.setVariables.SetCommand;
import com.kynsoft.finamer.payment.application.command.payment.setVariables.SetMessage;
import com.kynsoft.finamer.payment.application.command.payment.update.UpdatePaymentCommand;
import com.kynsoft.finamer.payment.application.command.payment.update.UpdatePaymentMessage;
import com.kynsoft.finamer.payment.application.command.payment.update.UpdatePaymentRequest;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentProjectionResponse;
import com.kynsoft.finamer.payment.application.query.objectResponse.PaymentResponse;
import com.kynsoft.finamer.payment.application.query.payment.countByAgency.CountAgencyByPaymentBalanceAndDepositBalanceQuery;
import com.kynsoft.finamer.payment.application.query.payment.countByAgency.CountAgencyByPaymentBalanceAndDepositBalanceResponse;
import com.kynsoft.finamer.payment.application.query.payment.excelExporter.GetPaymentExcelExporterQuery;
import com.kynsoft.finamer.payment.application.query.payment.excelExporter.PaymentExcelExporterResponse;
import com.kynsoft.finamer.payment.application.query.payment.excelExporter.SearchExcelExporter;
import com.kynsoft.finamer.payment.application.query.payment.getByGenCodeProjection.FindPaymentByGenProjectionQuery;
import com.kynsoft.finamer.payment.application.query.payment.getById.FindPaymentByIdQuery;
import com.kynsoft.finamer.payment.application.query.payment.search.GetSearchPaymentQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final IMediator mediator;

    public PaymentController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreatePaymentMessage> create(@RequestBody CreatePaymentRequest request) {
        CreatePaymentCommand createCommand = CreatePaymentCommand.fromRequest(request, mediator);
        CreatePaymentMessage response = mediator.send(createCommand);
//
//        FindPaymentByIdQuery query = new FindPaymentByIdQuery(response.getPayment().getId());
//        PaymentResponse paymentResponse = mediator.send(query);

//        response.setPayment(paymentResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/apply-payment")
    public ResponseEntity<ApplyPaymentMessage> applyPayment(@RequestBody ApplyPaymentRequest request) {
        ApplyPaymentCommand createCommand = ApplyPaymentCommand.fromRequest(request, mediator);
        ApplyPaymentMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/change-attachment-status")
    public ResponseEntity<ChangeAttachmentStatusMessage> attachmentStatus(@RequestBody ChangeAttachmentStatusRequest request) {
        ChangeAttachmentStatusCommand createCommand = ChangeAttachmentStatusCommand.fromRequest(request);
        ChangeAttachmentStatusMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeletePaymentCommand command = new DeletePaymentCommand(id);
        DeletePaymentMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdatePaymentRequest request) {

        UpdatePaymentCommand command = UpdatePaymentCommand.fromRequest(request, id);
        UpdatePaymentMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindPaymentByIdQuery query = new FindPaymentByIdQuery(id);
        PaymentResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping(path = "/set")
    public ResponseEntity<?> getSet() {

        SetCommand query = new SetCommand();
        SetMessage response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/projection/{id}")
    public ResponseEntity<?> getByGenProjection(@PathVariable long id) {

        FindPaymentByGenProjectionQuery query = new FindPaymentByGenProjectionQuery(id);
        PaymentProjectionResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/agency/{agencyId}")
    public ResponseEntity<?> CountAgencyByPaymentBalanceAndDepositBalance(@PathVariable UUID agencyId) {

        CountAgencyByPaymentBalanceAndDepositBalanceQuery query = new CountAgencyByPaymentBalanceAndDepositBalanceQuery(agencyId);
        CountAgencyByPaymentBalanceAndDepositBalanceResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchPaymentQuery query = new GetSearchPaymentQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PostMapping("/excel-exporter")
    public ResponseEntity<?> excelExporter(@RequestBody SearchExcelExporter request) {
        Pageable pageable = PageableUtil.createPageable(request.getSearch());

        GetPaymentExcelExporterQuery query = new GetPaymentExcelExporterQuery(pageable, request.getSearch().getFilter(), request.getSearch().getQuery(), request.getFileName());
        PaymentExcelExporterResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

}
