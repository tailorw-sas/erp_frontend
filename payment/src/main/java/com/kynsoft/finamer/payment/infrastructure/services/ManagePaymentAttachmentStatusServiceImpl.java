package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManagePaymentAttachmentStatusResponse;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentAttachmentStatusDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentAttachmentStatusService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManagePaymentAttachmentStatus;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManagePaymentAttachmentStatusWriteDataJpaRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManagePaymentAttachmentStatusReadDataJpaRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ManagePaymentAttachmentStatusServiceImpl implements IManagePaymentAttachmentStatusService {
    
    @Autowired
    private ManagePaymentAttachmentStatusWriteDataJpaRepository repositoryCommand;
    
    @Autowired
    private ManagePaymentAttachmentStatusReadDataJpaRepository repositoryQuery;
    
    @Override
    public UUID create(ManagePaymentAttachmentStatusDto dto) {
        final ManagePaymentAttachmentStatus entity = new ManagePaymentAttachmentStatus(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManagePaymentAttachmentStatusDto dto) {
        ManagePaymentAttachmentStatus entity = new ManagePaymentAttachmentStatus(dto);
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
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_PAYMENT_ATTACHMENT_STATUS_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MANAGER_PAYMENT_ATTACHMENT_STATUS_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManagePaymentAttachmentStatus> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagePaymentAttachmentStatus> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
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

    private PaginatedResponse getPaginatedResponse(Page<ManagePaymentAttachmentStatus> data) {
        List<ManagePaymentAttachmentStatusResponse> responses = new ArrayList<>();
        for (ManagePaymentAttachmentStatus p : data.getContent()) {
            responses.add(new ManagePaymentAttachmentStatusResponse(p.toAggregate()));
        }

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public ManagePaymentAttachmentStatusDto findByDefaults() {
        Optional<ManagePaymentAttachmentStatus> optionalEntity = repositoryQuery.findByDefault();
        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_NOT_FOUND.getReasonPhrase())));
    }

}
