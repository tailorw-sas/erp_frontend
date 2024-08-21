package com.kynsoft.report.applications.command.generateTemplate;

import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class GenerateTemplateCommandHandler implements ICommandHandler<GenerateTemplateCommand> {
    private static final Logger logger = LoggerFactory.getLogger(GenerateTemplateCommandHandler.class);
    private final RestTemplate restTemplate;
    private final IJasperReportTemplateService jasperReportTemplateService;

    public GenerateTemplateCommandHandler(RestTemplate restTemplate,
                                          IJasperReportTemplateService jasperReportTemplateService) {
        this.restTemplate = restTemplate;
        this.jasperReportTemplateService = jasperReportTemplateService;
    }

    @Override
    public void handle(GenerateTemplateCommand command) {
        JasperReportTemplateDto reportTemplateDto = jasperReportTemplateService.findByTemplateCode(command.getJasperReportCode());
//

        try {

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
        logger.error("Antes de crear  la base de datos: {}",    reportTemplateDto.getDbConection().getName());
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
        query = replaceQueryParameters(query, parameters);
        // Execute query with named parameters
        logger.error("Base de datos: {}", query);
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
      //  Resource resource = resourceLoader.getResource(reportPath);
       // InputStream jrxmlInput = resource.getInputStream();
        logger.error("Fetching JRXML template from URL: {}", reportPath);
       InputStream jrxmlInput = new ByteArrayInputStream(Objects.requireNonNull(restTemplate.getForObject(reportPath, byte[].class)));
        logger.error("Jrxml content loaded: {}", jrxmlInput.available());
       return JasperCompileManager.compileReport(jrxmlInput);
    }

    private DataSource createDataSource(String url, String username, String password) {
        logger.error("Base de datos: {}", url);
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    private   String replaceQueryParameters(String query, Map<String, Object> parameters) {
        // Regex pattern to match ::parameterName
        Pattern pattern = Pattern.compile("::([a-zA-Z]\\w*)");
        Matcher matcher = pattern.matcher(query);

        // StringBuffer to hold the modified query
        StringBuffer resultQuery = new StringBuffer();

        while (matcher.find()) {
            String paramName = matcher.group(1); // Extract the parameter name without the colons
            Object value = parameters.get(paramName); // Get the value from the map

            if (value == null) {
                throw new IllegalArgumentException("Parameter " + paramName + " not found in the parameters map.");
            }

            // Replace the parameter with its value in the query
            matcher.appendReplacement(resultQuery, value.toString());
        }
        matcher.appendTail(resultQuery);

        return resultQuery.toString();
    }


}