package com.kynsoft.report.applications.command.generateTemplate;

import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import com.kynsoft.report.domain.services.IReportService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import net.sf.jasperreports.engine.JREmptyDataSource;
import org.springframework.stereotype.Component;

@Component
public class GenerateTemplateCommandHandler implements ICommandHandler<GenerateTemplateCommand> {

 private final IReportService reportService;
 private final IJasperReportTemplateService jasperReportTemplateService;

    public GenerateTemplateCommandHandler(IReportService reportService, IJasperReportTemplateService jasperReportTemplateService) {

        this.reportService = reportService;
        this.jasperReportTemplateService = jasperReportTemplateService;
    }

    @Override
    public void handle(GenerateTemplateCommand command) {
        JasperReportTemplateDto reportTemplateDto = jasperReportTemplateService.findByTemplateCode(command.getJasperReportCode());
//        String jrxmlUrl = "http://d2cebw6tssfqem.cloudfront.net/cita_2024-04-17_11-38-05.jrxml";
//       Map<String, Object> parameters = new HashMap<>();
//        parameters.put("logo", "http://d3ksvzqyx4up5m.cloudfront.net/Ttt_2024-03-14_19-03-33.png");
//        parameters.put("cita", "111111");
//        parameters.put("nombres", "Keimer Montes Oliver");
//        parameters.put("identidad", "0961881992");
//        parameters.put("fecha", "2024-04-23");
//        parameters.put("hora", "10:40");
//        parameters.put("servicio", "GINECOLOGIA");
//        parameters.put("tipo", "CONSULTA EXTERNA");
//        parameters.put("direccion", "Calle 48");
//        parameters.put("lugar", "HOSPITAL MILITAR");
//        parameters.put("fecha_registro", "2024-04-23 10:40");
//        parameters.put("URL_QR", "http://d3ksvzqyx4up5m.cloudfront.net/Ttt_2024-03-14_19-03-33.png");
        byte [] response = reportService.generatePdfReport(command.getParameters(),reportTemplateDto.getTemplateContentUrl(), new JREmptyDataSource());
        command.setResult(response);
    }
}
