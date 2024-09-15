package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManagerMerchantConfigResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManagerMerchantConfigResponseDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantConfigService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManagerMerchantConfig;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ManagerMerchantConfigWriteDataJpaRepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ManagerMerchantConfigReadJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class ManagerMerchantConfigServiceImpl implements IManageMerchantConfigService {

    @Autowired
    private ManagerMerchantConfigWriteDataJpaRepository repositoryCommand;
    @Autowired
    private ManagerMerchantConfigReadJpaRepository repositoryQuery;

    @Override
    public UUID create(ManagerMerchantConfigDto dto) {
        ManagerMerchantConfig data = new ManagerMerchantConfig(dto);
        ManagerMerchantConfig merchantConfig = this.repositoryCommand.save(data);
        return merchantConfig.getId();
    }

    public void update(ManagerMerchantConfigDto dto) {
        ManagerMerchantConfig update = new ManagerMerchantConfig(dto);
        update.setUpdatedAt(LocalDateTime.now());
        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManagerMerchantConfigDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManagerMerchantConfigDto findById(UUID id) {
        Optional<ManagerMerchantConfig> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_MERCHANT_CONFIG_NOT_FOUND, new ErrorField("id", "Manager Merchant Currency not found.")));
    }

    @Override
    public ManagerMerchantConfigResponseDto findByIdWithDate(UUID id) {
        Optional<ManagerMerchantConfig> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregateWithDate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_MERCHANT_CONFIG_NOT_FOUND, new ErrorField("id", "Manager Merchant Currency not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {

        GenericSpecificationsBuilder<ManagerMerchantConfig> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagerMerchantConfig> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManagerMerchantConfig> data) {
        List<ManagerMerchantConfigResponse> userSystemsResponses = new ArrayList<>();
        for (ManagerMerchantConfig p : data.getContent()) {
            userSystemsResponses.add(new ManagerMerchantConfigResponse(p.toAggregateWithDate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(UUID managerMerchant) {
        return this.repositoryQuery.countByManagerMerchantConfig(managerMerchant);
    }

    @Override
    public Long countByManagerMerchantANDManagerCurrencyIdNotId(UUID id, UUID managerMerchant) {
        return this.repositoryQuery.countByManagerMerchantConfigNotId(id, managerMerchant);
    }
}
