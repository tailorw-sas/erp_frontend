package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.TransactionStatusHistory.TransactionStatusHistoryResponse;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionStatusHistoryDto;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionStatusHistoryService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.TransactionStatusHistory;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.TransactionsStatusHistoryWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.TransactionStatusHistoryReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionStatusHistoryServiceImpl implements ITransactionStatusHistoryService {

    @Autowired
    private final TransactionsStatusHistoryWriteDataJPARepository repositoryCommand;

    @Autowired
    private final TransactionStatusHistoryReadDataJPARepository repositoryQuery;

    public TransactionStatusHistoryServiceImpl(TransactionsStatusHistoryWriteDataJPARepository repositoryCommand, TransactionStatusHistoryReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public TransactionStatusHistoryDto create(TransactionStatusHistoryDto dto) {
        TransactionStatusHistory transactionStatusHistory = new TransactionStatusHistory(dto);
        return this.repositoryCommand.save(transactionStatusHistory).toAggregate();
    }

    @Override
    public void update(TransactionStatusHistoryDto dto) {
        TransactionStatusHistory transactionStatusHistory = new TransactionStatusHistory(dto);
        transactionStatusHistory.setUpdatedAt(LocalDateTime.now());
        this.repositoryCommand.save(transactionStatusHistory);
    }

    @Override
    public void delete(UUID id) {
        try {
            this.repositoryCommand.deleteById(id);
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public TransactionStatusHistoryDto findById(UUID id) {
        return this.repositoryQuery.findById(id)
                .map(TransactionStatusHistory::toAggregate)
                .orElseThrow(()->{
                    throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", DomainErrorMessage.NOT_FOUND.getReasonPhrase())));
            }
        );
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<TransactionStatusHistory> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<TransactionStatusHistory> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<TransactionStatusHistory> data) {
        List<TransactionStatusHistoryResponse> responses = new ArrayList<>();
        for (TransactionStatusHistory p : data.getContent()) {
            responses.add(new TransactionStatusHistoryResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
