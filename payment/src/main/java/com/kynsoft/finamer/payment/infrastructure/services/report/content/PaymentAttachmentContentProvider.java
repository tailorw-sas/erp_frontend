package com.kynsoft.finamer.payment.infrastructure.services.report.content;

import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.domain.service.IReportGenerator;
import com.kynsof.share.core.infrastructure.specifications.LogicalOperation;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.kynsof.share.core.infrastructure.util.PDFUtils;
import com.kynsoft.finamer.payment.application.query.objectResponse.MasterPaymentAttachmentResponse;
import com.kynsoft.finamer.payment.domain.dto.AttachmentTypeDto;
import com.kynsoft.finamer.payment.domain.services.IManageAttachmentTypeService;
import com.kynsoft.finamer.payment.domain.services.IMasterPaymentAttachmentService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class PaymentAttachmentContentProvider extends AbstractReportContentProvider {

    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;
    private final IManageAttachmentTypeService manageAttachmentTypeService;

    private final String ATTACHMENT_CODE;

    public PaymentAttachmentContentProvider(RestTemplate restTemplate,
                                            IReportGenerator reportGenerator,
                                            IMasterPaymentAttachmentService masterPaymentAttachmentService,
                                            IManageAttachmentTypeService manageAttachmentTypeService,
                                            String attachmentCode) {
        super(restTemplate, reportGenerator);
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
        this.manageAttachmentTypeService = manageAttachmentTypeService;
        ATTACHMENT_CODE = attachmentCode;
    }

    @Override
    public Optional<byte[]> getContent(Map<String, Object> parameters) {
        try {
            return this.getMergeAttachmentPdfContent(parameters);
        } catch (Exception e) {
           e.printStackTrace();
           return null;
        }
    }


    private Optional<byte[]> getMergeAttachmentPdfContent(Map<String, Object> parameters) throws Exception {
        String paymentId = (String) parameters.getOrDefault("paymentId", "");
        String attachmentTypeId = getAttachmentTypeId();
        SearchRequest searchRequest = createAttachmentSearchRequest(paymentId, attachmentTypeId);
        List<InputStream> attachmentContent = getAllAttachmentContent(searchRequest);
       return attachmentContent.isEmpty()?Optional.empty():Optional.of(PDFUtils.mergePDF(attachmentContent).toByteArray());
    }

    private String getAttachmentTypeId() {
        AttachmentTypeDto attachmentTypeDto = manageAttachmentTypeService.findByCode(ATTACHMENT_CODE);
        return attachmentTypeDto.getId().toString();
    }

    private SearchRequest createAttachmentSearchRequest(String paymentId, String attachmentTypeId) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setPageSize(20);
        searchRequest.setPage(0);
        FilterCriteria attachmentCodeFilter = new FilterCriteria();
        attachmentCodeFilter.setKey("attachmentType.id");
        attachmentCodeFilter.setValue(attachmentTypeId);
        attachmentCodeFilter.setOperator(SearchOperation.EQUALS);
        attachmentCodeFilter.setLogicalOperation(LogicalOperation.AND);
        FilterCriteria paymentFilter = new FilterCriteria();
        paymentFilter.setKey("resource.id");
        paymentFilter.setValue(paymentId);
        paymentFilter.setOperator(SearchOperation.EQUALS);
        paymentFilter.setLogicalOperation(LogicalOperation.AND);
        searchRequest.setFilter(List.of(attachmentCodeFilter, paymentFilter));
        return searchRequest;
    }

    private List<InputStream> getAllAttachmentContent(SearchRequest searchRequest) throws Exception {
        Pageable pageable = PageableUtil.createPageable(searchRequest);
        PaginatedResponse paginatedResponse = masterPaymentAttachmentService
                .search(pageable, searchRequest.getFilter());
        List<InputStream> support = new ArrayList<>();
        for (Object masterPaymentAttachmentResponse : paginatedResponse.getData()) {
            if (Objects.nonNull(masterPaymentAttachmentResponse)) {
                Optional<byte[]> remoteContent=getRemoteContent(((MasterPaymentAttachmentResponse) masterPaymentAttachmentResponse).getPath());
                remoteContent.ifPresent(content->support.add(new ByteArrayInputStream(content)));
            }
        }
        if (searchRequest.getPage() <= paginatedResponse.getTotalPages()) {
            searchRequest.setPage(searchRequest.getPage()+1);
            getAllAttachmentContent(searchRequest);
        }
        return support;
    }


}
