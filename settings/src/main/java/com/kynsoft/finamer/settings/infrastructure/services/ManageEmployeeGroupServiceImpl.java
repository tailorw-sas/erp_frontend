package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageEmployeeGroupResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageEmployeeGroupDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;

import com.kynsoft.finamer.settings.domain.services.IManageEmployeeGroupService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageEmployeeGroup;


import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageEmployeeGroupWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageEmployeeGroupReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageEmployeeGroupServiceImpl implements IManageEmployeeGroupService {

    @Autowired
    private ManageEmployeeGroupReadDataJPARepository repositoryQuery;

    @Autowired
    private ManageEmployeeGroupWriteDataJPARepository repositoryCommand;

    @Override
    public UUID create(ManageEmployeeGroupDto dto) {
        ManageEmployeeGroup entity = new ManageEmployeeGroup(dto);
        ManageEmployeeGroup saved = repositoryCommand.save(entity);

        return saved.getId();
    }

    @Override
    public void update(ManageEmployeeGroupDto dto) {
        repositoryCommand.save(new ManageEmployeeGroup(dto));
    }

    @Override
    public void delete(ManageEmployeeGroupDto dto) {
        ManageEmployeeGroup delete = new ManageEmployeeGroup(dto);
        delete.setDeleted(Boolean.TRUE);
        delete.setCode(delete.getCode()+ "-" + UUID.randomUUID());
        delete.setStatus(Status.INACTIVE);

        this.repositoryCommand.save(delete);
    }

    @Override
    public ManageEmployeeGroupDto findById(UUID id) {
        Optional<ManageEmployeeGroup> optionalEntity = repositoryQuery.findById(id);
        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_EMPLOYEE_GROUP_NOT_FOUND, new ErrorField("id", "The manager payment source not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageEmployeeGroup> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageEmployeeGroup> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageEmployeeGroup> data) {
        List<ManageEmployeeGroupResponse> responseList = new ArrayList<>();
        for (ManageEmployeeGroup entity : data.getContent()) {
            responseList.add(new ManageEmployeeGroupResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }
}
