package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageCityStateResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageCityStateDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageCityStateService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageCityState;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageCityStateWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageCityStateReadDataJPARepository;
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
public class ManageCityStateServiceImpl implements IManageCityStateService {

    @Autowired
    private ManageCityStateWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManageCityStateReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManageCityStateDto dto) {
        ManageCityState data = new ManageCityState(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManageCityStateDto dto) {
        ManageCityState update = new ManageCityState(dto);

        update.setUpdateAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManageCityStateDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageCityStateDto findById(UUID id) {
        Optional<ManageCityState> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_CITY_STATE_NOT_FOUND, new ErrorField("id", "Manage City State not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageCityState> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageCityState> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<ManageCityState> data) {
        List<ManageCityStateResponse> userSystemsResponses = new ArrayList<>();
        for (ManageCityState p : data.getContent()) {
            userSystemsResponses.add(new ManageCityStateResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

}
