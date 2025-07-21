package com.kynsoft.report.applications.command.jasperReportTemplate.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.DBConectionDto;
import com.kynsoft.report.domain.dto.JasperReportParameterDto;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.rules.jasperReport.ManageJasperReportCodeMustBeUniqueRule;
import com.kynsoft.report.domain.rules.jasperReport.ManageReportCodeMustBeNullRule;
import com.kynsoft.report.domain.rules.jasperReport.ManageReportNameMustBeNullRule;
import com.kynsoft.report.domain.services.IDBConnectionService;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import com.kynsoft.report.domain.services.IReportParameterService;
import com.kynsoft.report.infrastructure.enums.JasperParameterCategory;
import net.sf.jasperreports.engine.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class CreateJasperReportTemplateCommandHandler implements ICommandHandler<CreateJasperReportTemplateCommand> {

    private final IJasperReportTemplateService service;
    private final IReportParameterService reportParameterService;
    private final IDBConnectionService connectionService;
    private final RestTemplate restTemplate;

    public CreateJasperReportTemplateCommandHandler(IJasperReportTemplateService service,
                                                    IReportParameterService reportParameterService,
                                                    IDBConnectionService connectionService, RestTemplate restTemplate) {
        this.service = service;
        this.reportParameterService = reportParameterService;
        this.connectionService = connectionService;
        this.restTemplate = restTemplate;
    }

    @Override
    public void handle(CreateJasperReportTemplateCommand command) {
        RulesChecker.checkRule(new ManageReportCodeMustBeNullRule(command.getCode()));
        RulesChecker.checkRule(new ManageReportNameMustBeNullRule(command.getName()));
        RulesChecker.checkRule(new ManageJasperReportCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        DBConectionDto dbConectionDto = command.getDbConnection() != null ? this.connectionService.findById(command.getDbConnection()) : null;
        JasperReportTemplateDto reportTemplateDto = new JasperReportTemplateDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getFile(),
                command.getType(),
                command.getMenuPosition(),
                command.getStatus(),
                dbConectionDto,
                ""
        );

        reportTemplateDto.setModuleSystems(command.getModuleSystems());
        UUID id = this.service.create(reportTemplateDto);

        try {
            addParameters(command.getFile(), reportTemplateDto);
        } catch (Exception exception) {
            System.out.println("ERROR al crear los parametros de reporte");
            System.out.println(exception.getMessage());
        }

        command.setId(id);
    }

    private void addParameters(String fileUrl, JasperReportTemplateDto reportTemplateDto) {
        byte[] data = restTemplate.getForObject(fileUrl, byte[].class);
        InputStream inputStream = new ByteArrayInputStream(Objects.requireNonNull(data));
        JasperReport jasperReport;

        try {
            jasperReport = JasperCompileManager.compileReport(inputStream);
        } catch (JRException e) {
            throw new RuntimeException("Error compiling Jasper report: " + e.getMessage(), e);
        }

        // Crear un mapa vacío de parámetros
        Map<String, Object> parameters = new HashMap<>();

        // Llenar el reporte con un datasource vacío para evaluar expresiones
        try {
            JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
        } catch (JRException e) {
            throw new RuntimeException("Error filling Jasper report: " + e.getMessage(), e);
        }

        // Iterar sobre los parámetros obtenidos del JasperReport original
        for (JRParameter param : jasperReport.getParameters()) {
            if (!param.isSystemDefined() && param.isForPrompting()) {
                try {
                    Object paramValue = parameters.getOrDefault(param.getName(), "");
                    if (paramValue instanceof Date) {
                        paramValue = new SimpleDateFormat("MM/dd/yyyy").format((Date) paramValue);
                    }

                    this.reportParameterService.create(new JasperReportParameterDto(
                            UUID.randomUUID(), param.getName(), param.getValueClassName(),
                            paramValue.toString(), "", "", "", reportTemplateDto, 0,
                            "", "", "", JasperParameterCategory.REPORT
                    ));

                } catch (Exception e) {
                    System.out.println("Error processing parameter: " + param.getName() + " - " + e.getMessage());
                }
            }
        }
    }
}
