package com.kynsoft.report.infrastructure.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsoft.report.domain.services.IReportService;
import net.sf.jasperreports.engine.*;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class ReportService implements IReportService {
    private final RestTemplate restTemplate;
    private final ResourceLoader resourceLoader;

    public ReportService(RestTemplate restTemplate, ResourceLoader resourceLoader) {
        this.restTemplate = restTemplate;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public byte[] generatePdfReport(Map<String, Object> parameters, String jrxmlUrl, JREmptyDataSource jrEmptyDataSource) {
        //

        //  String jrxmlUrl = "http://d2cebw6tssfqem.cloudfront.net/cita_2024-04-17_11-38-05.jrxml";
        InputStream inputStream = new ByteArrayInputStream(Objects.requireNonNull(restTemplate.getForObject(jrxmlUrl, byte[].class)));
//        Resource resource = resourceLoader.getResource("classpath:templates/cita.jrxml");
//        if (!resource.exists() || !resource.isReadable()) {
//            throw new IOException("El archivo JRXML no se puede leer desde la ruta especificada.");
//        }
//        InputStream inputStream = resource.getInputStream();
        JasperReport jasperReport = null;
        try {
            jasperReport = JasperCompileManager.compileReport(inputStream);
        } catch (JRException e) {
            throw new RuntimeException(e);
        }

//        Map<String, Object> parameters = new HashMap<>();
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

        // Imprimir parámetros para debugging
        parameters.forEach((key, value) -> System.out.println(key + ": " + value));


        // Guardar el reporte como archivo PDF
//        String currentDir = System.getProperty("user.dir");
//        String filePath = currentDir + "/report.pdf";
//        JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrEmptyDataSource);
            JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
        } catch (JRException e) {
            throw new RuntimeException(e);
        }

        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public String getReportParameters(String jrxmlUrl) {
        // Obtener el archivo JRXML como un arreglo de bytes desde la URL
        byte[] data = restTemplate.getForObject(jrxmlUrl, byte[].class);
        InputStream inputStream = new ByteArrayInputStream(data);
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
            }
        }

        // Convertir el mapa a una cadena JSON
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(paramMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
//
//    //private final ResourceLoader resourceLoader;
//    private final RestTemplate restTemplate;
//
//    public ReportService(RestTemplate restTemplate) {
//        //    this.resourceLoader = resourceLoader;
//        this.restTemplate = restTemplate;
//    }
//
//    @Autowired
//    public byte[] generatePdfReport(Map<String, Object> parameters, String jrxmlUrl, JREmptyDataSource jrEmptyDataSource) {
//
//
//        //  String jrxmlUrl = "http://d2cebw6tssfqem.cloudfront.net/cita_2024-04-17_11-38-05.jrxml";
//        InputStream inputStream = new ByteArrayInputStream(Objects.requireNonNull(restTemplate.getForObject(jrxmlUrl, byte[].class)));
////        Resource resource = resourceLoader.getResource("classpath:templates/cita.jrxml");
////        if (!resource.exists() || !resource.isReadable()) {
////            throw new IOException("El archivo JRXML no se puede leer desde la ruta especificada.");
////        }
////        InputStream inputStream = resource.getInputStream();
//        JasperReport jasperReport = null;
//        try {
//            jasperReport = JasperCompileManager.compileReport(inputStream);
//        } catch (JRException e) {
//            throw new RuntimeException(e);
//        }
//
////        Map<String, Object> parameters = new HashMap<>();
////        parameters.put("logo", "http://d3ksvzqyx4up5m.cloudfront.net/Ttt_2024-03-14_19-03-33.png");
////        parameters.put("cita", "111111");
////        parameters.put("nombres", "Keimer Montes Oliver");
////        parameters.put("identidad", "0961881992");
////        parameters.put("fecha", "2024-04-23");
////        parameters.put("hora", "10:40");
////        parameters.put("servicio", "GINECOLOGIA");
////        parameters.put("tipo", "CONSULTA EXTERNA");
////        parameters.put("direccion", "Calle 48");
////        parameters.put("lugar", "HOSPITAL MILITAR");
////        parameters.put("fecha_registro", "2024-04-23 10:40");
////        parameters.put("URL_QR", "http://d3ksvzqyx4up5m.cloudfront.net/Ttt_2024-03-14_19-03-33.png");
//
//        // Imprimir parámetros para debugging
//        parameters.forEach((key, value) -> System.out.println(key + ": " + value));
//
//
//        // Guardar el reporte como archivo PDF
////        String currentDir = System.getProperty("user.dir");
////        String filePath = currentDir + "/report.pdf";
////        JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
//
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        try {
//            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrEmptyDataSource);
//            JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
//        } catch (JRException e) {
//            throw new RuntimeException(e);
//        }
//
//        return byteArrayOutputStream.toByteArray();
//    }
//
//
//    @Autowired
//    public String getReportParameters(String jrxmlUrl) {
//        // Obtener el archivo JRXML como un arreglo de bytes desde la URL
//        byte[] data = restTemplate.getForObject(jrxmlUrl, byte[].class);
//        InputStream inputStream = new ByteArrayInputStream(data);
//        JasperReport jasperReport = null;
//        try {
//            jasperReport = JasperCompileManager.compileReport(inputStream);
//        } catch (JRException e) {
//            throw new RuntimeException(e);
//        }
//
//        // Preparar el mapa para almacenar los detalles de los parámetros
//        Map<String, Map<String, String>> paramMap = new HashMap<>();
//        for (JRParameter param : jasperReport.getParameters()) {
//            if (!param.isSystemDefined() && param.isForPrompting()) { // Solo parámetros definidos por el usuario y que son promptables
//                Map<String, String> details = new HashMap<>();
//                details.put("description", param.getDescription() != null ? param.getDescription() : "No description");
//                details.put("type", param.getValueClassName());  // Añadir tipo de dato
//                paramMap.put(param.getName(), details);
//            }
//        }
//
//        // Convertir el mapa a una cadena JSON
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            return objectMapper.writeValueAsString(paramMap);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
