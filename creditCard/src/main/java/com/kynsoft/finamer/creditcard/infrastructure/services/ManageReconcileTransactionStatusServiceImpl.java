package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageReconcileTransactionStatusResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageReconcileTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.EReconcileTransactionStatus;
import com.kynsoft.finamer.creditcard.domain.services.IManageReconcileTransactionStatusService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageReconcileTransactionStatus;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ManageReconcileTransactionStatusWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ManageReconcileTransactionStatusReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageReconcileTransactionStatusServiceImpl implements IManageReconcileTransactionStatusService {

    @Autowired
    private ManageReconcileTransactionStatusReadDataJPARepository repositoryQuery;

    @Autowired
    private ManageReconcileTransactionStatusWriteDataJPARepository repositoryCommand;

    @Override
    public UUID create(ManageReconcileTransactionStatusDto dto) {
        ManageReconcileTransactionStatus entity = new ManageReconcileTransactionStatus(dto);
        ManageReconcileTransactionStatus saved = repositoryCommand.save(entity);

        return saved.getId();
    }

    @Override
    public void update(ManageReconcileTransactionStatusDto dto) {
        repositoryCommand.save(new ManageReconcileTransactionStatus(dto));
    }

    @Override
    public void delete(ManageReconcileTransactionStatusDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageReconcileTransactionStatusDto findById(UUID id) {
        Optional<ManageReconcileTransactionStatus> optionalEntity = repositoryQuery.findById(id);
        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", DomainErrorMessage.NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageReconcileTransactionStatus> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageReconcileTransactionStatus> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageReconcileTransactionStatus> data) {
        List<ManageReconcileTransactionStatusResponse> responseList = new ArrayList<>();
        for (ManageReconcileTransactionStatus entity : data.getContent()) {
            responseList.add(new ManageReconcileTransactionStatusResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public List<ManageReconcileTransactionStatusDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageReconcileTransactionStatus::toAggregate).toList();
    }

    @Override
    public Long countByCreatedAndNotId(UUID id) {
        return this.repositoryQuery.countByCreatedAndNotId(id);
    }

    @Override
    public Long countByCancelledAndNotId(UUID id) {
        return this.repositoryQuery.countByCancelledAndNotId(id);
    }

    @Override
    public Long countByCompletedAndNotId(UUID id) {
        return this.repositoryQuery.countByCompletedAndNotId(id);
    }

    @Override
    public ManageReconcileTransactionStatusDto findByEReconcileTransactionStatus(EReconcileTransactionStatus transactionStatus) {
        switch (transactionStatus) {
            case CREATED -> {
                return this.repositoryQuery.findByCreated()
                        .map(ManageReconcileTransactionStatus::toAggregate)
                        .orElseThrow(() ->
                                new BusinessException(
                                        DomainErrorMessage.MANAGE_RECONCILE_TRANSACTION_STATUS_NOT_FOUND,
                                        DomainErrorMessage.MANAGE_RECONCILE_TRANSACTION_STATUS_NOT_FOUND.getReasonPhrase()
                                )
                        );
            }
            case CANCELLED -> {
                return this.repositoryQuery.findByCancelled()
                        .map(ManageReconcileTransactionStatus::toAggregate)
                        .orElseThrow(() ->
                                new BusinessException(
                                        DomainErrorMessage.MANAGE_RECONCILE_TRANSACTION_STATUS_NOT_FOUND,
                                        DomainErrorMessage.MANAGE_RECONCILE_TRANSACTION_STATUS_NOT_FOUND.getReasonPhrase()
                                )
                        );
            }
            case COMPLETED -> {
                return this.repositoryQuery.findByCompleted()
                        .map(ManageReconcileTransactionStatus::toAggregate)
                        .orElseThrow(() ->
                                new BusinessException(
                                        DomainErrorMessage.MANAGE_RECONCILE_TRANSACTION_STATUS_NOT_FOUND,
                                        DomainErrorMessage.MANAGE_RECONCILE_TRANSACTION_STATUS_NOT_FOUND.getReasonPhrase()
                                ));
            }
        }
        throw new BusinessException(
                DomainErrorMessage.MANAGE_RECONCILE_TRANSACTION_STATUS_NOT_FOUND,
                DomainErrorMessage.MANAGE_RECONCILE_TRANSACTION_STATUS_NOT_FOUND.getReasonPhrase()
        );
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }
}
