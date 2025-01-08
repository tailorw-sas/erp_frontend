package com.kynsoft.finamer.insis.controllers;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.create.CreateInnsistHotelRoomTypeCommand;
import com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.create.CreateInnsistHotelRoomTypeMessage;
import com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.create.CreateInnsistHotelRoomTypeRequest;
import com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.delete.DeleteTradingCompanyHotelCommand;
import com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.delete.DeleteTradingCompanyHotelMessage;
import com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.update.UpdateInnsistHotelRoomTypeCommand;
import com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.update.UpdateInnsistHotelRoomTypeMessage;
import com.kynsoft.finamer.insis.application.command.innsistHotelRoomType.update.UpdateInnsistHotelRoomTypeRequest;
import com.kynsoft.finamer.insis.application.query.innsistHotelRoomType.getById.FindInnsistHotelRoomTypeByIdQuery;
import com.kynsoft.finamer.insis.application.query.innsistHotelRoomType.search.GetInnsistHotelRoomTypeQuery;
import com.kynsoft.finamer.insis.application.query.objectResponse.innsistHotelRoomType.InnsistHotelRoomTypeResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/innsist-trading-company-hotel")
public class InnsistTradingCompanyHotelController {

    private final IMediator mediator;

    public InnsistTradingCompanyHotelController(IMediator mediator){
        this.mediator = mediator;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        FindInnsistHotelRoomTypeByIdQuery query = new FindInnsistHotelRoomTypeByIdQuery(id);
        InnsistHotelRoomTypeResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateInnsistHotelRoomTypeRequest request){
        CreateInnsistHotelRoomTypeCommand command = CreateInnsistHotelRoomTypeCommand.fromRequest(request);
        CreateInnsistHotelRoomTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public ResponseEntity<?> update(@RequestBody UpdateInnsistHotelRoomTypeRequest request){
        UpdateInnsistHotelRoomTypeCommand command = UpdateInnsistHotelRoomTypeCommand.fromRequest(request);
        UpdateInnsistHotelRoomTypeMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id){
        DeleteTradingCompanyHotelCommand command = new DeleteTradingCompanyHotelCommand(id);
        DeleteTradingCompanyHotelMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    @PostMapping("search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request){
        Pageable pageable = PageableUtil.createPageable(request);

        GetInnsistHotelRoomTypeQuery query = new GetInnsistHotelRoomTypeQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);

        return ResponseEntity.ok(data);
    }

}
