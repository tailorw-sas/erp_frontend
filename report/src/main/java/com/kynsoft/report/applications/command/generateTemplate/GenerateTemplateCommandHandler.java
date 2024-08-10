package com.kynsoft.report.applications.command.generateTemplate;

import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import com.kynsoft.report.domain.services.IReportService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class GenerateTemplateCommandHandler implements ICommandHandler<GenerateTemplateCommand> {

    private final ResourceLoader resourceLoader;
    private final IReportService reportService;
    private final RestTemplate restTemplate;
    private final IJasperReportTemplateService jasperReportTemplateService;

    public GenerateTemplateCommandHandler(IReportService reportService, RestTemplate restTemplate, IJasperReportTemplateService jasperReportTemplateService, ResourceLoader resourceLoader) {
        this.reportService = reportService;
        this.restTemplate = restTemplate;
        this.jasperReportTemplateService = jasperReportTemplateService;
        this.resourceLoader = resourceLoader;
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
   //  byte [] response = reportService.generatePdfReport(command.getParameters(),reportTemplateDto.getFile(), new JREmptyDataSource());
       // command.setResult(response);

        try {
            // Set the path to your JRXML template
           // String reportPath = "classpath:templates/Payment.jrxml"; // Ensure the path is correct

            // Create parameters for the report
//            Map<String, Object> parameters = new HashMap<>();
//            parameters.put("fechayHora", Date.valueOf("2024-08-03")); // Use java.sql.Date
//            parameters.put("idPayment", Long.valueOf("1"));

            // Generate the PDF report
            byte[] response = generatePdfReport(command.getParameters(), reportTemplateDto.getFile(), reportTemplateDto);

            // Set the result in the command
            command.setResult(response);

        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
        }
    }

    public byte[] generatePdfReport(Map<String, Object> parameters, String reportPath, JasperReportTemplateDto reportTemplateDto) throws JRException, IOException {
        // Load the JRXML template
        JasperReport jasperReport = getJasperReport(reportPath);

        // Create and configure the data source
        JdbcTemplate jdbcTemplate = getJdbcTemplate(reportTemplateDto);

//        // Obtain the parameters
//        Long idPayment = (Long) parameters.get("idPayment");
//        Date fechayHora = (Date) parameters.get("fechayHora");

        // Fetch data from the database using the SQL function
       // String query = "SELECT * FROM generate_payment_report('5da74855-245a-4e07-ba06-2717f6c2f302')";
        String query = reportTemplateDto.getQuery() != null ? reportTemplateDto.getQuery() : "";
       // List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, idPayment, fechayHora);

        // Use NamedParameterJdbcTemplate for named parameters
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        // Create a map for named parameters
//        Map<String, Object> namedParameters = new HashMap<>();
//        namedParameters.put("idPayment", idPayment);
//        namedParameters.put("fechayHora", fechayHora);

        // Execute query with named parameters
        List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(query, parameters);

        // Convert data to JRDataSource
        JRDataSource jrDataSource = new JRBeanCollectionDataSource(rows);

        // Fill the report
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrDataSource);

        // Export to PDF
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private JdbcTemplate getJdbcTemplate(JasperReportTemplateDto reportTemplateDto) {
        //DataSource dataSource = createDataSource("jdbc:postgresql://localhost:6432/finamer-payments", "finamer_rw", "5G30y1cXz89cA1yc0gCE3OhhBLQkvUTV2icqz5qNRQGq4cbM5F0bc");
        DataSource dataSource = createDataSource(reportTemplateDto.getDbConection().getUrl(), reportTemplateDto.getDbConection().getUsername(),
                reportTemplateDto.getDbConection().getPassword());
        return new JdbcTemplate(dataSource);
    }

    private JasperReport getJasperReport(String reportPath) throws JRException, IOException {
        Resource resource = resourceLoader.getResource(reportPath);
        InputStream jrxmlInput = resource.getInputStream();
        //InputStream jrxmlInput = new ByteArrayInputStream(Objects.requireNonNull(restTemplate.getForObject(reportPath, byte[].class)));
        return JasperCompileManager.compileReport(jrxmlInput);
    }

    private DataSource createDataSource(String url, String username, String password) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}