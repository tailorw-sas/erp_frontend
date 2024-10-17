package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagePaymentTransactionTypeResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagePaymentTransactionTypeDto;

import com.kynsoft.finamer.settings.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManagePaymentTransactionType;

import com.kynsoft.finamer.settings.infrastructure.repository.command.ManagePaymentTransactionTypeWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManagePaymentTransactionTypeReadDataJPARepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@EnableCaching
public class ManagePaymentTransactionTypeServiceImpl implements IManagePaymentTransactionTypeService {

    private final ManagePaymentTransactionTypeReadDataJPARepository repositoryQuery;
    private final ManagePaymentTransactionTypeWriteDataJPARepository repositoryCommand;

    public ManagePaymentTransactionTypeServiceImpl(ManagePaymentTransactionTypeReadDataJPARepository repositoryQuery,
                                                   ManagePaymentTransactionTypeWriteDataJPARepository repositoryCommand) {
        this.repositoryQuery = repositoryQuery;
        this.repositoryCommand = repositoryCommand;
    }

    @Override
    @CacheEvict(cacheNames = {"managePaymentTransactionType", "managePaymentTransactionTypeAll"}, allEntries = true)
    public UUID create(ManagePaymentTransactionTypeDto dto) {
        ManagePaymentTransactionType entity = new ManagePaymentTransactionType(dto);
        ManagePaymentTransactionType saved = repositoryCommand.save(entity);
        return saved.getId();
    }

    @Override
    @CacheEvict(cacheNames = {"managePaymentTransactionType", "managePaymentTransactionTypeAll"}, allEntries = true)
    public void update(ManagePaymentTransactionTypeDto dto) {
        repositoryCommand.save(new ManagePaymentTransactionType(dto));
    }

    @Override
    @CacheEvict(cacheNames = {"managePaymentTransactionType", "managePaymentTransactionTypeAll"}, allEntries = true)
    public void delete(ManagePaymentTransactionTypeDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    @Cacheable(cacheNames = "managePaymentTransactionType", key = "#id", unless = "#result == null")
    public ManagePaymentTransactionTypeDto findById(UUID id) {
        Optional<ManagePaymentTransactionType> optionalEntity = repositoryQuery.findById(id);
        if (optionalEntity.isPresent()) {
            return optionalEntity.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_PAYMENT_TRANSACTION_TYPE_NOT_FOUND,
                new ErrorField("id", "The manager payment source not found.")));
    }

    @Override
    @Cacheable(cacheNames = "managePaymentTransactionTypeAll", unless = "#result == null")
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManagePaymentTransactionType> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagePaymentTransactionType> data = this.repositoryQuery.findAll(specifications, pageable);
        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManagePaymentTransactionType> data) {
        List<ManagePaymentTransactionTypeResponse> responseList = new ArrayList<>();
        for (ManagePaymentTransactionType entity : data.getContent()) {
            responseList.add(new ManagePaymentTransactionTypeResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public Long countByDefaultAndNotId(UUID id) {
        return this.repositoryQuery.countByDefaultAndNotId(id);
    }

    @Override
    @Cacheable(cacheNames = "managePaymentTransactionTypeAll", unless = "#result == null")
    public PaginatedResponse findWithApplyDepositAndDepositFalse(Boolean applyDeposit, Boolean deposit, Boolean defaults, Pageable pageable) {
        Page<ManagePaymentTransactionType> data = this.repositoryQuery.findWithApplyDepositAndDepositFalse(applyDeposit, deposit, defaults, pageable);
        return getPaginatedResponse(data);
    }

    @Override
    @Cacheable(cacheNames = "managePaymentTransactionTypeAll", unless = "#result == null or #result.isEmpty()")
    public List<ManagePaymentTransactionTypeDto> findAllToReplicate() {
        List<ManagePaymentTransactionType> objects = this.repositoryQuery.findAll();
        List<ManagePaymentTransactionTypeDto> objectDtos = new ArrayList<>();
        for (ManagePaymentTransactionType object : objects) {
            objectDtos.add(object.toAggregate());
        }
        return objectDtos;
    }

    @Override
    public Long countByIncomeDefaultAndNotId(UUID id) {
        return this.repositoryQuery.countByIncomeDefaultAndNotId(id);
    }

    @Override
    public Long countByApplyDepositAndNotId(UUID id) {
        return this.repositoryQuery.countByApplyDepositAndNotId(id);
    }

    @Override
    public Long countByDepositAndNotId(UUID id) {
        return this.repositoryQuery.countByDepositAndNotId(id);
    }

    @Override
    public Long countByPaymentInvoiceAndNotId(UUID id) {
        return this.repositoryQuery.countByPaymentInvoiceAndNotId(id);
    }
}