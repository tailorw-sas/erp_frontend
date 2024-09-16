package com.kynsoft.finamer.invoicing.infrastructure.event.createAttachment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ApiResponse;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsof.share.core.infrastructure.specifications.LogicalOperation;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentCommand;
import com.kynsoft.finamer.invoicing.application.query.manageAttachmentType.search.GetSearchAttachmentTypeQuery;
import com.kynsoft.finamer.invoicing.application.query.manageAttachmentType.search.GetSearchManageAttachmentTypeResponse;
import com.kynsoft.finamer.invoicing.application.query.managePaymentTransactionType.search.GetSearchManagePaymentTransactionTypeQuery;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManagePaymentTransactionTypeResponse;
import com.kynsoft.finamer.invoicing.application.query.resourceType.GetSearchResourceTypeQuery;
import com.kynsoft.finamer.invoicing.application.query.resourceType.GetSearchResourceTypeResponse;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.event.createAttachment.CreateAttachmentEvent;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAttachmentType;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;

@Component
public class CreateAttachmentEventHandler implements ApplicationListener<CreateAttachmentEvent> {
    @Value("${file.service.url}")
    private String UPLOAD_FILE_URL;
    private final RestTemplate restTemplate;

    private final IMediator mediator;


    public CreateAttachmentEventHandler(RestTemplate restTemplate, IMediator mediator) {
        this.restTemplate = restTemplate;
        this.mediator = mediator;
    }

    @Override
    public void onApplicationEvent(CreateAttachmentEvent event) {
        FilterCriteria filterDefault = new FilterCriteria();
        filterDefault.setKey("defaults");
        filterDefault.setValue(true);
        filterDefault.setOperator(SearchOperation.EQUALS);
        filterDefault.setLogicalOperation(LogicalOperation.AND);

        FilterCriteria statusActive = new FilterCriteria();
        statusActive.setKey("status");
        statusActive.setValue(Status.ACTIVE);
        statusActive.setOperator(SearchOperation.EQUALS);

        FilterCriteria codeFilter = new FilterCriteria();
        codeFilter.setKey("code");
        codeFilter.setValue("INV");
        codeFilter.setOperator(SearchOperation.EQUALS);

        GetSearchResourceTypeQuery resourceTypeQuery = new GetSearchResourceTypeQuery(Pageable.unpaged(), List.of(codeFilter), "");
        GetSearchAttachmentTypeQuery attachmentTypeQuery = new GetSearchAttachmentTypeQuery(Pageable.unpaged(), List.of(filterDefault,statusActive), "");
        PaginatedResponse resourceType = mediator.send(resourceTypeQuery);
        PaginatedResponse attachmentType = mediator.send(attachmentTypeQuery);

        Assert.notEmpty(resourceType.getData(),"No existe resource type por defecto para crear el attachement");
        Assert.notEmpty(attachmentType.getData(),"No existe attachment default");

        GetSearchResourceTypeResponse resourceTypeResponse = (GetSearchResourceTypeResponse)resourceType.getData().get(0);

        GetSearchManageAttachmentTypeResponse attachmentTypeResponse =  (GetSearchManageAttachmentTypeResponse)attachmentType.getData().get(0);

        ResponseEntity<String> response = restTemplate.postForEntity(UPLOAD_FILE_URL, this.createBody(event), String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ApiResponse apiResponse = objectMapper.readValue(response.getBody(), ApiResponse.class);
            LinkedHashMap<String,String> saveFileS3Message = (LinkedHashMap) apiResponse.getData();
            CreateAttachmentCommand createAttachmentCommand =
                        new CreateAttachmentCommand(event.getFileName(),
                                saveFileS3Message.get("url"),
                                event.getRemarks(),
                                attachmentTypeResponse.getId(),
                                event.getInvoiceId(),
                                event.getEmployee(),
                                event.getEmployeeId(),
                                resourceTypeResponse.getId());
                mediator.send(createAttachmentCommand);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpEntity<MultiValueMap<String, Object>> createBody(CreateAttachmentEvent event){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, String> contentDispositionMap = new LinkedMultiValueMap<>();
        ContentDisposition contentDisposition = ContentDisposition
                .builder("form-data")
                .name("file")
                .filename(event.getFileName())
                .build();
        contentDispositionMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
        HttpEntity<byte[]> fileEntity = new HttpEntity<>(event.getFile(), contentDispositionMap);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("file", fileEntity);

       return  new HttpEntity<>(body, headers);
    }
}
