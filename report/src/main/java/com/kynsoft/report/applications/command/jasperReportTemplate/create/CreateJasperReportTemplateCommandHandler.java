package com.kynsoft.report.applications.command.jasperReportTemplate.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.report.domain.dto.DBConectionDto;
import com.kynsoft.report.domain.dto.JasperReportParameterDto;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.rules.jasperReport.ManageJasperReportCodeMustBeUniqueRule;
import com.kynsoft.report.domain.rules.jasperReport.ManageReportCodeMustBeNullRule;
import com.kynsoft.report.domain.rules.jasperReport.ManageReportNameMustBeNullRule;
import com.kynsoft.report.domain.rules.jasperReport.ManageReportParentIndexMustBeNullRule;
import com.kynsoft.report.domain.services.IDBConnectionService;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import com.kynsoft.report.domain.services.IReportParameterService;
import com.kynsoft.report.domain.services.IReportService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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
        RulesChecker.checkRule(new ManageReportParentIndexMustBeNullRule(command.getParentIndex()));
        RulesChecker.checkRule(new ManageJasperReportCodeMustBeUniqueRule(this.service, command.getCode(), command.getId()));

        DBConectionDto dbConectionDto = command.getDbConection() != null ? this.connectionService.findById(command.getDbConection()) : null;
        JasperReportTemplateDto reportTemplateDto = new JasperReportTemplateDto(
                command.getId(),
                command.getCode(),
                command.getName(),
                command.getDescription(),
                command.getFile(),
                command.getType(),
                command.getParentIndex(),
                command.getMenuPosition(),
                command.getLanPath(),
                command.getWeb(),
                command.getSubMenu(),
                command.getSendEmail(),
                command.getInternal(),
                command.getHighRisk(),
                command.getVisible(),
                command.getCancel(),
                command.getRootIndex(),
                command.getLanguage(),
                command.getStatus(),
                dbConectionDto,
                command.getQuery()
        );
        UUID id = this.service.create(reportTemplateDto);

        addParameters(command.getFile(), reportTemplateDto);
        command.setId(id);

    }

    private void addParameters(String fileUrl, JasperReportTemplateDto reportTemplateDto) {
        byte[] data = restTemplate.getForObject(fileUrl, byte[].class);
        InputStream inputStream = new ByteArrayInputStream(Objects.requireNonNull(data));
        JasperReport jasperReport = null;
        try {
            jasperReport = JasperCompileManager.compileReport(inputStream);
        } catch (JRException e) {
            throw new RuntimeException(e);
        }

        // Preparar el mapa para almacenar los detalles de los parámetros
        Map<String, Map<String, String>> paramMap = new HashMap<>();
        for (JRParameter param : jasperReport.getParameters()) {
            if (!param.isSystemDefined() && param.isForPrompting()) { // Solo parámetros definidos por el usuario y que son promptables
                Map<String, String> details = new HashMap<>();
                details.put("description", param.getDescription() != null ? param.getDescription() : "No description");
                details.put("type", param.getValueClassName());  // Añadir tipo de dato
                paramMap.put(param.getName(), details);

                this.reportParameterService.create(new JasperReportParameterDto(
                        UUID.randomUUID(), param.getName(), param.getValueClassName(), "",
                        "", "","", reportTemplateDto
                ));
            }
        }
    }
}
