package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessException;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManagePaymentTransactionStatusResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManagePaymentTransactionStatusDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.EHotelPaymentStatus;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.domain.services.IManagePaymentTransactionStatus;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManagePaymentTransactionStatus;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ManagePaymentTransactionStatusWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ManagePaymentTransactionStatusReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ManagePaymentTransactionStatusImpl implements IManagePaymentTransactionStatus {

    private final ManagePaymentTransactionStatusWriteDataJPARepository repositoryCommand;

    private final ManagePaymentTransactionStatusReadDataJPARepository repositoryQuery;

    public ManagePaymentTransactionStatusImpl(ManagePaymentTransactionStatusWriteDataJPARepository repositoryCommand, ManagePaymentTransactionStatusReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public ManagePaymentTransactionStatusDto create(ManagePaymentTransactionStatusDto dto) {
        ManagePaymentTransactionStatus entity = new ManagePaymentTransactionStatus(dto);
        return this.repositoryCommand.save(entity).toAggregate();
    }

    @Override
    public void update(ManagePaymentTransactionStatusDto dto) {
        ManagePaymentTransactionStatus entity = new ManagePaymentTransactionStatus(dto);
        entity.setUpdatedAt(LocalDateTime.now());
        this.repositoryCommand.save(entity).toAggregate();
    }

    @Override
    public void delete(UUID id) {
        try{
            this.repositoryCommand.deleteById(id);
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManagePaymentTransactionStatusDto findById(UUID id) {
        return this.repositoryQuery.findById(id)
                .map(ManagePaymentTransactionStatus::toAggregate)
                .orElseThrow(()-> new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_NOT_FOUND,
                                        new ErrorField("id", DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_NOT_FOUND.getReasonPhrase())))
        );
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManagePaymentTransactionStatus> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagePaymentTransactionStatus> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return this.repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public Long countByInProgressAndNotId(UUID id) {
        return this.repositoryQuery.countByInProgressAndNotId(id);
    }

    @Override
    public Long countByCompletedAndNotId(UUID id) {
        return this.repositoryQuery.countByCompletedAndNotId(id);
    }

    @Override
    public Long countByCancelledAndNotId(UUID id) {
        return this.repositoryQuery.countByCancelledAndNotId(id);
    }

    @Override
    public Long countByAppliedAndNotId(UUID id) {
        return this.repositoryQuery.countByAppliedAndNotId(id);
    }

    @Override
    public ManagePaymentTransactionStatusDto findByEReconcileTransactionStatus(EHotelPaymentStatus hotelPaymentStatus) {
        switch (hotelPaymentStatus){
            case IN_PROGRESS -> {
                return this.repositoryQuery.findByInProgress().map(ManagePaymentTransactionStatus::toAggregate).orElseThrow(()->
                        new BusinessException(
                                DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_IN_PROGRESS_NOT_FOUND,
                                DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_IN_PROGRESS_NOT_FOUND.getReasonPhrase())
                );
            }
            case COMPLETED -> {
                return this.repositoryQuery.findByCompleted().map(ManagePaymentTransactionStatus::toAggregate).orElseThrow(()->
                        new BusinessException(
                                DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_COMPLETED_NOT_FOUND,
                                DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_COMPLETED_NOT_FOUND.getReasonPhrase())
                );
            }
            case CANCELLED -> {
                return this.repositoryQuery.findByCancelled().map(ManagePaymentTransactionStatus::toAggregate).orElseThrow(()->
                        new BusinessException(
                                DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_CANCELLED_NOT_FOUND,
                                DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_CANCELLED_NOT_FOUND.getReasonPhrase())
                );
            }
            case APPLIED -> {
                return this.repositoryQuery.findByApplied().map(ManagePaymentTransactionStatus::toAggregate).orElseThrow(()->
                        new BusinessException(
                                DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_APPLIED_NOT_FOUND,
                                DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_APPLIED_NOT_FOUND.getReasonPhrase())
                );
            }
        }
        throw new BusinessException(
                DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_NOT_FOUND,
                DomainErrorMessage.MANAGE_HOTEL_PAYMENT_STATUS_NOT_FOUND.getReasonPhrase());
    }

    @Override
    public List<ManagePaymentTransactionStatusDto> findByIds(List<UUID> ids) {
        return this.repositoryQuery.findAllById(ids).stream().map(ManagePaymentTransactionStatus::toAggregate).collect(Collectors.toList());
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

    private PaginatedResponse getPaginatedResponse(Page<ManagePaymentTransactionStatus> data) {
        List<ManagePaymentTransactionStatusResponse> responses = new ArrayList<>();

        for (ManagePaymentTransactionStatus p : data.getContent()) {
            responses.add(new ManagePaymentTransactionStatusResponse(p.toAggregate()));
        }

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
