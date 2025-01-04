package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.manageBankReconciliation.ManageBankReconciliationResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageBankReconciliationDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageBankReconciliationService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageBankReconciliation;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ManageBankReconciliationWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ManageBankReconciliationReadDataJPARepository;
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
public class ManageBankReconciliationServiceImpl implements IManageBankReconciliationService {

    @Autowired
    private final ManageBankReconciliationWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageBankReconciliationReadDataJPARepository repositoryQuery;

    public ManageBankReconciliationServiceImpl(ManageBankReconciliationWriteDataJPARepository repositoryCommand, ManageBankReconciliationReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public ManageBankReconciliationDto create(ManageBankReconciliationDto dto) {
        ManageBankReconciliation bankReconciliation = new ManageBankReconciliation(dto);

        return this.repositoryCommand.save(bankReconciliation).toAggregate();
    }

    @Override
    public void update(ManageBankReconciliationDto dto) {
        ManageBankReconciliation bankReconciliation = new ManageBankReconciliation(dto);
        bankReconciliation.setUpdatedAt(LocalDateTime.now());

        this.repositoryCommand.save(bankReconciliation);
    }

    @Override
    public void delete(UUID id) {
        try {
            this.repositoryCommand.deleteById(id);
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageBankReconciliationDto findById(UUID id) {
        Optional<ManageBankReconciliation> bankReconciliation = this.repositoryQuery.findById(id);

        if (bankReconciliation.isPresent()){
            return bankReconciliation.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_BANK_RECONCILIATION_NOT_FOUND, new ErrorField("id", DomainErrorMessage.MANAGE_BANK_RECONCILIATION_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {

        GenericSpecificationsBuilder<ManageBankReconciliation> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageBankReconciliation> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public void updateDetails(UUID bankReconciliationId) {
        ManageBankReconciliationDto bankReconciliationDto = this.findById(bankReconciliationId);
        bankReconciliationDto.setDetailsAmount(
            bankReconciliationDto.getTransactions().stream().map(dto ->
                dto.isAdjustment()
                    ? dto.getTransactionSubCategory().getNegative()
                        ? -dto.getNetAmount()
                        : dto.getNetAmount()
                    : dto.getNetAmount()
            ).reduce(0.0, Double::sum));

        ManageBankReconciliation entity = new ManageBankReconciliation(bankReconciliationDto);
        entity.setUpdatedAt(LocalDateTime.now());
        this.repositoryCommand.save(entity);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageBankReconciliation> data) {
        List<ManageBankReconciliationResponse> responseList = new ArrayList<>();
        for (ManageBankReconciliation entity : data.getContent()) {
            responseList.add(new ManageBankReconciliationResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
