package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageMerchantCommissionResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantCommissionDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantCommissionService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageMerchantCommission;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageMerchantCommissionWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManageMerchantCommissionReadDataJPARepository;
import java.time.LocalDate;
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
public class ManageMerchantCommissionServiceImpl implements IManageMerchantCommissionService {

    @Autowired
    private ManageMerchantCommissionWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManageMerchantCommissionReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManageMerchantCommissionDto dto) {
        ManageMerchantCommission data = new ManageMerchantCommission(dto);
        return this.repositoryCommand.save(data).getId();
    }

    @Override
    public void update(ManageMerchantCommissionDto dto) {
        ManageMerchantCommission update = new ManageMerchantCommission(dto);

        update.setUpdateAt(LocalDateTime.now());

        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManageMerchantCommissionDto dto) {
        ManageMerchantCommission delete = new ManageMerchantCommission(dto);

        delete.setDeleted(Boolean.TRUE);
        delete.setDeleteAt(LocalDateTime.now());

        this.repositoryCommand.save(delete);
    }

    @Override
    public ManageMerchantCommissionDto findById(UUID id) {
        Optional<ManageMerchantCommission> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_MERCHANT_COMMISSION_NOT_FOUND, new ErrorField("id", "Manage Merchant Commission not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);

        GenericSpecificationsBuilder<ManageMerchantCommission> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageMerchantCommission> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<ManageMerchantCommission> data) {
        List<ManageMerchantCommissionResponse> userSystemsResponses = new ArrayList<>();
        for (ManageMerchantCommission p : data.getContent()) {
            userSystemsResponses.add(new ManageMerchantCommissionResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByManagerMerchantANDManagerCreditCartType(UUID managerMerchant, UUID manageCreditCartType) {
        return this.repositoryQuery.countByManagerMerchantANDManagerCreditCartType(managerMerchant, manageCreditCartType);
    }

    @Override
    public Long countByManagerMerchantANDManagerCreditCartTypeIdNotId(UUID id, UUID managerMerchant, UUID manageCreditCartType) {
        return this.repositoryQuery.countByManagerMerchantANDManagerCurrencyIdNotId(id, managerMerchant, manageCreditCartType);
    }

    @Override
    public Long countByManagerMerchantANDManagerCreditCartTypeANDDateRange(UUID id, UUID managerMerchant, UUID manageCreditCartType, LocalDate fromCheckDate, LocalDate toCheckDate) {
        return this.repositoryQuery.countByManagerMerchantANDManagerCreditCartTypeANDDateRange(id, managerMerchant, manageCreditCartType, fromCheckDate, toCheckDate);
    }

}
