package com.kynsoft.finamer.insis.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.booking.updateResponseBooking.UpdateResponseImportBookingCommand;
import com.kynsoft.finamer.insis.application.command.booking.updateResponseBooking.UpdateResponseImportBookingMessage;
import com.kynsoft.finamer.insis.application.command.booking.updateResponseBooking.UpdateResponseImportBookingRequest;
import com.kynsoft.finamer.insis.application.command.roomRate.updateResponseImportRoomRate.UpdateResponseImportRoomRateCommand;
import com.kynsoft.finamer.insis.application.command.roomRate.updateResponseImportRoomRate.UpdateResponseImportRoomRateMessage;
import com.kynsoft.finamer.insis.application.command.roomRate.updateResponseImportRoomRate.UpdateResponseImportRoomRateRequest;
import com.kynsoft.finamer.insis.application.query.importProcess.getErrorResults.GetErrorResultsImportProcessQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/import-room-rate")
public class ImportRoomRateController {

    private final IMediator mediator;

    public ImportRoomRateController(IMediator mediator){
        this.mediator = mediator;
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchErrorsById(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetErrorResultsImportProcessQuery query = new GetErrorResultsImportProcessQuery(
                UUID.fromString(request.getQuery()),
                pageable);
        PaginatedResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateBookinsResponse(@RequestBody UpdateResponseImportBookingRequest request){
        UpdateResponseImportBookingCommand command = UpdateResponseImportBookingCommand.fromRequest(request);
        UpdateResponseImportBookingMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

}
