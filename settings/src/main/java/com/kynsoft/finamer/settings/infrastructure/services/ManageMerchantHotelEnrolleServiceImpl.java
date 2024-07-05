package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageMerchantHotelEnrolleResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageMerchantHotelEnrolleDto;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.services.IManageMerchantHotelEnrolleService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageMerchantHotelEnrolle;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManageMerchantHotelEnrolleWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManagerMerchantHotelEnrolleReadDataJPARepository;
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
public class ManageMerchantHotelEnrolleServiceImpl implements IManageMerchantHotelEnrolleService {

    @Autowired
    private ManageMerchantHotelEnrolleWriteDataJPARepository repositoryCommand;

    @Autowired
    private ManagerMerchantHotelEnrolleReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(ManageMerchantHotelEnrolleDto dto) {
        ManageMerchantHotelEnrolle data = new ManageMerchantHotelEnrolle(dto);
        this.repositoryCommand.save(data);
        return data.getId();
    }

    @Override
    public void update(ManageMerchantHotelEnrolleDto dto) {
        ManageMerchantHotelEnrolle update = new ManageMerchantHotelEnrolle(dto);
        update.setUpdateAt(LocalDateTime.now());
        this.repositoryCommand.save(update);
    }

    @Override
    public void delete(ManageMerchantHotelEnrolleDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageMerchantHotelEnrolleDto findById(UUID id) {
        Optional<ManageMerchantHotelEnrolle> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "Manage Merchant Hotel Enrolle not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        filterCriteria(filterCriteria);
        GenericSpecificationsBuilder<ManageMerchantHotelEnrolle> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageMerchantHotelEnrolle> data = this.repositoryQuery.findAll(specifications, pageable);

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

    private PaginatedResponse getPaginatedResponse(Page<ManageMerchantHotelEnrolle> data) {
        List<ManageMerchantHotelEnrolleResponse> userSystemsResponses = new ArrayList<>();
        for (ManageMerchantHotelEnrolle p : data.getContent()) {
            userSystemsResponses.add(new ManageMerchantHotelEnrolleResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByManageMerchantAndManageCurrencyAndManageHotelAndEnrolleIdNotId(UUID id, UUID managerMerchant, UUID managerCurrency, UUID managerHotel, String enrrolle) {
        return this.repositoryQuery.countByManageMerchantAndManageCurrencyAndManageHotelAndEnrolleIdNotId(id, managerMerchant, managerCurrency, managerHotel, enrrolle);
    }

}
