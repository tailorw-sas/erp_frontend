package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.payment.application.query.objectResponse.ManageBankAccountResponse;
import com.kynsoft.finamer.payment.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.payment.domain.services.IManageBankAccountService;
import com.kynsoft.finamer.payment.infrastructure.identity.ManageBankAccount;
import com.kynsoft.finamer.payment.infrastructure.repository.command.ManageBankAccountWriteDataJPARepository;
import com.kynsoft.finamer.payment.infrastructure.repository.query.ManageBankAccountReadDataJPARepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_BANK_ACCOUNT_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MANAGE_BANK_ACCOUNT_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public ManageBankAccountDto findByAccountNumber(String accountNumber) {
        return repositoryQuery.findManageBankAccountByAccountNumber(accountNumber)
                .map(ManageBankAccount::toAggregate)
                .orElseThrow(()->new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_BANK_ACCOUNT_NOT_FOUND, new ErrorField("accountNumber", DomainErrorMessage.MANAGE_BANK_ACCOUNT_NOT_FOUND.getReasonPhrase()))));
    }

    @Override
    public boolean existByAccountNumber(String accountNumber) {
        return repositoryQuery.existsByAccountNumber(accountNumber);
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {

        GenericSpecificationsBuilder<ManageBankAccount> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageBankAccount> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageBankAccount> data) {
        List<ManageBankAccountResponse> responses = new ArrayList<>();
        for (ManageBankAccount p : data.getContent()) {
            responses.add(new ManageBankAccountResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public List<ManageBankAccountDto> findAllByHotel(UUID hotelId) {
        Optional<List<ManageBankAccount>> result = repositoryQuery.findAllByHotel(hotelId);
        return result.map(paymentDetails -> paymentDetails.stream().map(ManageBankAccount::toAggregate).toList()).orElse(Collections.EMPTY_LIST);
    }

}
