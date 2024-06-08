package com.kynsoft.finamer.payment.controllers;

import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.finamer.payment.application.command.test.create.CreateTestCommand;
import com.kynsoft.finamer.payment.application.command.test.create.CreateTestRequest;
import com.kynsoft.finamer.payment.application.command.test.update.UpdateTestCommand;
import com.kynsoft.finamer.payment.application.command.test.update.UpdateTestMessage;
import com.kynsoft.finamer.payment.application.query.getById.FindTestByIdQuery;
import com.kynsoft.finamer.payment.application.query.objectResponse.TestResponse;
import com.kynsoft.finamer.payment.application.query.search.GetSearchTestQuery;
import com.kynsoft.finamer.payment.application.command.test.create.CreateTestMessage;
import com.kynsoft.finamer.payment.application.command.test.delete.DeleteTestCommand;
import com.kynsoft.finamer.payment.application.command.test.delete.DeleteTestMessage;

import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final IMediator mediator;

    public TestController(IMediator mediator) {

        this.mediator = mediator;
    }

    @PostMapping()
    public ResponseEntity<CreateTestMessage> create(@RequestBody CreateTestRequest request) {
        CreateTestCommand createCommand = CreateTestCommand.fromRequest(request);
        CreateTestMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        FindTestByIdQuery query = new FindTestByIdQuery(id);
        TestResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {

        DeleteTestCommand command = new DeleteTestCommand(id);
        DeleteTestMessage response = mediator.send(command);

        return ResponseEntity.ok(response);
    }

    
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize())
                .withSort(Sort.by("userName").ascending());

        GetSearchTestQuery query = new GetSearchTestQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody CreateTestRequest request) {

        UpdateTestCommand command = UpdateTestCommand.fromRequest(request, id);
        UpdateTestMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }
}
