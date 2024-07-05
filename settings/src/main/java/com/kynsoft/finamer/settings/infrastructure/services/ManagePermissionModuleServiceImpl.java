package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagePermissionModuleResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePermissionModuleDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;

import com.kynsoft.finamer.settings.domain.services.IManagePermissionModuleService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManagePermissionModule;


import com.kynsoft.finamer.settings.infrastructure.repository.command.ManagePermissionModuleWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManagePermissionModuleReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManagePermissionModuleServiceImpl implements IManagePermissionModuleService {

    @Autowired
    private ManagePermissionModuleReadDataJPARepository repositoryQuery;

    @Autowired
    private ManagePermissionModuleWriteDataJPARepository repositoryCommand;

    @Override
    public UUID create(ManagePermissionModuleDto dto) {
        ManagePermissionModule entity = new ManagePermissionModule(dto);
        ManagePermissionModule saved = repositoryCommand.save(entity);

        return saved.getId();
    }

    @Override
    public void update(ManagePermissionModuleDto dto) {
        repositoryCommand.save(new ManagePermissionModule(dto));
    }

    @Override
    public void delete(ManagePermissionModuleDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManagePermissionModuleDto findById(UUID id) {
        Optional<ManagePermissionModule> optionalEntity = repositoryQuery.findById(id);
        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_PERMISSION_MODULE_NOT_FOUND, new ErrorField("id", "The manager payment source not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManagePermissionModule> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagePermissionModule> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManagePermissionModule> data) {
        List<ManagePermissionModuleResponse> responseList = new ArrayList<>();
        for (ManagePermissionModule entity : data.getContent()) {
            responseList.add(new ManagePermissionModuleResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }
}
