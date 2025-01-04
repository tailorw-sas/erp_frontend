package com.kynsoft.report.controller;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.jasperReportParameter.create.CreateJasperReportParameterCommand;
import com.kynsoft.report.applications.command.jasperReportParameter.create.CreateJasperReportParameterMessage;
import com.kynsoft.report.applications.command.jasperReportParameter.create.CreateJasperReportParameterRequest;
import com.kynsoft.report.applications.command.jasperReportParameter.delete.DeletedJasperReportParameterCommand;
import com.kynsoft.report.applications.command.jasperReportParameter.delete.DeletedJasperReportParameterMessage;
import com.kynsoft.report.applications.command.jasperReportParameter.update.UpdateJasperReportParameterCommand;
import com.kynsoft.report.applications.command.jasperReportParameter.update.UpdateJasperReportParameterMessage;
import com.kynsoft.report.applications.command.jasperReportParameter.update.UpdateJasperReportParameterRequest;
import com.kynsoft.report.applications.command.jasperReportTemplate.create.CreateJasperReportTemplateCommand;
import com.kynsoft.report.applications.command.jasperReportTemplate.create.CreateJasperReportTemplateMessage;
import com.kynsoft.report.applications.command.jasperReportTemplate.create.CreateJasperReportTemplateRequest;
import com.kynsoft.report.applications.command.jasperReportTemplate.delete.DeleteJasperReportTemplateCommand;
import com.kynsoft.report.applications.command.jasperReportTemplate.delete.DeleteJasperReportTemplateMessage;
import com.kynsoft.report.applications.command.jasperReportTemplate.update.UpdateJasperReportTemplateCommand;
import com.kynsoft.report.applications.command.jasperReportTemplate.update.UpdateJasperReportTemplateMessage;
import com.kynsoft.report.applications.command.jasperReportTemplate.update.UpdateJasperReportTemplateRequest;
import com.kynsoft.report.applications.query.jasperReportParameter.getById.JasperReportParameterByIdQuery;
import com.kynsoft.report.applications.query.jasperReportParameter.getById.JasperReportParameterResponse;
import com.kynsoft.report.applications.query.jasperReportParameter.search.GetSearchJasperReportParameterQuery;
import com.kynsoft.report.applications.query.jasperreporttemplate.getbyid.FindJasperReportTemplateByIdQuery;
import com.kynsoft.report.applications.query.jasperreporttemplate.getbyid.JasperReportTemplateResponse;
import com.kynsoft.report.applications.query.jasperreporttemplate.search.GetJasperReportTemplateQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/jasper-report-template-parameter")
public class JasperReportTemplateParameterController {

    private final IMediator mediator;

    @Autowired
    public JasperReportTemplateParameterController(IMediator mediator) {
        this.mediator = mediator;
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateJasperReportParameterRequest request) {
        CreateJasperReportParameterCommand createCommand = CreateJasperReportParameterCommand.fromRequest(request);
        CreateJasperReportParameterMessage response = mediator.send(createCommand);

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {

        JasperReportParameterByIdQuery query = new JasperReportParameterByIdQuery(id);
        JasperReportParameterResponse response = mediator.send(query);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/search")
    public ResponseEntity<PaginatedResponse> search(@RequestBody SearchRequest request) {
        Pageable pageable = PageableUtil.createPageable(request);

        GetSearchJasperReportParameterQuery query = new GetSearchJasperReportParameterQuery(pageable, request.getFilter(), request.getQuery());
        PaginatedResponse data = mediator.send(query);

        return ResponseEntity.ok(data);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") UUID id,
            @RequestBody UpdateJasperReportParameterRequest request) {

        UpdateJasperReportParameterCommand command = UpdateJasperReportParameterCommand.fromRequest(request, id);
        UpdateJasperReportParameterMessage response = mediator.send(command);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {

        DeletedJasperReportParameterCommand query = new DeletedJasperReportParameterCommand(id);
        DeletedJasperReportParameterMessage response = mediator.send(query);
        return ResponseEntity.ok(response);
    }

}
