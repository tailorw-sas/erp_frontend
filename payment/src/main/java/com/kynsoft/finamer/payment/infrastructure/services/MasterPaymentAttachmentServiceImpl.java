package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.payment.application.query.objectResponse.MasterPaymentAttachmentResponse;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IMasterPaymentAttachmentService;
import com.kynsoft.finamer.payment.infrastructure.identity.MasterPaymentAttachment;
import com.kynsoft.finamer.payment.infrastructure.identity.Payment;
import com.kynsoft.finamer.payment.infrastructure.repository.command.MasterPaymentAttachmentWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.MasterPaymentAttachmentReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MasterPaymentAttachmentServiceImpl implements IMasterPaymentAttachmentService {

    @Autowired
    private MasterPaymentAttachmentWriteDataJPARepository repositoryCommand;

    @Autowired
    private MasterPaymentAttachmentReadDataJPARepository repositoryQuery;

    @Override
    public Long create(MasterPaymentAttachmentDto dto) {
        MasterPaymentAttachment data = new MasterPaymentAttachment(dto);
        return this.repositoryCommand.save(data).getAttachmentId();
    }

    @Override
    public void create(List<MasterPaymentAttachmentDto> dtos) {
        List<MasterPaymentAttachment> masterPaymentAttachments = new ArrayList<>();
        for (MasterPaymentAttachmentDto dto : dtos) {
            masterPaymentAttachments.add(new MasterPaymentAttachment(dto));
        }
        this.repositoryCommand.saveAll(masterPaymentAttachments);
    }

    @Override
    public void update(MasterPaymentAttachmentDto dto) {
        MasterPaymentAttachment update = new MasterPaymentAttachment(dto);

        update.setUpdatedAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(MasterPaymentAttachmentDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public MasterPaymentAttachmentDto findById(UUID id) {
        Optional<MasterPaymentAttachment> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MASTER_PAYMENT_ATTACHMENT_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MASTER_PAYMENT_ATTACHMENT_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<Payment> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<MasterPaymentAttachment> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<MasterPaymentAttachment> data) {
        List<MasterPaymentAttachmentResponse> responses = new ArrayList<>();
        for (MasterPaymentAttachment p : data.getContent()) {
            responses.add(new MasterPaymentAttachmentResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByResourceAndAttachmentTypeIsDefault(UUID resource) {
        Long cant = this.repositoryQuery.countByResourceAndAttachmentTypeIsDefault(resource);
        return cant;
    }
    
    @Override
    public List<MasterPaymentAttachmentDto> findAllByPayment(UUID payment) {
        return this.repositoryQuery.findAllByPayment(payment)
                .stream()
                .map(MasterPaymentAttachment::toAggregate)
                .collect(Collectors.toList());
    }

}
