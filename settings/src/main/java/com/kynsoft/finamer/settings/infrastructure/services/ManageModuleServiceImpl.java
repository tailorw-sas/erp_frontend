package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.settings.domain.dto.ManageModuleDto;
import com.kynsoft.finamer.settings.domain.services.IManageModuleService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageModule;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageModuleWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageModuleReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageModuleServiceImpl implements IManageModuleService {

    @Autowired
    private final ManageModuleWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageModuleReadDataJPARepository repositoryQuery;

    public ManageModuleServiceImpl(ManageModuleWriteDataJPARepository repositoryCommand, ManageModuleReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageModuleDto dto) {
        ManageModule data = new ManageModule(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManageModuleDto dto) {
        ManageModule update = new ManageModule(dto);

        update.setUpdateAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManageModuleDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageModuleDto findById(UUID id) {
        Optional<ManageModule> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "Manage Module not found.")));
    }
}
