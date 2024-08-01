package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageHotelResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageModuleResponse;
import com.kynsoft.finamer.settings.domain.dto.ModuleDto;
import com.kynsoft.finamer.settings.domain.services.IManageModuleService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageHotel;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageModule;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageModuleWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageModuleReadDataJPARepository;
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
public class ManageModuleServiceImpl implements IManageModuleService {

    private final ManageModuleWriteDataJPARepository repositoryCommand;

    private final ManageModuleReadDataJPARepository repositoryQuery;

    public ManageModuleServiceImpl(ManageModuleWriteDataJPARepository repositoryCommand, ManageModuleReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ModuleDto dto) {
        ManageModule data = new ManageModule(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ModuleDto dto) {
        ManageModule update = new ManageModule(dto);

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ModuleDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ModuleDto findById(UUID id) {
        Optional<ManageModule> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "Manage Module not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {


        GenericSpecificationsBuilder<ManageModule> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageModule> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageModule> data) {
        List<ManageModuleResponse> responseList = new ArrayList<>();
        for (ManageModule entity : data.getContent()) {
            responseList.add(new ManageModuleResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
