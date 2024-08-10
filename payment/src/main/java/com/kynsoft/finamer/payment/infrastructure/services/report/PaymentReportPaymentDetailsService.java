package com.kynsoft.finamer.payment.infrastructure.services.report;

import com.kynsof.share.core.domain.service.IReportGenerator;
import com.kynsoft.finamer.payment.application.query.report.PaymentReportResponse;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentReportType;
import com.kynsoft.finamer.payment.domain.services.IPaymentReport;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service(PaymentReportPaymentDetailsService.BEAN_ID)
public class PaymentReportPaymentDetailsService implements IPaymentReport {
    public  static final String BEAN_ID="PAYMENT_DETAILS";
    private final IPaymentService paymentService;


    private final IReportGenerator reportGenerator;

    public PaymentReportPaymentDetailsService(IPaymentService paymentService,
                                              IReportGenerator reportGenerator) {
        this.paymentService = paymentService;

        this.reportGenerator = reportGenerator;
    }

    @Override
    public PaymentReportResponse generateReport(EPaymentReportType reportType, UUID paymentId)  {

        Map<String, Object> parameters = new HashMap<>();
        Date currentDate = new Date(System.currentTimeMillis());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(currentDate);
        parameters.put("fechayHora", formattedDate);
        parameters.put("paymentId", paymentId);

        byte[] pdfContent = reportGenerator.generateReport(parameters, "aaa");
        try {
            return  createPaymentReportResponse(pdfContent, paymentId.toString()+".pdf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PaymentReportResponse createPaymentReportResponse(byte[] pdfContent, String fileName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(pdfContent);
        return new PaymentReportResponse(fileName, baos);
    }
}
