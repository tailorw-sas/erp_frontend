package com.kynsoft.finamer.invoicing.application.query.report;

import com.itextpdf.text.DocumentException;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.infrastructure.util.PDFUtils;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageClientDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceReportType;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReport;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.IManagerClientService;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.factory.InvoiceReportProviderFactory;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.util.ReportUtil;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class InvoiceReportQueryHandler implements IQueryHandler<InvoiceReportQuery, InvoiceZipReportResponse> {

    private final InvoiceReportProviderFactory invoiceReportProviderFactory;
    private final IManageInvoiceService manageInvoiceService;

    private final IManagerClientService managerClientService;

    public InvoiceReportQueryHandler(InvoiceReportProviderFactory invoiceReportProviderFactory,
                                     IManageInvoiceService manageInvoiceService,
                                     IManagerClientService managerClientService) {
        this.invoiceReportProviderFactory = invoiceReportProviderFactory;
        this.manageInvoiceService = manageInvoiceService;
        this.managerClientService = managerClientService;
    }


    @Override
    public InvoiceZipReportResponse handle(InvoiceReportQuery query) {
        InvoiceReportRequest invoiceReportRequest = query.getInvoiceReportRequest();
        List<EInvoiceReportType> invoiceReportTypes = getInvoiceTypeFromRequest(invoiceReportRequest);
        Map<EInvoiceReportType, IInvoiceReport> services = getServiceByInvoiceType(invoiceReportTypes);
        try {
            Optional<Map<String, byte[]>> reportContent;
            if (invoiceReportRequest.isGroupByClient()) {
                reportContent = getReportContentGroupedByClient(services, Arrays.asList(invoiceReportRequest.getInvoiceId()));
                if (reportContent.isPresent()) {
                    return ReportUtil.createCompressResponse(reportContent.get());
                }
            } else {
                reportContent = getReportContent(services, Arrays.asList(invoiceReportRequest.getInvoiceId()));
                if (reportContent.isPresent()) {
                    return ReportUtil.createCompressResponse(reportContent.get());
                }
            }

        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private List<EInvoiceReportType> getInvoiceTypeFromRequest(InvoiceReportRequest invoiceReportRequest) {
        return Arrays.stream(invoiceReportRequest.getInvoiceType())
                .map(EInvoiceReportType::valueOf)
                .toList();
    }

    private Map<EInvoiceReportType, IInvoiceReport> getServiceByInvoiceType(List<EInvoiceReportType> types) {
        Map<EInvoiceReportType, IInvoiceReport> services = new HashMap<>();
        for (EInvoiceReportType type : types) {
            services.put(type, invoiceReportProviderFactory.getInvoiceReportService(type));
        }
        return services;
    }

    private Optional<Map<String, byte[]>> getReportContent(Map<EInvoiceReportType, IInvoiceReport> reportService, List<String> invoicesIds) throws DocumentException, IOException {
        Map<String, byte[]> result = new HashMap<>();

        for (String invoiceId : invoicesIds) {
            Map<EInvoiceReportType, Optional<byte[]>> reportContent = reportService.entrySet().stream()
                    .filter(entry -> Objects.nonNull(entry.getValue()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().generateReport(invoiceId)
                    ));

            List<InputStream> finalContent = getOrderReportContent(reportContent).stream()
                    .filter(Optional::isPresent)
                    .map(content -> new ByteArrayInputStream(content.get()))
                    .map(InputStream.class::cast)
                    .toList();
            if (!finalContent.isEmpty()) {
                result.put(resolveInvoiceNumber(invoiceId), PDFUtils.mergePDFtoByte(finalContent));
            }
        }

        // Retornar el mapa de resultados si no está vacío
        return result.isEmpty() ? Optional.empty() : Optional.of(result);
    }

    private Optional<Map<String, byte[]>> getReportContentGroupedByClient(Map<EInvoiceReportType, IInvoiceReport> reportService, List<String> invoicesIds) throws DocumentException, IOException {
        Map<String, byte[]> result = new HashMap<>();
        Map<String, List<String>> invoicesGrouped = getInvoiceGroupedByClient(invoicesIds);

        for (Map.Entry<String, List<String>> entry : invoicesGrouped.entrySet()) {
            Map<EInvoiceReportType, Optional<byte[]>> reportContent = this.groupPdfContentByReportType(entry.getValue(), reportService);
            result.put(resolveClientName(entry.getKey()), PDFUtils.mergePDFtoByte(getOrderReportContent(reportContent)
                    .stream()
                    .filter(Optional::isPresent)
                    .map(bytes -> new ByteArrayInputStream(bytes.get()))
                    .map(InputStream.class::cast).toList()));
        }

        // Retornar el mapa de resultados si no está vacío
        return result.isEmpty() ? Optional.empty() : Optional.of(result);
    }

    private Map<EInvoiceReportType, Optional<byte[]>> groupPdfContentByReportType(List<String> invoicesIds, Map<EInvoiceReportType, IInvoiceReport> reportService) throws DocumentException, IOException {
        Map<EInvoiceReportType, Optional<byte[]>> content = new HashMap<>();
        for (Map.Entry<EInvoiceReportType, IInvoiceReport> entry : reportService.entrySet()) {
            if (Objects.nonNull(entry.getValue())) {
                List<Optional<byte[]>> reportContent = invoicesIds.stream().map(invoicesId -> entry.getValue().generateReport(invoicesId)).toList();
                content.put(entry.getKey(), Optional.of(PDFUtils.mergePDFtoByte(reportContent.stream()
                        .filter(Optional::isPresent)
                        .map(bytes -> new ByteArrayInputStream(bytes.get()))
                        .map(InputStream.class::cast).toList())));

            }
        }
        return content;
    }

    private List<Optional<byte[]>> getOrderReportContent(Map<EInvoiceReportType, Optional<byte[]>> content) {
        List<Optional<byte[]>> orderedContent = new LinkedList<>();
        orderedContent.add(content.getOrDefault(EInvoiceReportType.INVOICE_AND_BOOKING, Optional.empty()));
        orderedContent.add(content.getOrDefault(EInvoiceReportType.INVOICE_SUPPORT, Optional.empty()));
        return orderedContent;
    }

    private Map<String, List<String>> getInvoiceGroupedByClient(List<String> invoiceIds) {
        Map<String, List<String>> invoiceClientMap = new HashMap<>();
        invoiceIds.forEach(invoiceId -> {
            ManageInvoiceDto manageInvoiceDto = manageInvoiceService.findById(UUID.fromString(invoiceId));
            ManageAgencyDto manageAgencyDto = manageInvoiceDto.getAgency();
            ManageClientDto manageClientDto = manageAgencyDto.getClient();
            if (Objects.nonNull(manageClientDto)) {
                invoiceClientMap.computeIfAbsent(manageClientDto.getId().toString(), k -> new ArrayList<>()).add(invoiceId);
            }
        });
        return invoiceClientMap;
    }

    private String resolveInvoiceNumber(String invoiceId){
       ManageInvoiceDto manageInvoiceDto = manageInvoiceService.findById(UUID.fromString(invoiceId));
       return manageInvoiceDto.getInvoiceNumber();
    }
    private String resolveClientName(String clientId){
        ManageClientDto manageClientDto = managerClientService.findById(UUID.fromString(clientId));
        return manageClientDto.getName();
    }
}
