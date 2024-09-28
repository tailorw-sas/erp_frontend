package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagerMerchantResponse;
import com.kynsoft.finamer.settings.domain.dto.ManagerB2BPartnerDto;
import com.kynsoft.finamer.settings.domain.dto.ManagerMerchantDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManagerMerchantService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageB2BPartner;
import com.kynsoft.finamer.settings.infrastructure.identity.ManagerMerchant;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManagerMerchantWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManagerMerchantReadDataJPARepository;
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
public class ManagerMerchantServiceImpl implements IManagerMerchantService {

    @Autowired
    private ManagerMerchantWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManagerMerchantReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManagerMerchantDto dto) {
        ManagerMerchant data = new ManagerMerchant(dto);
        ManagerMerchant test = this.repositoryCommand.save(data);
        return test.getId();
    }

    @Override
    public void update(ManagerMerchantDto dto) {
        ManagerMerchant update = new ManagerMerchant(dto);
        update.setUpdateAt(LocalDateTime.now());
        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManagerMerchantDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManagerMerchantDto findById(UUID id) {
        Optional<ManagerMerchant> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_MERCHANT_NOT_FOUND, new ErrorField("id", "Manager Merchant not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManagerMerchant> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagerMerchant> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<ManagerMerchant> data) {
        List<ManagerMerchantResponse> userSystemsResponses = new ArrayList<>();
        for (ManagerMerchant p : data.getContent()) {
            userSystemsResponses.add(new ManagerMerchantResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return this.repositoryQuery.countByCodeAndNotId(code, id);
    }
    @Override
    public List<ManagerMerchantDto> findAllToReplicate() {
        return repositoryQuery.findAll().stream().map(ManagerMerchant::toAggregate).toList();
    }

}
