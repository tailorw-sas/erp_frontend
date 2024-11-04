package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManagerMerchantCurrencyResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantCurrencyDto;
import com.kynsoft.finamer.creditcard.domain.services.IManagerMerchantCurrencyService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManagerMerchantCurrency;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ManagerMerchantCurrencyWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ManagerMerchantCurrencyReadDataJPARepository;
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
public class ManagerMerchantCurrencyServiceImpl implements IManagerMerchantCurrencyService {

    @Autowired
    private ManagerMerchantCurrencyWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManagerMerchantCurrencyReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManagerMerchantCurrencyDto dto) {
        ManagerMerchantCurrency data = new ManagerMerchantCurrency(dto);
        ManagerMerchantCurrency object = this.repositoryCommand.save(data);
        return object.getId();
    }

    @Override
    public void update(ManagerMerchantCurrencyDto dto) {
        ManagerMerchantCurrency update = new ManagerMerchantCurrency(dto);
        update.setUpdateAt(LocalDateTime.now());
        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManagerMerchantCurrencyDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManagerMerchantCurrencyDto findById(UUID id) {
        Optional<ManagerMerchantCurrency> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_MERCHANT_CURRENCY_NOT_FOUND, new ErrorField("id", "Manager Merchant Currency not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {

        GenericSpecificationsBuilder<ManagerMerchantCurrency> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagerMerchantCurrency> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManagerMerchantCurrency> data) {
        List<ManagerMerchantCurrencyResponse> userSystemsResponses = new ArrayList<>();
        for (ManagerMerchantCurrency p : data.getContent()) {
            userSystemsResponses.add(new ManagerMerchantCurrencyResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(UUID managerMerchant, UUID managerCurrency) {
        return this.repositoryQuery.countByManagerMerchantAndManagerCurrency(managerMerchant, managerCurrency);
    }

    @Override
    public Long countByManagerMerchantANDManagerCurrencyIdNotId(UUID id, UUID managerMerchant, UUID managerCurrency) {
        return this.repositoryQuery.countByManagerMerchantAndManagerCurrencyNotId(id, managerMerchant, managerCurrency);
    }

}
