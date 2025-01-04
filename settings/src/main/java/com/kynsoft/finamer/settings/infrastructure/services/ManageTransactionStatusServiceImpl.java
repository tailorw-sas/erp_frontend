package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageTransactionStatusResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageTransactionStatusDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageTransactionStatusService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageTransactionStatus;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageTransactionStatusWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageTransactionStatusReadDataJPARepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageTransactionStatusServiceImpl implements IManageTransactionStatusService {

    @Autowired
    private ManageTransactionStatusWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManageTransactionStatusReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManageTransactionStatusDto dto) {
        ManageTransactionStatus data = new ManageTransactionStatus(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManageTransactionStatusDto dto) {
        ManageTransactionStatus update = new ManageTransactionStatus(dto);

        update.setUpdateAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManageTransactionStatusDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageTransactionStatusDto findById(UUID id) {
        Optional<ManageTransactionStatus> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_BANK_NOT_FOUND, new ErrorField("id", "Manager Bank not found.")));
    }

    @Override
    public List<ManageTransactionStatusDto> findByIds(List<UUID> ids) {
        return this.repositoryQuery.findAllById(ids).stream().map(ManageTransactionStatus::toAggregate).toList();
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageTransactionStatus> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageTransactionStatus> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<ManageTransactionStatus> data) {
        List<ManageTransactionStatusResponse> userSystemsResponses = new ArrayList<>();

        for (ManageTransactionStatus p : data.getContent()) {
            userSystemsResponses.add(new ManageTransactionStatusResponse(p.toAggregate()));
        }

        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public List<ManageTransactionStatusDto> findAllToReplicate() {
        List<ManageTransactionStatus> objects = this.repositoryQuery.findAll();
        List<ManageTransactionStatusDto> objectDtos = new ArrayList<>();

        for (ManageTransactionStatus object : objects) {
            objectDtos.add(object.toAggregate());
        }

        return objectDtos;
    }

}
