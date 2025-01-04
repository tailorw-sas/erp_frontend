package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageBankAccountResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import com.kynsoft.finamer.creditcard.domain.services.IManageBankAccountService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageBankAccount;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ManageBankAccountWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ManageBankAccountReadDataJPARepository;
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
public class ManageBankAccountServiceImpl implements IManageBankAccountService {

    @Autowired
    private final ManageBankAccountWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageBankAccountReadDataJPARepository repositoryQuery;

    public ManageBankAccountServiceImpl(ManageBankAccountWriteDataJPARepository repositoryCommand, ManageBankAccountReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageBankAccountDto dto) {
        ManageBankAccount entity = new ManageBankAccount(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManageBankAccountDto dto) {
        ManageBankAccount entity = new ManageBankAccount(dto);

        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageBankAccountDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageBankAccountDto findById(UUID id) {
        Optional<ManageBankAccount> optionalEntity = repositoryQuery.findById(id);

        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", DomainErrorMessage.NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageBankAccount> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageBankAccount> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

//    @Override
    public Long countByAccountNumberAndNotId(String accountNumber, UUID id) {
        return repositoryQuery.countByAccountNumberAndNotId(accountNumber, id);
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

    private PaginatedResponse getPaginatedResponse(Page<ManageBankAccount> data) {
        List<ManageBankAccountResponse> responseList = new ArrayList<>();
        for (ManageBankAccount entity : data.getContent()) {
            responseList.add(new ManageBankAccountResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

}
