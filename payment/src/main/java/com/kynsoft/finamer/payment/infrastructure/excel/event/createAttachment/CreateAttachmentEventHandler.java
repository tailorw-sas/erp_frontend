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
        filterCriteria.setKey("antiToIncomeImport");
        filterCriteria.setValue(true);
        filterCriteria.setOperator(SearchOperation.EQUALS);
        filterCriteria.setLogicalOperation(LogicalOperation.AND);

        FilterCriteria filterDefault = new FilterCriteria();
        filterDefault.setKey("defaults");
        filterDefault.setValue(true);
        filterDefault.setOperator(SearchOperation.EQUALS);
        filterDefault.setLogicalOperation(LogicalOperation.AND);

        FilterCriteria statusActive = new FilterCriteria();
        statusActive.setKey("status");
        statusActive.setValue(Status.ACTIVE);
        statusActive.setOperator(SearchOperation.EQUALS);

        GetSearchManageResourceTypeQuery resourceTypeQuery = new GetSearchManageResourceTypeQuery(Pageable.unpaged(), List.of(filterDefault,statusActive), "");
        GetSearchAttachmentTypeQuery attachmentTypeQuery = new GetSearchAttachmentTypeQuery(Pageable.unpaged(), List.of(filterCriteria,statusActive), "");
        PaginatedResponse resourceType = mediator.send(resourceTypeQuery);
        PaginatedResponse attachmentType = mediator.send(attachmentTypeQuery);

        Assert.notEmpty(resourceType.getData(),"No existe resource type por defecto para crear el attachement");
        Assert.notEmpty(attachmentType.getData(),"No existe attachment para anti income");

        ResourceTypeResponse resourceTypeResponse = (ResourceTypeResponse)resourceType.getData().get(0);



        AttachmentTypeResponse attachmentTypeResponse =  (AttachmentTypeResponse)attachmentType.getData().get(0);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ApiResponse apiResponse = objectMapper.readValue(response.getBody(), ApiResponse.class);
            LinkedHashMap<String,String> saveFileS3Message = (LinkedHashMap) apiResponse.getData();

                CreateMasterPaymentAttachmentCommand createMasterPaymentAttachmentCommand =
                        new CreateMasterPaymentAttachmentCommand(Status.ACTIVE, event.getEmployeeId(),
                               event.getPaymentIds()
                                , resourceTypeResponse.getId(), attachmentTypeResponse.getId(),
                                event.getFileName(), saveFileS3Message.get("url"), "Uploaded", event.getFileSize());
                mediator.send(createMasterPaymentAttachmentCommand);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpEntity<MultiValueMap<String, Object>> createBody(CreateAttachmentEvent event){
        HttpHeaders headers = new HttpHeaders();
       // headers.add("Authorization","Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIxT0lxYlEwY2tXNVhwejgybFFuZzR1bzcxMEtkWnpfV0VDLVU5c2NGRWJNIn0.eyJleHAiOjE3MjQyNzMwMDksImlhdCI6MTcyNDE2NTAwOSwianRpIjoiZTQ2YzU3MzUtNjkyZC00ZjgzLTg3ZWQtNzdjNWVmMDlkY2FkIiwiaXNzIjoiaHR0cHM6Ly9zc28ua3luc29mdC5uZXQvcmVhbG1zL2t5bnNvZnQiLCJzdWIiOiIxYTU3MDE2My01NzYxLTQzNGMtOTA3Mi1mYjNmNzZiZmU1MDEiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJtZWRpbmVjIiwic2Vzc2lvbl9zdGF0ZSI6IjBlODY3MzY0LTUyZTAtNDBjYi04Yzc4LWM3MmUzYTEyNjNmMSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiLyoiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIkFETUlOIiwiVVNFUiIsImRlZmF1bHQtcm9sZXMta3lzb2Z0LXJlYWxtIl19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiIwZTg2NzM2NC01MmUwLTQwY2ItOGM3OC1jNzJlM2ExMjYzZjEiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6IkFETUlOIEFETUlOIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiYWRtaW4tdXNlckBnbWFpbC5jb20iLCJnaXZlbl9uYW1lIjoiQURNSU4iLCJmYW1pbHlfbmFtZSI6IkFETUlOIiwiZW1haWwiOiJhZG1pbi11c2VyQGdtYWlsLmNvbSJ9.0Uddw9HVrNNUEYFOaWIpU3vw0znxxQm2rvlvpLvFKp2YQHxJWNahr_f84GNhsfmiF1hbie1OiZIK9yDdvd1DoK9Z5ugZ0vn92BVhTGZEmK3fkdOsDRmM5XWu13HQngx1I8Vut6MVZs0-tAZy9a4Ii0J8d3GzZ8en68Ahjtlc1yeyI4a9rNH_VHChpTybZvWQIZBTvTz9aBW6QwnKFlH6XRqEBGyX6DdchtS3jaLliIq-5m6egmHL8rQieXePjrXuS1RY1kZ5p2w2poJz2T60xEFJt2POqVmXbry0xZ8eTYKCW7yOMU3Vmp2NQWPdZrO2MfSJNDZBz_7UucOp8nfa1Q");
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
