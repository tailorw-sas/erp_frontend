package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageAttachmentResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.Invoice;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageAttachment;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageAttachmentWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageAttachmentReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManageAttachmentServiceImpl implements IManageAttachmentService {

    @Autowired
    private final ManageAttachmentWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageAttachmentReadDataJPARepository repositoryQuery;

    public ManageAttachmentServiceImpl(ManageAttachmentWriteDataJPARepository repositoryCommand,
            ManageAttachmentReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public Long create(ManageAttachmentDto dto) {
        ManageAttachment entity = new ManageAttachment(dto);
        return repositoryCommand.saveAndFlush(entity).getAttachmentId();
    }

    @Override
    public void update(ManageAttachmentDto dto) {
        ManageAttachment entity = new ManageAttachment(dto);
        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageAttachment> specifications = new GenericSpecificationsBuilder<>(
                filterCriteria);
        Page<ManageAttachment> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageAttachment> data) {
        List<ManageAttachmentResponse> responseList = new ArrayList<>();
        for (ManageAttachment entity : data.getContent()) {
            responseList.add(new ManageAttachmentResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public void delete(ManageAttachmentDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageAttachmentDto findById(UUID id) {
        Optional<ManageAttachment> optionalEntity = repositoryQuery.findById(id);

        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.INVOICE_ATTACHMENT_NOT_FOUND,
                new ErrorField("id", DomainErrorMessage.INVOICE_ATTACHMENT_NOT_FOUND.getReasonPhrase())));

    }

    @Override
    public List<ManageAttachmentDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageAttachment::toAggregate).toList();
    }

    @Override
    public List<ManageAttachmentDto> findAllByInvoiceId(UUID invoiceId) {
        return repositoryQuery.findAllByInvoiceId(invoiceId).stream().map(ManageAttachment::toAggregate).toList();
    }

    @Override
    public void create(List<ManageAttachmentDto> dtos) {
        List<ManageAttachment> attachments = new ArrayList<>();
        Map<UUID, ManageAttachmentDto> attachmentDtoMap = new HashMap<>();

        for(ManageAttachmentDto dto : dtos){
            attachments.add(new ManageAttachment(dto));
            attachmentDtoMap.put(dto.getId(), dto);
        }
        List<ManageAttachment> createdAttachments = this.repositoryCommand.saveAll(attachments);

        createdAttachments.forEach(manageAttachment -> {
            ManageAttachmentDto attachmentDto = attachmentDtoMap.get(manageAttachment.getId());
            attachmentDto.setAttachmentId(manageAttachment.getAttachmentId());
        });
    }

    @Override
    public void create(ManageInvoiceDto invoiceDto) {
        Invoice invoice = new Invoice(invoiceDto);
        if(invoice.getAttachments() != null && !invoice.getAttachments().isEmpty()){
            for(ManageAttachment attachment : invoice.getAttachments()){
                this.insert(attachment);
            }
            Map<UUID, ManageAttachmentDto> attachmentDtoMap = invoiceDto.getAttachments().stream().collect(Collectors.toMap(ManageAttachmentDto::getId, attachmentDto -> attachmentDto));

            invoice.getAttachments().forEach(manageAttachment -> {
                ManageAttachmentDto attachmentDto = attachmentDtoMap.get(manageAttachment.getId());
                attachmentDto.setAttachmentId(manageAttachment.getAttachmentId());
            });
        }
    }

    @Override
    public void deleteInvoice(ManageAttachmentDto dto) {
        ManageAttachment entity = new ManageAttachment(dto);
        entity.setDeleteInvoice(true);
        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    private void filterCriteria(List<FilterCriteria> filterCriteria) {
        for (FilterCriteria filter : filterCriteria) {

            if ("status".equals(filter.getKey()) && filter.getValue() instanceof String) {
                try {
                    Status enumValue = Status.valueOf((String) filter.getValue());
                    filter.setValue(enumValue);
                } catch (IllegalArgumentException e) {
                    System.err.println("Valor inválido para el tipo Enum Status: " + filter.getValue());
                }
            }
        }
    }

    private void insert(ManageAttachment attachment){
        Map<String, Object> result = this.repositoryCommand.insertInvoiceAttachment(attachment.getId(),
                attachment.isDeleteInvoice(),
                attachment.getDeleted(),
                attachment.getDeletedAt(),
                attachment.getEmployee(),
                attachment.getEmployeeId(),
                attachment.getFile(),
                attachment.getFilename(),
                attachment.getRemark(),
                attachment.getUpdatedAt(),
                attachment.getInvoice() != null ? attachment.getInvoice().getId() : null,
                attachment.getPaymentResourceType() != null ? attachment.getPaymentResourceType().getId() : null,
                attachment.getType() != null ? attachment.getType().getId() : null);
        if(result != null){
            if(result.containsKey("o_id")) attachment.setId((UUID)result.get("o_id"));
            if(result.containsKey("o_attachment_gen_id")) attachment.setAttachmentId(((Integer)result.get("o_attachment_gen_id")).longValue());
        }
    }

}
