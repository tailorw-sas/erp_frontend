//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.kynsoft.finamer.invoicing.application.command.manageInvoice.updateSendStatus;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.response.ResponseStatus;
import com.kynsof.share.core.domain.response.UploadFileResponse;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceStatusHistoryDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceStatusHistoryService;
import com.kynsoft.finamer.invoicing.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceStatusService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UpdateSendStatusCommandHandler implements ICommandHandler<UpdateSendStatusCommand> {
    private static final Logger logger = LoggerFactory.getLogger(UpdateSendStatusCommandHandler.class);
    private final IManageInvoiceService manageInvoiceService;
    private final IManageInvoiceStatusService manageInvoiceStatusService;
    private final IInvoiceStatusHistoryService invoiceStatusHistoryService;
    private final IManageEmployeeService manageEmployeeService;

    public UpdateSendStatusCommandHandler(IManageInvoiceService manageInvoiceService, IManageInvoiceStatusService manageInvoiceStatusService, IInvoiceStatusHistoryService invoiceStatusHistoryService, IManageEmployeeService manageEmployeeService) {
        this.manageInvoiceService = manageInvoiceService;
        this.manageInvoiceStatusService = manageInvoiceStatusService;
        this.invoiceStatusHistoryService = invoiceStatusHistoryService;
        this.manageEmployeeService = manageEmployeeService;
    }

    public void handle(UpdateSendStatusCommand command) {
        Map<UUID, UploadFileResponse> invoiceResponses = command.getInvoiceResponses();
        List<UUID> invoiceIds = new ArrayList<>(invoiceResponses.keySet());
        List<ManageInvoiceDto> invoices = this.manageInvoiceService.findInvoicesWithoutBookings(invoiceIds);
        ManageInvoiceStatusDto manageInvoiceStatus = this.manageInvoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.SENT);
        String employee = "";

        for(ManageInvoiceDto invoice : invoices) {
            UploadFileResponse response = invoiceResponses.get(invoice.getId());
            if (response != null && response.getStatus() == ResponseStatus.SUCCESS_RESPONSE) {
                if (invoice.getStatus().equals(EInvoiceStatus.RECONCILED)) {
                    invoice.setStatus(EInvoiceStatus.SENT);
                    invoice.setManageInvoiceStatus(manageInvoiceStatus);
                    invoice.setDueDate(LocalDate.now().plusDays(invoice.getAgency().getCreditDay().longValue()));
                    invoice.setSendStatusError((String)null);
                    this.invoiceStatusHistoryService.create(new InvoiceStatusHistoryDto(UUID.randomUUID(), invoice, "The invoice data was updated.", (LocalDateTime)null, employee, EInvoiceStatus.SENT, 0L));
                    logger.info("✅ Invoice {} updated to status SENT", invoice.getInvoiceNumber());
                } else if (invoice.getStatus().equals(EInvoiceStatus.SENT)) {
                    invoice.setDueDate(LocalDate.now().plusDays(invoice.getAgency().getCreditDay().longValue()));
                    invoice.setReSend(true);
                    invoice.setReSendDate(LocalDate.now());
                    invoice.setSendStatusError((String)null);
                    logger.info("\ud83d\udd04 Invoice {} marked for resend", invoice.getInvoiceNumber());
                }
            } else {
                logger.error("❌ Invoice {} failed to upload to Bavel FTP.", invoice.getInvoiceNumber());
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String var10000 = response != null ? response.getMessage() : "Unknown error";
                String errorMessage = "❌ Error uploading to common storage: " + var10000 + timestamp;
                invoice.setSendStatusError(errorMessage);
            }
        }

        this.manageInvoiceService.updateAll(invoices);
    }
}
