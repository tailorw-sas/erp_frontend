package com.kynsoft.finamer.payment.infrastructure.services.report.content;

import com.itextpdf.text.DocumentException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.service.IReportGenerator;
import com.kynsof.share.core.infrastructure.util.PDFUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InvoiceAttachmentContentProvider extends AbstractReportContentProvider {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceAttachmentContentProvider.class);

    private final String INVOICE_SERVICE_URL;

    public InvoiceAttachmentContentProvider(RestTemplate restTemplate, IReportGenerator reportGenerator, String invoiceServiceUrl) {
        super(restTemplate, reportGenerator);
        INVOICE_SERVICE_URL = invoiceServiceUrl;
    }

    @Override
    public Optional<byte[]> getContent(Map<String, Object> parameters) {
        String invoiceId = (String) parameters.getOrDefault("invoiceRelatedId", "");
        Assert.notNull(invoiceId, "El id del invoice es obligatorio");
        try {
            return getMergeAttachmentPdfContent(invoiceId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<byte[]> getMergeAttachmentPdfContent(String invoiceId) throws DocumentException, IOException {
        SearchRequest searchRequest = new SearchRequest();
        FilterCriteria invoiceIdFilter = new FilterCriteria();
        invoiceIdFilter.setValue("manageInvoice");
        invoiceIdFilter.setValue(invoiceId);
        searchRequest.setFilter(List.of(invoiceIdFilter));
        searchRequest.setPage(0);
        searchRequest.setPageSize(20);
        List<InputStream> attachmentInputStream = getSupportInvoiceAttachmentInputStream(searchRequest);
        return attachmentInputStream.isEmpty() ? Optional.empty() :
                Optional.of(PDFUtils.mergePDF(attachmentInputStream).toByteArray());
    }

    private List<InputStream> getSupportInvoiceAttachmentInputStream(SearchRequest searchRequest) throws IOException {
        ResponseEntity<String> response =
                restTemplate.postForEntity(INVOICE_SERVICE_URL + "/api/manage-attachment/search",
                        searchRequest, String.class);
        int page = searchRequest.getPage();
        List<InputStream> supportStream = new ArrayList<>();
        if (HttpStatus.OK.isSameCodeAs(response.getStatusCode())) {
            JSONObject paginateResponseJson = new JSONObject(response.getBody());
            JSONArray data = paginateResponseJson.getJSONArray("data");
            if (!data.isEmpty()) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject element = data.getJSONObject(i);
                    Optional<byte[]> remoteContent = getRemoteContent(element.getString("file"));
                    remoteContent.ifPresent(content -> supportStream.add(new ByteArrayInputStream(content)));
                }
            }
            if (page <= paginateResponseJson.getInt("totalPages")) {
                searchRequest.setPage(page + 1);
                getSupportInvoiceAttachmentInputStream(searchRequest);
            }
        }
        return supportStream;
    }
}
