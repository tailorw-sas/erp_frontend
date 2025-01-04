package com.kynsoft.finamer.invoicing.infrastructure.services.report.content;

import com.kynsof.share.core.domain.service.IReportGenerator;
import com.kynsof.share.core.infrastructure.util.PDFUtils;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentService;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class InvoiceSupportProvider extends AbstractReportContentProvider {

    private final IManageAttachmentService attachmentService;
    public InvoiceSupportProvider(RestTemplate restTemplate,
                                  IReportGenerator reportGenerator, IManageAttachmentService attachmentService) {
        super(restTemplate,reportGenerator);
        this.attachmentService = attachmentService;
    }

    @Override
    public Optional<byte[]> getContent(Map<String, Object> parameters) {
        return getInvoiceReport(parameters);
    }

    private Optional<byte[]> getInvoiceReport(Map<String, Object> parameters) {
        String invoiceId= (String) parameters.getOrDefault("invoiceId","");
        try {
            return getMergeAttachmentPdfContent(invoiceId);
        } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
    }
    private Optional<byte[]> getMergeAttachmentPdfContent(String invoiceId) throws Exception {
        List<ManageAttachmentDto> masterPaymentAttachmentDtoList;
        masterPaymentAttachmentDtoList = attachmentService.findAllByInvoiceId(UUID.fromString(invoiceId));
        List<InputStream> support = new ArrayList<>();
        for (ManageAttachmentDto manageAttachmentDto : masterPaymentAttachmentDtoList) {
            if (Objects.nonNull(manageAttachmentDto.getFile())) {
                Optional<byte[]> remoteContent = getRemoteContent(manageAttachmentDto.getFile());
                remoteContent.ifPresent(content -> support.add(new ByteArrayInputStream(content)));
            }
        }
        return support.isEmpty() ? Optional.empty() : Optional.of(PDFUtils.mergePDF(support).toByteArray());
    }

}
