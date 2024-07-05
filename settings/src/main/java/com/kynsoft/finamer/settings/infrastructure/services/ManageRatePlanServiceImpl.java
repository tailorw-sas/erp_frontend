package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageRatePlanResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageRatePlanService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageRatePlan;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageRatePlanWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageRatePlanReadDataJPARepository;
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
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
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
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageRatePlan> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageRatePlan> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<ManageRatePlan> data) {
        List<ManageRatePlanResponse> userSystemsResponses = new ArrayList<>();
        for (ManageRatePlan p : data.getContent()) {
            userSystemsResponses.add(new ManageRatePlanResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

}
