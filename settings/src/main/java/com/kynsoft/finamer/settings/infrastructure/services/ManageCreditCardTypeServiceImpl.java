package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageCreditCardTypeResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageCreditCardTypeDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageCreditCardTypeService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageCreditCardType;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageCreditCardTypeWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageCreditCardTypeReadDataJPARepository;

@Service
public class ManageCreditCardTypeServiceImpl implements IManageCreditCardTypeService {

    @Autowired
    private ManageCreditCardTypeWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManageCreditCardTypeReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManageCreditCardTypeDto dto) {
        ManageCreditCardType data = new ManageCreditCardType(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManageCreditCardTypeDto dto) {
        ManageCreditCardType update = new ManageCreditCardType(dto);

        update.setUpdateAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManageCreditCardTypeDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageCreditCardTypeDto findById(UUID id) {
        Optional<ManageCreditCardType> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_BANK_NOT_FOUND, new ErrorField("id", "Manager Bank not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageCreditCardType> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageCreditCardType> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<ManageCreditCardType> data) {
        List<ManageCreditCardTypeResponse> userSystemsResponses = new ArrayList<>();
        for (ManageCreditCardType p : data.getContent()) {
            userSystemsResponses.add(new ManageCreditCardTypeResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public Long countByFirstDigitAndNotId(Integer firstDigit, UUID id) {
        return this.repositoryQuery.countByFirstDigitAndNotId(firstDigit, id);
    }

}
