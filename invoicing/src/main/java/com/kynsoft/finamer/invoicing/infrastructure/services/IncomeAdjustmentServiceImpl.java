package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.IncomeAdjustmentResponse;
import com.kynsoft.finamer.invoicing.domain.dto.IncomeAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IIncomeAdjustmentService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.IncomeAdjustment;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.IncomeAdjustmentWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.IncomeAdjustmentReadDataJPARepository;
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
public class IncomeAdjustmentServiceImpl implements IIncomeAdjustmentService {

    @Autowired
    private IncomeAdjustmentWriteDataJPARepository repositoryCommand;

    @Autowired
    private IncomeAdjustmentReadDataJPARepository repositoryQuery;

    @Override
    public IncomeAdjustmentDto create(IncomeAdjustmentDto dto) {
        IncomeAdjustment data = new IncomeAdjustment(dto);
        return this.repositoryCommand.save(data).toAggregate();
    }

    @Override
    public void update(IncomeAdjustmentDto dto) {
        IncomeAdjustment update = new IncomeAdjustment(dto);

        update.setUpdatedAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(IncomeAdjustmentDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public IncomeAdjustmentDto findById(UUID id) {
        Optional<IncomeAdjustment> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.INCOME_ADJUSTMENT_NOT_FOUND, new ErrorField("id", DomainErrorMessage.INCOME_ADJUSTMENT_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<IncomeAdjustment> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<IncomeAdjustment> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<IncomeAdjustment> data) {
        List<IncomeAdjustmentResponse> responses = new ArrayList<>();
        for (IncomeAdjustment p : data.getContent()) {
            responses.add(new IncomeAdjustmentResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

}
