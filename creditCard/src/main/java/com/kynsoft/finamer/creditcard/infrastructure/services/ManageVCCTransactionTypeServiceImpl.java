package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageVCCTransactionTypeResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageVCCTransactionTypeDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageVCCTransactionTypeService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageVCCTransactionType;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ManageVCCTransactionTypeWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ManageVCCTransactionTypeReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ManageVCCTransactionTypeServiceImpl implements IManageVCCTransactionTypeService {

    @Autowired
    private ManageVCCTransactionTypeReadDataJPARepository repositoryQuery;

    @Autowired
    private ManageVCCTransactionTypeWriteDataJPARepository repositoryCommand;

    @Override
    public UUID create(ManageVCCTransactionTypeDto dto) {
        ManageVCCTransactionType entity = new ManageVCCTransactionType(dto);
        ManageVCCTransactionType saved = repositoryCommand.save(entity);

        return saved.getId();
    }

    @Override
    public void update(ManageVCCTransactionTypeDto dto) {
        repositoryCommand.save(new ManageVCCTransactionType(dto));
    }

    @Override
    public void delete(ManageVCCTransactionTypeDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageVCCTransactionTypeDto findById(UUID id) {
        Optional<ManageVCCTransactionType> optionalEntity = repositoryQuery.findById(id);
        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.VCC_MANAGE_TRANSACTION_TYPE_NOT_FOUND, new ErrorField("id", DomainErrorMessage.VCC_MANAGE_TRANSACTION_TYPE_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public List<ManageVCCTransactionTypeDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageVCCTransactionType::toAggregate).toList();
    }

    @Override
    public List<ManageVCCTransactionTypeDto> findAll() {
        return repositoryQuery.findAll().stream().map(ManageVCCTransactionType::toAggregate).collect(Collectors.toList());
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageVCCTransactionType> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageVCCTransactionType> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public ManageVCCTransactionTypeDto findByCode(String code) {
        Optional<ManageVCCTransactionType> userSystem = this.repositoryQuery.findByCode(code);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("code", "Manage Transaction Type not found.")));
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageVCCTransactionType> data) {
        List<ManageVCCTransactionTypeResponse> responses = new ArrayList<>();
        for (ManageVCCTransactionType p : data.getContent()) {
            responses.add(new ManageVCCTransactionTypeResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
