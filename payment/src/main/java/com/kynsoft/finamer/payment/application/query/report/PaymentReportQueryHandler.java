package com.kynsoft.finamer.payment.application.query.report;

import com.itextpdf.text.DocumentException;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.infrastructure.util.PDFUtils;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentReportType;
import com.kynsoft.finamer.payment.domain.services.IPaymentReport;
import com.kynsoft.finamer.payment.infrastructure.services.factory.PaymentReportProviderFactory;
import com.kynsoft.finamer.payment.infrastructure.services.report.util.ReportUtil;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentReportQueryHandler implements IQueryHandler<PaymentReportQuery, PaymentReportResponse> {
    private final PaymentReportProviderFactory paymentReportProviderFactory;

    public PaymentReportQueryHandler(PaymentReportProviderFactory paymentReportProviderFactory) {
        this.paymentReportProviderFactory = paymentReportProviderFactory;
    }

    @Override
    public PaymentReportResponse handle(PaymentReportQuery query) {
        PaymentReportRequest paymentReportRequest = query.getPaymentReportRequest();
        List<EPaymentReportType> invoiceReportTypes = getPaymentTypeFromRequest(paymentReportRequest);
        Map<EPaymentReportType,IPaymentReport> services = getServiceByPaymentType(invoiceReportTypes);
        try {
            ByteArrayOutputStream reportContent = getReportContent(services, Arrays.asList(paymentReportRequest.getPaymentId()));
            return ReportUtil.createPaymentReportResponse(reportContent.toByteArray(),paymentReportRequest.getPaymentId().length>0?
                    "invoicing-report.pdf":paymentReportRequest.getPaymentId()[0]+".pdf");
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<EPaymentReportType> getPaymentTypeFromRequest(PaymentReportRequest paymentReportRequest){
        return Arrays.stream(paymentReportRequest.getPaymentType())
                .map(EPaymentReportType::valueOf)
                .toList();
    }
    private Map<EPaymentReportType,IPaymentReport> getServiceByPaymentType(List<EPaymentReportType> types){
        Map<EPaymentReportType,IPaymentReport> services = new HashMap<>();
        for (EPaymentReportType type : types) {
            services.put(type,paymentReportProviderFactory.getPaymentReportService(type));
        }
        return services;
    }

    private ByteArrayOutputStream getReportContent(Map<EPaymentReportType,IPaymentReport> reportService, List<String> paymentIds) throws DocumentException, IOException {
        Map<EPaymentReportType, Optional<byte[]>> reportContent = new HashMap<>();
        List<byte[]> result= new ArrayList<>();
        for (String paymentId : paymentIds) {
            for (Map.Entry<EPaymentReportType, IPaymentReport> entry : reportService.entrySet()) {
                reportContent.put(entry.getKey(),entry.getValue().generateReport(UUID.fromString(paymentId)));
            }
            List<Optional<byte[]>> orderedContent=getOrderReportContent(reportContent);
            List<InputStream> finalContent=orderedContent.stream()
                    .filter(Optional::isPresent)
                    .map(content->new ByteArrayInputStream(content.get()))
                    .map(InputStream.class::cast)
                    .toList();
            result.add(PDFUtils.mergePDFtoByte(finalContent));
        }
        return PDFUtils.mergePDF(result.stream().map(ByteArrayInputStream::new).map(InputStream.class::cast).toList());
    }


    private List<Optional<byte[]>> getOrderReportContent(Map<EPaymentReportType,Optional<byte[]>> content){
        List<Optional<byte[]>> orderedContent = new LinkedList<>();
        orderedContent.add(content.getOrDefault(EPaymentReportType.PAYMENT_DETAILS,Optional.empty()));
        orderedContent.add(content.getOrDefault(EPaymentReportType.PAYMENT_SUPPORT,Optional.empty()));
        orderedContent.add(content.getOrDefault(EPaymentReportType.ALL_SUPPORT,Optional.empty()));
        orderedContent.add(content.getOrDefault(EPaymentReportType.INVOICE_RELATED,Optional.empty()));
        orderedContent.add(content.getOrDefault(EPaymentReportType.INVOICE_RELATED_SUPPORT,Optional.empty()));
        return orderedContent;
    }
}
