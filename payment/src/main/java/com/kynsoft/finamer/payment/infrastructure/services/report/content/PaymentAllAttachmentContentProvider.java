package com.kynsoft.finamer.payment.infrastructure.services.report.content;

import com.kynsof.share.core.domain.service.IReportGenerator;
import com.kynsof.share.core.infrastructure.util.PDFUtils;
import com.kynsoft.finamer.payment.application.query.objectResponse.MasterPaymentAttachmentResponse;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.services.IMasterPaymentAttachmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class PaymentAllAttachmentContentProvider extends AbstractReportContentProvider {
    private final Logger logger = LoggerFactory.getLogger(PaymentAllAttachmentContentProvider.class);
    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;

    public PaymentAllAttachmentContentProvider(RestTemplate restTemplate, IReportGenerator reportGenerator,
                                               IMasterPaymentAttachmentService masterPaymentAttachmentService) {
        super(restTemplate, reportGenerator);
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
    }

    @Override
    public Optional<byte[]> getContent(Map<String, Object> parameters) {
        try {
            String paymentId = (String) parameters.getOrDefault("paymentId", "");
            return getMergeAttachmentPdfContent(paymentId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<byte[]> getMergeAttachmentPdfContent(String paymentId) throws Exception {
        List<MasterPaymentAttachmentDto> masterPaymentAttachmentDtoList;
        masterPaymentAttachmentDtoList = masterPaymentAttachmentService
                .findAllByPayment(UUID.fromString(paymentId));
        List<InputStream> support = new ArrayList<>();
        for (MasterPaymentAttachmentDto masterPaymentAttachmentDto : masterPaymentAttachmentDtoList) {
            if (Objects.nonNull(masterPaymentAttachmentDto.getPath())) {
                Optional<byte[]> remoteContent = getRemoteContent(masterPaymentAttachmentDto.getPath());
                remoteContent.ifPresent(content -> support.add(new ByteArrayInputStream(content)));
            }
        }
        return support.isEmpty() ? Optional.empty() : Optional.of(PDFUtils.mergePDF(support).toByteArray());
    }



}
