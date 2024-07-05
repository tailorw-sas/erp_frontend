package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerChargeTypeResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerChargeTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManagerChargeTypeService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManagerChargeType;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManagerChargeTypeWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManagerChargeTypeReadDataJPARepository;
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
public class ManagerChargeTypeServiceImpl implements IManagerChargeTypeService {

    @Autowired
    private ManagerChargeTypeWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManagerChargeTypeReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManagerChargeTypeDto dto) {
        ManagerChargeType data = new ManagerChargeType(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManagerChargeTypeDto dto) {
        ManagerChargeType update = new ManagerChargeType(dto);

        update.setUpdateAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManagerChargeTypeDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManagerChargeTypeDto findById(UUID id) {
        Optional<ManagerChargeType> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_BANK_NOT_FOUND, new ErrorField("id", "Manager Bank not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManagerChargeType> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagerChargeType> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<ManagerChargeType> data) {
        List<ManagerChargeTypeResponse> userSystemsResponses = new ArrayList<>();
        for (ManagerChargeType p : data.getContent()) {
            userSystemsResponses.add(new ManagerChargeTypeResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

}
