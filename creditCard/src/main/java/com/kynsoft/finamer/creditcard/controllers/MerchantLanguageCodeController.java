package com.kynsoft.finamer.creditcard.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.merchantLanguageCode.create.CreateMerchantLanguageCodeCommand;
import com.kynsoft.finamer.creditcard.application.command.merchantLanguageCode.create.CreateMerchantLanguageCodeMessage;
import com.kynsoft.finamer.creditcard.application.command.merchantLanguageCode.create.CreateMerchantLanguageCodeRequest;
import com.kynsoft.finamer.creditcard.application.command.merchantLanguageCode.delete.DeleteMerchantLanguageCodeCommand;
import com.kynsoft.finamer.creditcard.application.command.merchantLanguageCode.delete.DeleteMerchantLanguageCodeMessage;
import com.kynsoft.finamer.creditcard.application.command.merchantLanguageCode.update.UpdateMerchantLanguageCodeCommand;
import com.kynsoft.finamer.creditcard.application.command.merchantLanguageCode.update.UpdateMerchantLanguageCodeMessage;
import com.kynsoft.finamer.creditcard.application.command.merchantLanguageCode.update.UpdateMerchantLanguageCodeRequest;
import com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.MerchantLanguageCodeResponse;
import com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.findLanguageByMerchant.FindLanguageByMerchantQuery;
import com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.findLanguageByMerchant.FindLanguageByMerchantResponse;
import com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.findMerchantLanguage.FindMerchantLanguageQuery;
import com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.findMerchantLanguage.FindMerchantLanguageRequest;
import com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.findMerchantLanguage.FindMerchantLanguageResponse;
import com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.getById.FindMerchantLanguageCodeByIdQuery;
import com.kynsoft.finamer.creditcard.application.query.merchantLanguageCode.search.GetSearchMerchantLanguageCodeQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/merchant-language-code")
public class MerchantLanguageCodeController {

    private final IMediator mediator;

    public MerchantLanguageCodeController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<?> create(@RequestBody CreateMerchantLanguageCodeRequest request) {

        CreateMerchantLanguageCodeCommand createCommand = CreateMerchantLanguageCodeCommand.fromRequest(request);
        CreateMerchantLanguageCodeMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindMerchantLanguageCodeByIdQuery query = new FindMerchantLanguageCodeByIdQuery(id);
        MerchantLanguageCodeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteMerchantLanguageCodeCommand command = new DeleteMerchantLanguageCodeCommand(id);
        DeleteMerchantLanguageCodeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {

        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchMerchantLanguageCodeQuery query = new GetSearchMerchantLanguageCodeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);

        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateMerchantLanguageCodeRequest request) {

        UpdateMerchantLanguageCodeCommand command = UpdateMerchantLanguageCodeCommand.fromRequest(request, id);
        UpdateMerchantLanguageCodeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/languages/{id}")
    public ResponseEntity<?> getLanguagesByMerchant(@PathVariable UUID id) {

        FindLanguageByMerchantQuery query = new FindLanguageByMerchantQuery(id);
        FindLanguageByMerchantResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/merchant-language")
    public ResponseEntity<?> getMerchantCode(@RequestBody FindMerchantLanguageRequest request) {

        FindMerchantLanguageQuery query = new FindMerchantLanguageQuery(request.getMerchantId(), request.getLanguageId());
        FindMerchantLanguageResponse data = mediator.send(query);

        return ResponseEntity.ok(data);
    }
}
