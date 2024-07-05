package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.ManageHotelResponse;
import com.kynsoft.finamer.creditcard.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.creditcard.domain.services.IManageHotelService;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageHotel;
import com.kynsoft.finamer.creditcard.infrastructure.identity.ManageHotel;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.ManageHotelWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.ManageHotelReadDataJPARepository;
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
public class ManageHotelServiceImpl implements IManageHotelService {

    @Autowired
    private final ManageHotelWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageHotelReadDataJPARepository repositoryQuery;

    public ManageHotelServiceImpl(ManageHotelWriteDataJPARepository repositoryCommand, ManageHotelReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageHotelDto dto) {
        ManageHotel entity = new ManageHotel(dto);

        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManageHotelDto dto) {
        ManageHotel entity = new ManageHotel(dto);

        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageHotelDto dto) {
        try{
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageHotelDto findById(UUID id) {
        Optional<ManageHotel> optionalEntity = repositoryQuery.findById(id);

        if(optionalEntity.isPresent()){
            if(optionalEntity.get().getIsApplyByVcc()){
                return optionalEntity.get().toAggregate();
            }
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_HOTEL_NOT_FOUND, new ErrorField("id", "Manage Hotel not found.")));
    }

    @Override
    public List<ManageHotelDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManageHotel::toAggregate).toList();
    }

    @Override
    public List<ManageHotelDto> findAll() {
        return repositoryQuery.findAll().stream().map(ManageHotel::toAggregate).collect(Collectors.toList());
    }

    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageHotel> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageHotel> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageHotel> data) {
        List<ManageHotelResponse> responseList = new ArrayList<>();
        for (ManageHotel entity : data.getContent()) {
            responseList.add(new ManageHotelResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

}
