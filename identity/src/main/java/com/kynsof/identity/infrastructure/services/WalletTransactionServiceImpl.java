package com.kynsof.identity.infrastructure.services;

import com.kynsof.identity.application.query.walletTransaction.search.WalletTransactionResponse;
import com.kynsof.identity.domain.dto.WalletTransactionDto;
import com.kynsof.identity.domain.dto.enumType.TransactionType;
import com.kynsof.identity.domain.interfaces.service.IWalletTransactionService;
import com.kynsof.identity.infrastructure.identity.Business;
import com.kynsof.identity.infrastructure.identity.Wallet;
import com.kynsof.identity.infrastructure.identity.WalletTransaction;
import com.kynsof.identity.infrastructure.repository.command.WalletTransactionWriteDataJPARepository;
import com.kynsof.identity.infrastructure.repository.command.WalletWriteDataJPARepository;
import com.kynsof.identity.infrastructure.repository.query.WalletTransactionReadDataJPARepository;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WalletTransactionServiceImpl implements IWalletTransactionService {

    @Autowired
    private WalletTransactionWriteDataJPARepository repositoryCommand;

    @Autowired
    private WalletTransactionReadDataJPARepository repositoryQuery;

    @Autowired
    private WalletWriteDataJPARepository walletWriteDataJPARepository;



    @Override
    public UUID create(WalletTransactionDto object) {

        WalletTransaction entity = this.repositoryCommand.save(new WalletTransaction(object));
        walletWriteDataJPARepository.save(new Wallet(object.getWalletDto()));
        return entity.getId();
    }

    @Override
    public void update(WalletTransactionDto objectDto) {
        this.repositoryCommand.save(new WalletTransaction(objectDto));
    }


    @Override
    public WalletTransactionDto getById(UUID id) {

        Optional<WalletTransaction> object = this.repositoryQuery.findById(id);
        if (object.isPresent()) {
            return object.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("id", "WalletTransaction not found.")));
    }

   // @Cacheable(cacheNames = CacheConfig.BUSINESS_CACHE, unless = "#result == null")
    @Override
    public WalletTransactionDto findById(UUID id) {
        Optional<WalletTransaction> object = this.repositoryQuery.findById(id);
        if (object.isPresent()) {
            return object.get().toAggregate();
        } else {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_NOT_FOUND,
                    new ErrorField("id", "Business not found.")));
        }
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCreteria(filterCriteria);
        GenericSpecificationsBuilder<Business> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<WalletTransaction> data = this.repositoryQuery.findAll(specifications, pageable);
        return getPaginatedResponse(data);
    }

    private void filterCreteria(List<FilterCriteria> filterCriteria) {
        for (FilterCriteria filter : filterCriteria) {
            if ("type".equals(filter.getKey()) && filter.getValue() instanceof String) {
                try {
                    TransactionType enumValue = TransactionType.valueOf((String) filter.getValue());
                    filter.setValue(enumValue);
                } catch (IllegalArgumentException e) {
                    System.err.println("Valor inválido para el tipo Enum Wallet: " + filter.getValue());
                }
            }
        }
    }

    private PaginatedResponse getPaginatedResponse(Page<WalletTransaction> data) {
        List<WalletTransactionResponse> patients = new ArrayList<>();
        for (WalletTransaction o : data.getContent()) {
            patients.add(new WalletTransactionResponse(o.toAggregate()));
        }
        return new PaginatedResponse(patients, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }



}
