package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBankAccountDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageBankAccountService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageBankAccount;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageBankAccountWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageBankAccountReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
