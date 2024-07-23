package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageRatePlanService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageRatePlan;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageRatePlanWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageRatePlanReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageRatePlanServiceImpl implements IManageRatePlanService {

    @Autowired
    private ManageRatePlanWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManageRatePlanReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManageRatePlanDto dto) {
        ManageRatePlan data = new ManageRatePlan(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManageRatePlanDto dto) {
        ManageRatePlan update = new ManageRatePlan(dto);

        update.setUpdateAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManageRatePlanDto dto) {
        ManageRatePlan delete = new ManageRatePlan(dto);

        delete.setDeleted(Boolean.TRUE);
        delete.setCode(delete.getCode()+ "-" + UUID.randomUUID());

        delete.setDeleteAt(LocalDateTime.now());

        this.repositoryCommand.save(delete);
    }

    @Override
    public ManageRatePlanDto findById(UUID id) {
        Optional<ManageRatePlan> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_RATE_PLAN_NOT_FOUND, new ErrorField("id", "Manage Rate Plan not found.")));
    }

    @Override
    public ManageRatePlanDto findByCode(String code) {
        Optional<ManageRatePlan> userSystem = this.repositoryQuery.findManageRatePlanByCode(code);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_RATE_PLAN_NOT_FOUND, new ErrorField("code", "Manage Rate Plan not found.")));
    }

    @Override
    public boolean existByCode(String code) {
        return repositoryQuery.existsByCode(code);
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
