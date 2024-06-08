package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageReportParamTypeResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageReportParamTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageReportParamTypeService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageReportParamType;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageReportParamTypeWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageReportParamTypeReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageReportParamTypeServiceImpl implements IManageReportParamTypeService {

    @Autowired
    private final ManageReportParamTypeWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageReportParamTypeReadDataJPARepository repositoryQuery;

    public ManageReportParamTypeServiceImpl(ManageReportParamTypeWriteDataJPARepository repositoryCommand, ManageReportParamTypeReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageReportParamTypeDto dto) {
        ManageReportParamType entity = new ManageReportParamType(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManageReportParamTypeDto dto) {
        ManageReportParamType entity = new ManageReportParamType(dto);

        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageReportParamTypeDto dto) {
        ManageReportParamType delete = new ManageReportParamType(dto);

        delete.setDeleted(Boolean.TRUE);
        delete.setName(delete.getName()+ "-" + UUID.randomUUID());
        delete.setStatus(Status.INACTIVE);
        delete.setDeletedAt(LocalDateTime.now());

        repositoryCommand.save(delete);
    }

    @Override
    public ManageReportParamTypeDto findById(UUID id) {
        Optional<ManageReportParamType> optionalEntity = repositoryQuery.findById(id);

        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_REPORT_PARAM_TYPE_NOT_FOUND, new ErrorField("id", "The source not found.")));

    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageReportParamType> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageReportParamType> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public Long countByNameAndNotId(String name, UUID id) {
        return repositoryQuery.countByNameAndNotId(name, id);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageReportParamType> data) {
        List<ManageReportParamTypeResponse> responseList = new ArrayList<>();
        for (ManageReportParamType entity : data.getContent()) {
            responseList.add(new ManageReportParamTypeResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
