package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagePaymentAttachmentStatusResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentAttachmentStatusService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManagePaymentAttachmentStatus;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManagePaymentAttachmentStatusWriteDataJpaRepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManagePaymentAttachmentStatusReadDataJpaRepository;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManagePaymentAttachmentStatusServiceImpl implements IManagePaymentAttachmentStatusService {
    
    private final ManagePaymentAttachmentStatusWriteDataJpaRepository repositoryCommand;
    
    private final ManagePaymentAttachmentStatusReadDataJpaRepository repositoryQuery;

    public ManagePaymentAttachmentStatusServiceImpl(ManagePaymentAttachmentStatusWriteDataJpaRepository repositoryCommand, ManagePaymentAttachmentStatusReadDataJpaRepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManagePaymentAttachmentStatusDto dto) {
        final ManagePaymentAttachmentStatus entity = new ManagePaymentAttachmentStatus(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManagePaymentAttachmentStatusDto dto) {
        ManagePaymentAttachmentStatus entity = new ManagePaymentAttachmentStatus(dto);
        entity.setUpdatedAt(LocalDateTime.now());
        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManagePaymentAttachmentStatusDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManagePaymentAttachmentStatusDto findById(UUID id) {
        Optional<ManagePaymentAttachmentStatus> result = this.repositoryQuery.findById(id);
        if (result.isPresent()) {
            return result.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_PAYMENT_ATTACHMENT_STATUS_NOT_FOUND, new ErrorField("code", "Payment Attachment Status code not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManagePaymentAttachmentStatus> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagePaymentAttachmentStatus> data = this.repositoryQuery.findAll(specifications, pageable);
        return getPaginatedResponse(data);

    }

    @Override
    public Long countByCode(String code, UUID id) {
        return repositoryQuery.countByCode(code, id);
    }

    @Override
    public List<ManagePaymentAttachmentStatusDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManagePaymentAttachmentStatus::toAggregate).toList();
    }

    private PaginatedResponse getPaginatedResponse(Page<ManagePaymentAttachmentStatus> data) {
        List<ManagePaymentAttachmentStatusResponse> userSystemsResponses = new ArrayList<>();
        for (ManagePaymentAttachmentStatus p : data.getContent()) {
            userSystemsResponses.add(new ManagePaymentAttachmentStatusResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByNameAndNotId(String name, UUID id) {
        return this.repositoryQuery.countByNameAndNotId(name, id);
    }

    @Override
    public List<ManagePaymentAttachmentStatusDto> findAllToReplicate() {
        List<ManagePaymentAttachmentStatus> objects = this.repositoryQuery.findAll();
        List<ManagePaymentAttachmentStatusDto> objectDtos = new ArrayList<>();

        for (ManagePaymentAttachmentStatus object : objects) {
            objectDtos.add(object.toAggregate());
        }

        return objectDtos;
    }

    @Override
    public Long countByDefaultAndNotId(UUID id) {
        return this.repositoryQuery.countByDefaultAndNotId(id);
    }

}
