package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageDepartmentGroupResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageDepartmentGroupDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;

import com.kynsoft.finamer.settings.domain.services.IManageDepartmentGroupService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageDepartmentGroup;


import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageDepartmentGroupWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageDepartmentGroupReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageDepartmentGroupServiceImpl implements IManageDepartmentGroupService {

    @Autowired
    private ManageDepartmentGroupReadDataJPARepository repositoryQuery;

    @Autowired
    private ManageDepartmentGroupWriteDataJPARepository repositoryCommand;

    @Override
    public UUID create(ManageDepartmentGroupDto dto) {
        ManageDepartmentGroup entity = new ManageDepartmentGroup(dto);
        ManageDepartmentGroup saved = repositoryCommand.save(entity);

        return saved.getId();
    }

    @Override
    public void update(ManageDepartmentGroupDto dto) {
        repositoryCommand.save(new ManageDepartmentGroup(dto));
    }

    @Override
    public void delete(ManageDepartmentGroupDto dto) {
        ManageDepartmentGroup delete = new ManageDepartmentGroup(dto);
        delete.setDeleted(Boolean.TRUE);
        delete.setCode(delete.getCode()+ "-" + UUID.randomUUID());
        delete.setStatus(Status.INACTIVE);

        this.repositoryCommand.save(delete);
    }

    @Override
    public ManageDepartmentGroupDto findById(UUID id) {
        Optional<ManageDepartmentGroup> optionalEntity = repositoryQuery.findById(id);
        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_DEPARTMENT_GROUP_NOT_FOUND, new ErrorField("id", "The manager payment source not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageDepartmentGroup> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageDepartmentGroup> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageDepartmentGroup> data) {
        List<ManageDepartmentGroupResponse> responseList = new ArrayList<>();
        for (ManageDepartmentGroup entity : data.getContent()) {
            responseList.add(new ManageDepartmentGroupResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }
}
