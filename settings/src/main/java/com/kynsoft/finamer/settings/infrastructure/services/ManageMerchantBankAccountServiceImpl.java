package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageMerchantBankAccountResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantBankAccountDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantBankAccountService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageMerchantBankAccount;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageMerchantBankAccountWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageMechantBankAccountReadDataJPARepository;
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
public class ManageMerchantBankAccountServiceImpl implements IManageMerchantBankAccountService {

    @Autowired
    private ManageMerchantBankAccountWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManageMechantBankAccountReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManageMerchantBankAccountDto dto) {
        ManageMerchantBankAccount data = new ManageMerchantBankAccount(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManageMerchantBankAccountDto dto) {
        ManageMerchantBankAccount update = new ManageMerchantBankAccount(dto);

        update.setUpdateAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManageMerchantBankAccountDto dto) {
        ManageMerchantBankAccount delete = new ManageMerchantBankAccount(dto);

        delete.setDeleted(Boolean.TRUE);
        delete.setDeleteAt(LocalDateTime.now());
        delete.setStatus(Status.INACTIVE);
        delete.setAccountNumber(delete.getAccountNumber() + " + " + UUID.randomUUID());

        this.repositoryCommand.save(delete);
    }

    @Override
    public ManageMerchantBankAccountDto findById(UUID id) {
        Optional<ManageMerchantBankAccount> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_MERCHANT_BANK_ACCOUNT_NOT_FOUND, new ErrorField("id", "Manage Merchant Bank Account not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageMerchantBankAccount> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageMerchantBankAccount> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<ManageMerchantBankAccount> data) {
        List<ManageMerchantBankAccountResponse> userSystemsResponses = new ArrayList<>();
        for (ManageMerchantBankAccount p : data.getContent()) {
            userSystemsResponses.add(new ManageMerchantBankAccountResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByManagerMerchantANDManagerCurrencyIdNotId(UUID id, UUID managerMerchant, UUID manageBank, String accountNumber) {
        return this.repositoryQuery.countByManagerMerchantANDManagerCurrencyIdNotId(id, managerMerchant, manageBank, accountNumber);
    }

}
