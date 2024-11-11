package com.kynsoft.finamer.payment.application.query.manageInvoice.sendAccountStatement;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SendAccountStatementQueryHandler implements IQueryHandler<SendAccountStatementQuery, SendAccountStatementResponse> {

    private final FileService fileService;
    public SendAccountStatementQueryHandler(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public SendAccountStatementResponse handle(SendAccountStatementQuery query) {

        try {
            return new SendAccountStatementResponse(fileService.convertExcelToBase64(query.getInvoiceIds()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
