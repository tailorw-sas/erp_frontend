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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageMerchantCommissionDto findById(UUID id) {
        Optional<ManageMerchantCommission> commission = this.repositoryQuery.findById(id);
        if (commission.isPresent()) {
            return commission.get().toAggregate();
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
                    System.err.println("Invalid value for enum type Status: " + filter.getValue());
                }
            }
        }
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageMerchantCommission> data) {
        List<ManageMerchantCommissionResponse> responses = new ArrayList<>();
        for (ManageMerchantCommission p : data.getContent()) {
            responses.add(new ManageMerchantCommissionResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

//    @Override
//    public Long countByManagerMerchantANDManagerCreditCartType(UUID managerMerchant, UUID manageCreditCartType) {
//        return this.repositoryQuery.countByManagerMerchantANDManagerCreditCartType(managerMerchant, manageCreditCartType);
//    }
//
//    @Override
//    public Long countByManagerMerchantANDManagerCreditCartTypeIdNotId(UUID id, UUID managerMerchant, UUID manageCreditCartType) {
//        return this.repositoryQuery.countByManagerMerchantANDManagerCreditCartTypeNotId(id, managerMerchant, manageCreditCartType);
//    }


    @Override
    public List<ManageMerchantCommissionDto> findAllByMerchantAndCreditCardType(UUID managerMerchant, UUID manageCreditCartType) {
        return this.repositoryQuery.findAllByManagerMerchantAndManageCreditCartType(managerMerchant, manageCreditCartType)
                .stream()
                .map(ManageMerchantCommission::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ManageMerchantCommissionDto> findAllByMerchantAndCreditCardTypeById(UUID managerMerchant, UUID manageCreditCartType, UUID id) {
        return this.repositoryQuery.findAllByManagerMerchantAndManageCreditCartTypeById(id, managerMerchant, manageCreditCartType)
                .stream()
                .map(ManageMerchantCommission::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkDateOverlapForSameCombination(UUID managerMerchant, UUID manageCreditCartType, Double commission, String calculationType, LocalDate fromDate, LocalDate toDate) {
        List<ManageMerchantCommissionDto> existingCommissions = findAllByMerchantAndCreditCardType(managerMerchant, manageCreditCartType);
        for (ManageMerchantCommissionDto existing : existingCommissions) {
            if (existing.getCommission().equals(commission) && existing.getCalculationType().equals(calculationType)) {
                if (isOverlapping(existing.getFromDate(), existing.getToDate(), fromDate, toDate)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean checkDateOverlapForDifferentCombination(UUID managerMerchant, UUID manageCreditCartType, LocalDate fromDate, LocalDate toDate) {
        List<ManageMerchantCommissionDto> existingCommissions = findAllByMerchantAndCreditCardType(managerMerchant, manageCreditCartType);
        for (ManageMerchantCommissionDto existing : existingCommissions) {
            if (isOverlapping(existing.getFromDate(), existing.getToDate(), fromDate, toDate)) {
                return true;
            }
        }
        return false;
    }

//    @Override
//    public int countOverlappingRecords(UUID managerMerchant, UUID manageCreditCartType, LocalDate fromDate, LocalDate toDate) {
//        return 0;
//    }

    private boolean isOverlapping(LocalDate existingFromDate, LocalDate existingToDate, LocalDate newFromDate, LocalDate newToDate) {
        return !newFromDate.isAfter(existingToDate) && !newToDate.isBefore(existingFromDate);
    }


    public boolean hasOverlappingRecords(UUID id, UUID managerMerchant, UUID manageCreditCartType, LocalDate fromDate, LocalDate toDate, Double commission, String calculationType) {
        Long count = repositoryQuery.countOverlappingRecords(id, managerMerchant, manageCreditCartType, commission, calculationType, fromDate, toDate);
        return count > 0;
    }
}