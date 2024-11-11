package com.kynsoft.finamer.invoicing.application.query.manageInvoice.sendAccountStatement;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageEmployeeService;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SendAccountStatementQueryHandler implements IQueryHandler<SendAccountStatementQuery, SendAccountStatementResponse> {

    private final FileService fileService;
    private final IManageEmployeeService employeeService;

    public SendAccountStatementQueryHandler(FileService fileService,
                                            IManageEmployeeService employeeService) {
        this.fileService = fileService;
        this.employeeService = employeeService;
    }

    @Override
    public SendAccountStatementResponse handle(SendAccountStatementQuery query) {

        ManageEmployeeDto employeeDto = this.employeeService.findById(query.getEmployee());
        try {
            return new SendAccountStatementResponse(fileService.convertExcelToBase64(query.getInvoiceIds(), employeeDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
