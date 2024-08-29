package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.dto.ManageNightTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageNightTypeService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageNightType;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageNightTypeWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageNightTypeReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageNightTypeServiceImpl implements IManageNightTypeService {

    @Autowired
    private ManageNightTypeWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManageNightTypeReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManageNightTypeDto dto) {
        ManageNightType data = new ManageNightType(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManageNightTypeDto dto) {
        ManageNightType update = new ManageNightType(dto);

        update.setUpdateAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManageNightTypeDto dto) {
        ManageNightType delete = new ManageNightType(dto);

        delete.setDeleted(Boolean.TRUE);
        delete.setCode(delete.getCode()+ "-" + UUID.randomUUID());

        delete.setDeleteAt(LocalDateTime.now());

        this.repositoryCommand.save(delete);
    }

    @Override
    public ManageNightTypeDto findById(UUID id) {
        Optional<ManageNightType> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "Manage Night Type not found.")));
    }

    @Override
    public boolean existNightTypeByCode(String code) {
        return repositoryQuery.existsManageNightTypeByCode(code);
    }

    @Override
    public ManageNightTypeDto findByCode(String code) {
        Optional<ManageNightType> userSystem = this.repositoryQuery.findManageNightTypeByCode(code);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("code", "Manage Night Type not found.")));
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



    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

}
