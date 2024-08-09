package com.kynsoft.finamer.payment.infrastructure.excel.event.createAttachment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ApiResponse;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsof.share.core.infrastructure.specifications.LogicalOperation;
import com.kynsof.share.core.infrastructure.specifications.SearchOperation;
import com.kynsoft.finamer.payment.application.command.masterPaymentAttachment.create.CreateMasterPaymentAttachmentCommand;
import com.kynsoft.finamer.payment.application.query.attachmentType.search.GetSearchAttachmentTypeQuery;
import com.kynsoft.finamer.payment.application.query.manageResourceType.search.GetSearchManageResourceTypeQuery;
import com.kynsoft.finamer.payment.application.query.objectResponse.AttachmentTypeResponse;
import com.kynsoft.finamer.payment.application.query.objectResponse.ResourceTypeResponse;
import com.kynsoft.finamer.payment.domain.dto.AttachmentTypeDto;
import com.kynsoft.finamer.payment.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.infrastructure.excel.remote.SaveFileS3Message;
import com.kynsoft.finamer.payment.infrastructure.identity.AttachmentType;
import com.kynsoft.finamer.payment.infrastructure.identity.ResourceType;
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
import java.util.UUID;

@Component
public class CreateAttachmentEventHandler implements ApplicationListener<CreateAttachmentEvent> {
    @Value("${payment.file.service.url}")
    private String UPLOAD_FILE_URL;
    private final RestTemplate restTemplate;

    private final IMediator mediator;


    public CreateAttachmentEventHandler(RestTemplate restTemplate, IMediator mediator) {
        this.restTemplate = restTemplate;
        this.mediator = mediator;
    }

    @Override
    public void onApplicationEvent(CreateAttachmentEvent event) {
        ResponseEntity<String> response = restTemplate.postForEntity(UPLOAD_FILE_URL, this.createBody(event), String.class);

        FilterCriteria filterCriteria = new FilterCriteria();
        filterCriteria.setKey("defaults");
        filterCriteria.setValue(true);
        filterCriteria.setOperator(SearchOperation.EQUALS);
        filterCriteria.setLogicalOperation(LogicalOperation.AND);

        FilterCriteria statusActive = new FilterCriteria();
        statusActive.setKey("status");
        statusActive.setValue(Status.ACTIVE);
        statusActive.setOperator(SearchOperation.EQUALS);

        GetSearchManageResourceTypeQuery resourceTypeQuery = new GetSearchManageResourceTypeQuery(Pageable.unpaged(), List.of(filterCriteria,statusActive), "");
        GetSearchAttachmentTypeQuery attachmentTypeQuery = new GetSearchAttachmentTypeQuery(Pageable.unpaged(), List.of(filterCriteria,statusActive), "");
        PaginatedResponse resourceType = mediator.send(resourceTypeQuery);
        PaginatedResponse attachmentType = mediator.send(attachmentTypeQuery);


        ResourceTypeResponse resourceTypeResponse = (ResourceTypeResponse)resourceType.getData().get(0);


        AttachmentTypeResponse attachmentTypeResponse =  (AttachmentTypeResponse)attachmentType.getData().get(0);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ApiResponse apiResponse = objectMapper.readValue(response.getBody(), ApiResponse.class);
            LinkedHashMap<String,String> saveFileS3Message = (LinkedHashMap) apiResponse.getData();
            for (UUID paymentId:event.getPaymentIds()) {
                CreateMasterPaymentAttachmentCommand createMasterPaymentAttachmentCommand =
                        new CreateMasterPaymentAttachmentCommand(Status.ACTIVE, event.getEmployeeId(),
                               paymentId
                                , resourceTypeResponse.getId(), attachmentTypeResponse.getId(),
                                event.getFileName(), saveFileS3Message.get("url"), "Uploaded", event.getFileSize());
                mediator.send(createMasterPaymentAttachmentCommand);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpEntity<MultiValueMap<String, Object>> createBody(CreateAttachmentEvent event){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        //TODO keymer
       // headers.setBearerAuth();

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
