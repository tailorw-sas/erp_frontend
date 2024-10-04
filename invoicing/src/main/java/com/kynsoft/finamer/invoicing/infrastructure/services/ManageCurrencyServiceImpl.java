package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageCurrencyResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageCurrencyDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IManageCurrencyService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageCurrency;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageCurrencyWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageCurrencyReadDataJPARepository;
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
public class ManageCurrencyServiceImpl implements IManageCurrencyService {

    @Autowired
    private ManageCurrencyWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManageCurrencyReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManageCurrencyDto dto) {
        ManageCurrency data = new ManageCurrency(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManageCurrencyDto dto) {
        ManageCurrency update = new ManageCurrency(dto);

        update.setUpdateAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManageCurrencyDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageCurrencyDto findById(UUID id) {
        Optional<ManageCurrency> userSystem = this.repositoryQuery.findById(id);
        return userSystem.map(ManageCurrency::toAggregate).orElse(null);
        //        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_CURRENCY_NOT_FOUND, new ErrorField("id", "Element cannot be deleted has a related element.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
//        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageCurrency> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageCurrency> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<ManageCurrency> data) {
        List<ManageCurrencyResponse> userSystemsResponses = new ArrayList<>();
        for (ManageCurrency p : data.getContent()) {
            userSystemsResponses.add(new ManageCurrencyResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

}
