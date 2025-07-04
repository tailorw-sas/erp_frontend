package com.kynsoft.report.controller;

import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsoft.report.applications.command.jasperReportTemplate.create.CreateJasperReportTemplateCommand;
import com.kynsoft.report.applications.command.jasperReportTemplate.create.CreateJasperReportTemplateMessage;
import com.kynsoft.report.applications.command.jasperReportTemplate.create.CreateJasperReportTemplateRequest;
import com.kynsoft.report.applications.command.jasperReportTemplate.delete.DeleteJasperReportTemplateCommand;
import com.kynsoft.report.applications.command.jasperReportTemplate.delete.DeleteJasperReportTemplateMessage;
import com.kynsoft.report.applications.command.jasperReportTemplate.update.UpdateJasperReportTemplateCommand;
import com.kynsoft.report.applications.command.jasperReportTemplate.update.UpdateJasperReportTemplateMessage;
import com.kynsoft.report.applications.command.jasperReportTemplate.update.UpdateJasperReportTemplateRequest;
import com.kynsoft.report.applications.query.jasperreporttemplate.getbyid.FindJasperReportTemplateByIdQuery;
import com.kynsoft.report.applications.query.jasperreporttemplate.getbyid.JasperReportTemplateResponse;
import com.kynsoft.report.applications.query.jasperreporttemplate.search.GetJasperReportTemplateQuery;
import com.kynsoft.report.applications.query.menu.ReportMenuResponse;
import com.kynsoft.report.domain.dto.status.ModuleSystems;
import com.kynsoft.report.infrastructure.services.JasperReportTemplateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/report-menu")
public class ReportMenuController {

    private final IMediator mediator;
    private final JasperReportTemplateServiceImpl jasperReportTemplateService;

    @Autowired
    public ReportMenuController(IMediator mediator, JasperReportTemplateServiceImpl jasperReportTemplateService) {
        this.mediator = mediator;
        this.jasperReportTemplateService = jasperReportTemplateService;
    }

    @GetMapping("/grouped")
    public ResponseEntity<?> getGroupedReports() {
        Map<ModuleSystems, List<ReportMenuResponse>> groupedReports = jasperReportTemplateService.getGroupedAndOrderedReports();
        return ResponseEntity.ok(groupedReports);
    }

}
