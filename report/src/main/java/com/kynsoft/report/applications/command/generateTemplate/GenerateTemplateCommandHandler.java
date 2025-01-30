package com.kynsoft.report.applications.command.generateTemplate;

import com.kynsoft.report.domain.dto.DBConectionDto;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GenerateTemplateCommandHandler implements ICommandHandler<GenerateTemplateCommand> {
    private static final Logger logger = LoggerFactory.getLogger(GenerateTemplateCommandHandler.class);
    private final RestTemplate restTemplate;
    private final IJasperReportTemplateService jasperReportTemplateService;
    private final ResourceLoader resourceLoader;

    public GenerateTemplateCommandHandler(RestTemplate restTemplate,
                                          IJasperReportTemplateService jasperReportTemplateService,
                                          ResourceLoader resourceLoader) {
        this.restTemplate = restTemplate;
        this.jasperReportTemplateService = jasperReportTemplateService;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void handle(GenerateTemplateCommand command) {
        JasperReportTemplateDto reportTemplateDto = jasperReportTemplateService.findByTemplateCode(command.getJasperReportCode());
        try {
            byte[] response;
            if (Objects.equals(command.getReportFormatType(), "XLS")) {
                response = generateExcelReport(command.getParameters(), reportTemplateDto.getFile(), reportTemplateDto);
            } else {
                response = generatePdfReport(command.getParameters(), reportTemplateDto.getFile(), reportTemplateDto);
                System.out.println("Se genero el reporte");
                System.out.println(response.length);
            }
            command.setResult(response);
        } catch (Exception e) {
            logger.error("Error generating report: ", e);
            throw new RuntimeException(e);
        }
    }

    public byte[] generatePdfReport(Map<String, Object> parameters, String reportPath, JasperReportTemplateDto reportTemplateDto) {
        Connection connection = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            // Verificar que el DTO contiene información válida
            if (reportTemplateDto == null || reportTemplateDto.getDbConectionDto() == null) {
                throw new IllegalArgumentException("Database connection details are missing.");
            }

            // Cargar el reporte
            JasperReport jasperReport = loadJasperReportFromUrl(reportTemplateDto.getFile());
            if (jasperReport == null) {
                throw new IllegalArgumentException("Could not load JasperReport from provided URL.");
            }

            // Establecer la conexión con la base de datos
            DBConectionDto dbConnection = reportTemplateDto.getDbConectionDto();
            Class.forName("org.postgresql.Driver"); // Asegurar que el driver está cargado

            connection = DriverManager.getConnection(dbConnection.getUrl(),
                    dbConnection.getUsername(),
                    dbConnection.getPassword());

            // Llenar el reporte con los parámetros y la conexión
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            // Exportar el reporte a un stream en formato PDF
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

//            // Verifica el tamaño del archivo generado
            if (outputStream.size() > getMaxFileSize()) {
                throw new RuntimeException("The generated PDF report is too large. Size: " + outputStream.size() + " bytes.");
            }
            return outputStream.toByteArray();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC Driver not found. Ensure it is included in the classpath.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database. Verify credentials and connection settings.", e);
        } catch (JRException e) {
            throw new RuntimeException("Error generating the JasperReport.", e);
        } finally {
            // Cerrar la conexión a la base de datos
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.err.println("Error closing database connection: " + ex.getMessage());
                }
            }
        }
    }

    private JasperReport loadJasperReportFromUrl(String templateUrl) throws JRException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(new URL(templateUrl).openStream().readAllBytes())) {
            return JasperCompileManager.compileReport(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Error loading JRXML template from URL: " + templateUrl, e);
        }
    }

//    public byte[] generatePdfReport(Map<String, Object> parameters, String reportPath, JasperReportTemplateDto reportTemplateDto) throws JRException, IOException {
//        JasperReport jasperReport = getJasperReport(reportPath);
//        logger.info("Generating PDF report with database: {}", reportTemplateDto.getDbConectionDto().getName());
//
//        JRFileVirtualizer virtualizer = new JRFileVirtualizer(20, "temp/");
//        parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
//
//        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//            JdbcTemplate jdbcTemplate = getJdbcTemplate(reportTemplateDto);
//            String query = reportTemplateDto.getQuery() != null ? reportTemplateDto.getQuery() : "";
//            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
//
//            query = replaceQueryParameters(query, parameters);
//            List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(query, parameters);
//
//            JRDataSource jrDataSource = new JRBeanCollectionDataSource(rows);
//            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrDataSource);
//
//            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
//
//            // Verifica el tamaño del archivo generado
//            if (outputStream.size() > getMaxFileSize()) {
//                throw new RuntimeException("The generated PDF report is too large. Size: " + outputStream.size() + " bytes.");
//            }
//
//            return outputStream.toByteArray();
//        } catch (IOException | JRException e) {
//            logger.error("Error generating PDF report: {}", e.getMessage(), e);
//            throw e;
//        } finally {
//            virtualizer.cleanup();
//        }
//    }

    private int getMaxFileSize() {
        // Define el límite en bytes (por ejemplo, 50 MB)
        return 50 * 1024 * 1024; // 50 MB
    }

    public byte[] generateExcelReport(Map<String, Object> parameters, String reportPath, JasperReportTemplateDto reportTemplateDto) throws JRException, IOException {
        JRFileVirtualizer virtualizer = new JRFileVirtualizer(20, "temp/");
        parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             Connection connection = createConnection(reportTemplateDto)) {

            JasperReport jasperReport = getJasperReport(reportPath);
            logger.error("Generating Excel report with database: {}", reportTemplateDto.getDbConectionDto().getName());

            JdbcTemplate jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(connection, true));
            String query = reportTemplateDto.getQuery() != null ? reportTemplateDto.getQuery() : "";
            NamedParameterJdbcTemplate namedJdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
            query = replaceQueryParameters(query, parameters);
            List<Map<String, Object>> rows = namedJdbc.queryForList(query, parameters);

            JRDataSource jrDataSource = new JRBeanCollectionDataSource(rows);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrDataSource);

            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
            configuration.setDetectCellType(true);
            configuration.setCollapseRowSpan(false);
            exporter.setConfiguration(configuration);

            exporter.exportReport();
            return outputStream.toByteArray();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            virtualizer.cleanup();
        }
    }

    private Connection createConnection(JasperReportTemplateDto reportTemplateDto) throws SQLException {
        return DriverManager.getConnection(reportTemplateDto.getDbConectionDto().getUrl(),
                reportTemplateDto.getDbConectionDto().getUsername(),
                reportTemplateDto.getDbConectionDto().getPassword());
    }

//    public byte[] generateExcelReport(Map<String, Object> parameters, String reportPath, JasperReportTemplateDto reportTemplateDto) throws JRException, IOException {
//        JasperReport jasperReport = getJasperReport(reportPath);
//        logger.info("Generating Excel report with database: {}", reportTemplateDto.getDbConectionDto().getName());
//
//        JRFileVirtualizer virtualizer = new JRFileVirtualizer(2048, "temp/");
//        parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
//
//        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//            JdbcTemplate jdbcTemplate = getJdbcTemplate(reportTemplateDto);
//            String query = reportTemplateDto.getQuery() != null ? reportTemplateDto.getQuery() : "";
//            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
//            query = replaceQueryParameters(query, parameters);
//            List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(query, parameters);
//
//            JRDataSource jrDataSource = new JRBeanCollectionDataSource(rows);
//            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrDataSource);
//
//            JRXlsxExporter exporter = new JRXlsxExporter();
//            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
//
//            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
//            configuration.setDetectCellType(true);
//            configuration.setCollapseRowSpan(false);
//            exporter.setConfiguration(configuration);
//
//            exporter.exportReport();
//            return outputStream.toByteArray();
//        } finally {
//            virtualizer.cleanup();
//        }
//    }

    private JdbcTemplate getJdbcTemplate(JasperReportTemplateDto reportTemplateDto) {
        DataSource dataSource = createDataSource(reportTemplateDto.getDbConectionDto().getUrl(), reportTemplateDto.getDbConectionDto().getUsername(),
                reportTemplateDto.getDbConectionDto().getPassword());
        return new JdbcTemplate(dataSource);
    }

    private JasperReport getJasperReport(String reportPath) throws JRException, IOException {
        try (InputStream jrxmlInput = loadReportInputStream(reportPath)) {
            logger.info("JRXML content loaded successfully from: {}", reportPath);
            return JasperCompileManager.compileReport(jrxmlInput);
        }
    }

    private InputStream loadReportInputStream(String reportPath) throws IOException {
        Resource localResource = resourceLoader.getResource("classpath:templates/" + reportPath);
        if (localResource.exists()) {
            logger.info("Loading JRXML template from local resources: templates/{}", reportPath);
            return localResource.getInputStream();
        } else {
            logger.warn("Local template not found, fetching JRXML template from URL: {}", reportPath);
            return new ByteArrayInputStream(Objects.requireNonNull(restTemplate.getForObject(reportPath, byte[].class)));
        }
    }

    private DataSource createDataSource(String url, String username, String password) {
        logger.info("Connecting to database: {}", url);
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    private String replaceQueryParameters(String query, Map<String, Object> parameters) {
        Pattern pattern = Pattern.compile("::([a-zA-Z]\\w*)");
        Matcher matcher = pattern.matcher(query);
        StringBuffer resultQuery = new StringBuffer();

        while (matcher.find()) {
            String paramName = matcher.group(1);
            Object value = parameters.get(paramName);

            if (value == null) {
                throw new IllegalArgumentException("Parameter " + paramName + " not found in the parameters map.");
            }
            matcher.appendReplacement(resultQuery, value.toString());
        }
        matcher.appendTail(resultQuery);

        return resultQuery.toString();
    }
}
