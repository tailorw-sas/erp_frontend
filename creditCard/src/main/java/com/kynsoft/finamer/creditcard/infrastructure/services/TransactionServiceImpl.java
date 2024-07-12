package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.TransactionResponse;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.MethodType;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.Transaction;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.TransactionWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.TransactionReadDataJPARepository;
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
public class TransactionServiceImpl implements ITransactionService {
    
    @Autowired
    private final TransactionWriteDataJPARepository repositoryCommand;
    
    @Autowired
    private final TransactionReadDataJPARepository repositoryQuery;

    public TransactionServiceImpl(TransactionWriteDataJPARepository repositoryCommand, TransactionReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public Long create(TransactionDto dto) {
        Transaction entity = new Transaction(dto);
        return this.repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(TransactionDto dto) {
        Transaction entity = new Transaction(dto);
        entity.setUpdateAt(LocalDateTime.now());
        
        this.repositoryCommand.save(entity);
    }

    @Override
    public void delete(TransactionDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public TransactionDto findById(Long id) {
        Optional<Transaction> entity = this.repositoryQuery.findById(id);
        if (entity.isPresent()) {
            return entity.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.VCC_TRANSACTION_NOT_FOUND, new ErrorField("id", DomainErrorMessage.VCC_TRANSACTION_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<Transaction> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Transaction> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public Long countByReservationNumberAndManageHotelIdAndNotId(String reservationNumber, UUID hotel) {
        return this.repositoryQuery.countByReservationNumberAndManageHotelIdAndNotId(reservationNumber, hotel);
    }

    @Override
    public boolean compareParentTransactionAmount(Long parentId, Double amount) {
        Optional<Transaction> parentTransaction = repositoryQuery.findById(parentId);
        if (parentTransaction.isPresent()) {
            double parentAmount = parentTransaction.get().getAmount();
            Optional<Double> sumOfChildrenAmount = repositoryQuery.findSumOfAmountByParentId(parentId);
            return (sumOfChildrenAmount.orElse(0.0) + amount) > parentAmount;
        } else {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.VCC_TRANSACTION_NOT_FOUND, new ErrorField("id", DomainErrorMessage.VCC_TRANSACTION_NOT_FOUND.getReasonPhrase())));
        }
    }

    private void filterCriteria(List<FilterCriteria> filterCriteria) {
        for (FilterCriteria filter : filterCriteria) {

            if ("methodType".equals(filter.getKey()) && filter.getValue() instanceof String) {
                try {
                    MethodType enumValue = MethodType.valueOf((String) filter.getValue());
                    filter.setValue(enumValue);
                } catch (IllegalArgumentException e) {
                    System.err.println("Valor inválido para el tipo Enum MethodType: " + filter.getValue());
                }
            }
        }
    }

    private PaginatedResponse getPaginatedResponse(Page<Transaction> data) {
        List<TransactionResponse> responseList = new ArrayList<>();
        for (Transaction entity : data.getContent()) {
            responseList.add(new TransactionResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
