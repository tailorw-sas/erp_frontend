package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagePaymentTransactionStatusResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManagePaymentTransactionStatusService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManagePaymentTransactionStatus;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManagePaymentTransactionStatusWriteDataJpaRepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManagePaymentTransactionStatusReadDataJpaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManagePaymentTransactionStatusImpl implements IManagePaymentTransactionStatusService {

    @Autowired
    private ManagePaymentTransactionStatusReadDataJpaRepository repositoryQuery;

    @Autowired
    private ManagePaymentTransactionStatusWriteDataJpaRepository repositoryCommand;

    @Override
    public UUID create(ManagePaymentTransactionStatusDto dto) {
       ManagePaymentTransactionStatus entity = new ManagePaymentTransactionStatus(dto);
       ManagePaymentTransactionStatus saved = repositoryCommand.save(entity);

        return saved.getId();
    }

    @Override
    public void update(ManagePaymentTransactionStatusDto dto) {
        repositoryCommand.save(new ManagePaymentTransactionStatus(dto));
    }

    @Override
    public void delete(ManagePaymentTransactionStatusDto dto) {
       ManagePaymentTransactionStatus delete = new ManagePaymentTransactionStatus(dto);
        delete.setDeleted(Boolean.TRUE);
        delete.setCode(delete.getCode()+ "-" + UUID.randomUUID());
        delete.setStatus(Status.INACTIVE);

        this.repositoryCommand.save(delete);
    }

    @Override
    public ManagePaymentTransactionStatusDto findById(UUID id) {
        Optional<ManagePaymentTransactionStatus> optionalEntity = repositoryQuery.findById(id);
        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_PAYMENT_SOURCE_NOT_FOUND, new ErrorField("id", "The manager payment source not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManagePaymentTransactionStatus> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagePaymentTransactionStatus> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManagePaymentTransactionStatus> data) {
        List<ManagePaymentTransactionStatusResponse> responseList = new ArrayList<>();
        for (ManagePaymentTransactionStatus entity : data.getContent()) {
            responseList.add(new ManagePaymentTransactionStatusResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }
}
