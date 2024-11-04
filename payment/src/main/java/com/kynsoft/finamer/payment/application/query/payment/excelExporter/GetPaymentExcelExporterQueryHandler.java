package com.kynsoft.finamer.payment.application.query.payment.excelExporter;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.payment.domain.dtoEnum.PaymentExcelExporterEnum;
import com.kynsoft.finamer.payment.infrastructure.services.ExcelExporterService;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class GetPaymentExcelExporterQueryHandler implements IQueryHandler<GetPaymentExcelExporterQuery, PaymentExcelExporterResponse> {
    private final ExcelExporterService service;
    
    public GetPaymentExcelExporterQueryHandler(ExcelExporterService service) {
        this.service = service;
    }

    @Override
    public PaymentExcelExporterResponse handle(GetPaymentExcelExporterQuery query) {

        try {
            byte[] response = this.service.exportToExcel(query.getPageable(),query.getFilter(), PaymentExcelExporterEnum.EXPORT_SUMMARY);
            return new PaymentExcelExporterResponse(response, query.getFileName());
        } catch (Exception ex) {
            Logger.getLogger(GetPaymentExcelExporterQueryHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
