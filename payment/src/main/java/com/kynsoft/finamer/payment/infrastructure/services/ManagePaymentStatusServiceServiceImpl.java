package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManagePaymentStatusResponse;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentStatusDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManagePaymentStatus;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManagePaymentStatusWriteDataJpaRepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManagePaymentStatusReadDataJpaRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ManagePaymentStatusServiceServiceImpl implements IManagePaymentStatusService {

    @Autowired
    private ManagePaymentStatusWriteDataJpaRepository repositoryCommand;

    @Autowired
    private ManagePaymentStatusReadDataJpaRepository repositoryQuery;

    @Override
    public UUID create(ManagePaymentStatusDto dto) {
        ManagePaymentStatus entity = new ManagePaymentStatus(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManagePaymentStatusDto dto) {
        ManagePaymentStatus entity = new ManagePaymentStatus(dto);
        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManagePaymentStatusDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManagePaymentStatusDto findById(UUID id) {
        Optional<ManagePaymentStatus> result = this.repositoryQuery.findById(id);
        if (result.isPresent()) {
            return result.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_PAYMENT_STATUS_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MANAGER_PAYMENT_STATUS_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public ManagePaymentStatusDto findByCode(String code) {
        Optional<ManagePaymentStatus> result = this.repositoryQuery.findByCodeAndStatus(code,Status.ACTIVE.name());
        if (result.isPresent()) {
            return result.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_PAYMENT_STATUS_NOT_FOUND, new ErrorField("code", DomainErrorMessage.MANAGER_PAYMENT_STATUS_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManagePaymentStatus> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagePaymentStatus> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<ManagePaymentStatus> data) {
        List<ManagePaymentStatusResponse> responses = new ArrayList<>();
        for (ManagePaymentStatus p : data.getContent()) {
            responses.add(new ManagePaymentStatusResponse(p.toAggregate()));
        }

        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public ManagePaymentStatusDto findByApplied() {
        Optional<ManagePaymentStatus> result = this.repositoryQuery.findByApplied();
        if (result.isPresent()) {
            return result.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_PAYMENT_STATUS_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MANAGER_PAYMENT_STATUS_NOT_FOUND.getReasonPhrase())));
    }

}
