package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageMerchantHotelEnrolleResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageMerchantHotelEnrolleDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageMerchantHotelEnrolleService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageHotel;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageMerchant;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageMerchantHotelEnrolle;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageMerchantHotelEnrolle;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ManageMerchantHotelEnrolleWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ManageMerchantHotelEnrolleReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ManageMerchantHotelEnrolleServiceImpl implements IManageMerchantHotelEnrolleService {

    @Autowired
    private final ManageMerchantHotelEnrolleWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageMerchantHotelEnrolleReadDataJPARepository repositoryQuery;

    public ManageMerchantHotelEnrolleServiceImpl(ManageMerchantHotelEnrolleWriteDataJPARepository repositoryCommand, ManageMerchantHotelEnrolleReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

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
    public ManageMerchantHotelEnrolleDto findByManageMerchantAndManageHotel(ManageMerchantDto managerMerchantDto, ManageHotelDto managerHotelDto) {
        ManageMerchant manageMerchantEntity = new ManageMerchant(managerMerchantDto);
        ManageHotel manageHotelEntity = new ManageHotel(managerHotelDto);

        Optional<ManageMerchantHotelEnrolle> manageMerchantHotelEnrolle = this.repositoryQuery.findByManageMerchantAndManageHotel(manageMerchantEntity, manageHotelEntity);
        if (manageMerchantHotelEnrolle.isPresent()){
            return manageMerchantHotelEnrolle.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "Manage Merchant Hotel Enrolle not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageMerchantHotelEnrolle> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageMerchantHotelEnrolle> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public List<ManageHotelDto> findHotelsByManageMerchant(ManageMerchantDto manageMerchant) {
        return this.repositoryQuery.findHotelsByManageMerchant(new ManageMerchant(manageMerchant)).stream().map(ManageHotel::toAggregate).collect(Collectors.toList());
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageMerchantHotelEnrolle> data) {
        List<ManageMerchantHotelEnrolleResponse> responses = new ArrayList<>();
        for (ManageMerchantHotelEnrolle p : data.getContent()) {
            responses.add(new ManageMerchantHotelEnrolleResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
