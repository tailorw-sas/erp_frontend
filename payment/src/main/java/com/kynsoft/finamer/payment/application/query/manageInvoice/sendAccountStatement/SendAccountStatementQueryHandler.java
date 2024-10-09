package com.kynsoft.finamer.payment.application.query.manageInvoice.sendAccountStatement;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.application.query.objectResponse.ResourceTypeResponse;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SendAccountStatementQueryHandler implements IQueryHandler<SendAccountStatementQuery, SendAccountStatementResponse> {

    
    public SendAccountStatementQueryHandler(IManageInvoiceService service) {

    }

    @Override
    public SendAccountStatementResponse handle(SendAccountStatementQuery query) {
        FileService fileService = new FileService();

        try {
            return new SendAccountStatementResponse(fileService.convertExcelToBase64());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
