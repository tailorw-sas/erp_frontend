package com.kynsoft.notification.controller;

import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.notification.application.query.file.delete.DeleteFileCommand;
import com.kynsoft.notification.application.query.file.delete.DeleteFileMessage;
import com.kynsoft.notification.application.query.file.getbyid.FindByIdAFileQuery;
import com.kynsoft.notification.application.query.file.search.FileResponse;
import com.kynsoft.notification.application.query.file.search.GetSearchAFileQuery;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data/files")
public class DataFileController {

    private final IMediator mediator;

    public DataFileController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize());
        GetSearchAFileQuery query = new GetSearchAFileQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);
        return ResponseEntity.ok(data);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<FileResponse> getById(@PathVariable UUID id) {

        FindByIdAFileQuery query = new FindByIdAFileQuery(id);
        FileResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {

        DeleteFileCommand query = new DeleteFileCommand(id);
        DeleteFileMessage response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

}
