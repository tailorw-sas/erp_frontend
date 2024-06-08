package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerAccountTypeResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerAccountTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManagerAccountTypeService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManagerAccountType;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManagerAccountTypeWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManagerAccountTypeReadDataJPARepository;
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
public class ManagerAccountTypeServiceImpl implements IManagerAccountTypeService {

    @Autowired
    private ManagerAccountTypeWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManagerAccountTypeReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManagerAccountTypeDto dto) {
        ManagerAccountType data = new ManagerAccountType(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManagerAccountTypeDto dto) {
        ManagerAccountType update = new ManagerAccountType(dto);

        update.setUpdateAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManagerAccountTypeDto dto) {
        ManagerAccountType delete = new ManagerAccountType(dto);

        delete.setDeleted(Boolean.TRUE);
        delete.setCode(delete.getCode() + "-" + UUID.randomUUID());
        delete.setStatus(Status.INACTIVE);
        delete.setDeleteAt(LocalDateTime.now());

        this.repositoryCommand.save(delete);
    }

    @Override
    public ManagerAccountTypeDto findById(UUID id) {
        Optional<ManagerAccountType> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_ACCOUNT_TYPE_NOT_FOUND, new ErrorField("id", "Manager Account Type not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManagerAccountType> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagerAccountType> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<ManagerAccountType> data) {
        List<ManagerAccountTypeResponse> userSystemsResponses = new ArrayList<>();
        for (ManagerAccountType p : data.getContent()) {
            userSystemsResponses.add(new ManagerAccountTypeResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

}
