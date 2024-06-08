package com.kynsoft.notification.controller;

import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.notification.application.command.advertisingcontent.create.CreateAdvertisingContentCommand;
import com.kynsoft.notification.application.command.advertisingcontent.create.CreateAdvertisingContentMessage;
import com.kynsoft.notification.application.command.advertisingcontent.create.CreateAdvertisingContentRequest;
import com.kynsoft.notification.application.command.advertisingcontent.delete.DeleteAdvertisingContentCommand;
import com.kynsoft.notification.application.command.advertisingcontent.delete.DeleteAdvertisingContentMessage;
import com.kynsoft.notification.application.command.advertisingcontent.update.UpdateAdvertisingContentCommand;
import com.kynsoft.notification.application.command.advertisingcontent.update.UpdateAdvertisingContentMessage;
import com.kynsoft.notification.application.command.advertisingcontent.update.UpdateAdvertisingContentRequest;
import com.kynsoft.notification.application.query.advertisingcontent.getById.AdvertisingContentResponse;
import com.kynsoft.notification.application.query.advertisingcontent.getById.FindAdvertisingContentByIdQuery;
import com.kynsoft.notification.application.query.advertisingcontent.search.GetSearchAdvertisingContentQuery;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/advertising-content")
public class AdvertisingContentController {

    private final IMediator mediator;

    @Autowired
    public AdvertisingContentController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<CreateAdvertisingContentMessage> create(@RequestBody CreateAdvertisingContentRequest request)  {
        CreateAdvertisingContentCommand createCommand = CreateAdvertisingContentCommand.fromRequest(request);
        CreateAdvertisingContentMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindAdvertisingContentByIdQuery query = new FindAdvertisingContentByIdQuery(id);
        AdvertisingContentResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request)
    {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());
        GetSearchAdvertisingContentQuery query = new GetSearchAdvertisingContentQuery(pageable, request.getFilter(),request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UpdateAdvertisingContentMessage> update(@PathVariable("id") UUID id,@RequestBody UpdateAdvertisingContentRequest request) {

        UpdateAdvertisingContentCommand command = UpdateAdvertisingContentCommand.fromRequest(request,id);
        UpdateAdvertisingContentMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {

        DeleteAdvertisingContentCommand query = new DeleteAdvertisingContentCommand(id);
        DeleteAdvertisingContentMessage response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

}
