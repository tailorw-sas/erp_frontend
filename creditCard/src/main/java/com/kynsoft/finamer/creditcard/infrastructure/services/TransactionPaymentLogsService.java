package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionPaymentLogsDto;
import com.kynsoft.finamer.creditcard.domain.services.ITransactionPaymentLogsService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.TransactionPaymentLogs;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ManageTransactionsRedirectLogsWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.TransactionPaymentLogsReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionPaymentLogsService implements ITransactionPaymentLogsService {

    @Autowired
    private final ManageTransactionsRedirectLogsWriteDataJPARepository repositoryCommand;
    @Autowired
    private final TransactionPaymentLogsReadDataJPARepository repositoryQuery;

    public TransactionPaymentLogsService(ManageTransactionsRedirectLogsWriteDataJPARepository repositoryCommand, TransactionPaymentLogsReadDataJPARepository repositoryQuery){
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }
    public void update(TransactionPaymentLogsDto dto) {
        TransactionPaymentLogs entity = new TransactionPaymentLogs(dto);
        entity.setUpdatedAt(LocalDateTime.now());
        repositoryCommand.save(entity);
    }

    @Override
    public TransactionPaymentLogsDto findByTransactionId(UUID id) {
        Optional<TransactionPaymentLogs> optional = this.repositoryQuery.findByTransactionId(id);
        if (optional.isPresent()) {
            return optional.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("code", "Transaction Payment Log not found.")));
    }

}
