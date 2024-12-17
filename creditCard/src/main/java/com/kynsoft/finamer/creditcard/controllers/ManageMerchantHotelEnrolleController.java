package com.kynsoft.finamer.creditcard.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.create.CreateManagerMerchantHotelEnrolleCommand;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.create.CreateManagerMerchantHotelEnrolleMessage;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.create.CreateManagerMerchantHotelEnrolleRequest;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.delete.DeleteManagerMerchantHotelEnrolleCommand;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.delete.DeleteManagerMerchantHotelEnrolleMessage;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.update.UpdateManagerMerchantHotelEnrolleCommand;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.update.UpdateManagerMerchantHotelEnrolleMessage;
import com.kynsoft.finamer.creditcard.application.command.manageMerchantHotelEnrolle.update.UpdateManagerMerchantHotelEnrolleRequest;
import com.kynsoft.finamer.creditcard.application.query.manageMerchantHotelEnrolle.getById.FindManageMerchantHotelEnrolleByIdQuery;
import com.kynsoft.finamer.creditcard.application.query.manageMerchantHotelEnrolle.search.GetSearchManageMerchantHotelEnrolleQuery;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageMerchantHotelEnrolleResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manage-merchant-hotel-enrolle")
public class ManageMerchantHotelEnrolleController {

    private final IMediator mediator;

    public ManageMerchantHotelEnrolleController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateManagerMerchantHotelEnrolleMessage> create(@RequestBody CreateManagerMerchantHotelEnrolleRequest request) {
        CreateManagerMerchantHotelEnrolleCommand createCommand = CreateManagerMerchantHotelEnrolleCommand.fromRequest(request);
        CreateManagerMerchantHotelEnrolleMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindManageMerchantHotelEnrolleByIdQuery query = new FindManageMerchantHotelEnrolleByIdQuery(id);
        ManageMerchantHotelEnrolleResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteManagerMerchantHotelEnrolleCommand command = new DeleteManagerMerchantHotelEnrolleCommand(id);
        DeleteManagerMerchantHotelEnrolleMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchManageMerchantHotelEnrolleQuery query = new GetSearchManageMerchantHotelEnrolleQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody UpdateManagerMerchantHotelEnrolleRequest request) {

        UpdateManagerMerchantHotelEnrolleCommand command = UpdateManagerMerchantHotelEnrolleCommand.fromRequest(request, id);
        UpdateManagerMerchantHotelEnrolleMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
