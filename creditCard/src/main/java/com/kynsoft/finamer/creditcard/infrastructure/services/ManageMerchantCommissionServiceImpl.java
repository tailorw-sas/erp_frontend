package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageMerchantCommissionResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantCommissionDto;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.CalculationType;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantCommissionService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageMerchantCommission;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ManageMerchantCommissionWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ManageMerchantCommissionReadDataJPARepository;
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
    private final ManageMerchantCommissionWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageMerchantCommissionReadDataJPARepository repositoryQuery;

    public ManageMerchantCommissionServiceImpl(ManageMerchantCommissionWriteDataJPARepository repositoryCommand, ManageMerchantCommissionReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

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
        delete.setDeleted(true);
        delete.setDeletedAt(LocalDateTime.now());
        this.repositoryCommand.save(delete);
    }

    @Override
    public ManageMerchantCommissionDto findById(UUID id) {
        Optional<ManageMerchantCommission> commission = this.repositoryQuery.findById(id);
        if (commission.isPresent()) {
            return commission.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.VCC_MANAGE_MERCHANT_COMMISSION_NOT_FOUND, new ErrorField("id", DomainErrorMessage.VCC_MANAGE_MERCHANT_COMMISSION_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageMerchantCommission> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageMerchantCommission> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageMerchantCommission> data) {
        List<ManageMerchantCommissionResponse> responses = new ArrayList<>();
        for (ManageMerchantCommission p : data.getContent()) {
            responses.add(new ManageMerchantCommissionResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public List<ManageMerchantCommissionDto> findAllByMerchantAndCreditCardType(UUID managerMerchant, UUID manageCreditCartType) {
        return this.repositoryQuery.findAllByManagerMerchantAndManageCreditCartType(managerMerchant, manageCreditCartType)
                .stream()
                .map(ManageMerchantCommission::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public ManageMerchantCommissionDto findByManagerMerchantAndManageCreditCartTypeAndDateWithinRangeOrNoEndDate(UUID managerMerchant, UUID manageCreditCartType, LocalDate date) {
        Optional<ManageMerchantCommission> commission = this.repositoryQuery.findByManagerMerchantAndManageCreditCartTypeAndDateWithinRangeOrNoEndDate(
                managerMerchant, manageCreditCartType, date
        );
        if (commission.isPresent()) {
            return commission.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.VCC_MANAGE_MERCHANT_COMMISSION_NOT_FOUND, new ErrorField("id", DomainErrorMessage.VCC_MANAGE_MERCHANT_COMMISSION_NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public Double calculateCommission(double amount, UUID merchantId, UUID creditCardTypeId) {
        List<ManageMerchantCommissionDto> merchantCommissionDtoList = findAllByMerchantAndCreditCardType(merchantId, creditCardTypeId);
        double commission = 0;
        if (!merchantCommissionDtoList.isEmpty()) {
            ManageMerchantCommissionDto first = merchantCommissionDtoList.get(0);
            if (first.getCalculationType() == CalculationType.PER) {
                commission = (first.getCommission() / 100.0) * amount;
                // Aplicar redondeo de banquero con dos decimales por ahora, despues la cantidad de decimales se toma de la config
                commission = BankerRounding.round(commission, 2);
            } else {
                commission = first.getCommission();
            }
        }
        return commission;
    }

}