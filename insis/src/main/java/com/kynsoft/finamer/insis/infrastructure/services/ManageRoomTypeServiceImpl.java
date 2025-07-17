package com.kynsoft.finamer.insis.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageRoomType.ManageRoomTypeResponse;
import com.kynsoft.finamer.insis.domain.dto.ManageRoomTypeDto;
import com.kynsoft.finamer.insis.domain.services.IManageRoomTypeService;
import com.kynsoft.finamer.insis.infrastructure.model.ManageRoomType;
import com.kynsoft.finamer.insis.infrastructure.repository.command.ManageRoomTypeWriteDataJPARepository;
import com.kynsoft.finamer.insis.infrastructure.repository.query.ManageRoomTypeReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManageRoomTypeServiceImpl implements IManageRoomTypeService {

    private final ManageRoomTypeWriteDataJPARepository writeRepository;
    private final ManageRoomTypeReadDataJPARepository readRepository;

    public ManageRoomTypeServiceImpl(ManageRoomTypeWriteDataJPARepository writeRepository, ManageRoomTypeReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(ManageRoomTypeDto dto) {
        ManageRoomType roomType = new ManageRoomType(dto);
        return writeRepository.save(roomType).getId();
    }

    @Override
    public List<ManageRoomTypeDto> createMany(List<ManageRoomTypeDto> roomTypeDtos) {
        List<ManageRoomType> roomTypes = roomTypeDtos.stream()
                .map(ManageRoomType::new)
                .collect(Collectors.toList());

        return writeRepository.saveAll(roomTypes).stream()
                .map(ManageRoomType::toAggregate)
                .toList();
    }

    @Override
    public void update(ManageRoomTypeDto dto) {
        ManageRoomType roomType = new ManageRoomType(dto);
        roomType.setUpdatedAt(LocalDateTime.now());
        writeRepository.save(roomType);
    }

    @Override
    public void delete(ManageRoomTypeDto dto) {
        try{
            writeRepository.deleteById(dto.getId());
        }catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageRoomTypeDto findById(UUID id) {
        Optional<ManageRoomType> roomTypeOptional = readRepository.findById(id);
        if(roomTypeOptional.isPresent()){
            return roomTypeOptional.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "Room type not found.")));
    }

    @Override
    public ManageRoomTypeDto findByCode(String code) {
        return readRepository.findByCode(code)
                .map(ManageRoomType::toAggregate)
                .orElse(null);
    }

    @Override
    public ManageRoomTypeDto findByCodeAndHotel(String code, UUID hotel) {
        Optional<ManageRoomType> roomType = readRepository.findByCodeAndHotel_Id(code, hotel);
        return roomType.
                map(ManageRoomType::toAggregate)
                .orElse(null);
    }

    @Override
    public List<ManageRoomTypeDto> findAll() {
        return readRepository.findAll().stream()
                .map(ManageRoomType::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, UUID> findIdsByCodes(List<String> codes, UUID hotelCode) {
        return readRepository.findRoomTypeIdsByCodesAndHotel(codes, hotelCode)
                .stream()
                .collect(Collectors.toMap(
                        row -> (String)row[0],
                        row -> (UUID)row[1]
                ));
    }

    @Override
    public List<ManageRoomTypeDto> findAllByCodesAndHotel(List<String> codes, UUID hotelId) {
        if(Objects.nonNull(hotelId)){
            return readRepository.findByCodeInAndHotel_Id(codes, hotelId).stream()
                    .map(ManageRoomType::toAggregate)
                    .toList();
        }
        throw new IllegalArgumentException("Hotel Id must not be null");
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageRoomType> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageRoomType> data = readRepository.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageRoomType> data) {
        List<ManageRoomTypeResponse> manageRoomTypesResponse = new ArrayList<>();
        for (ManageRoomType p : data.getContent()) {
            manageRoomTypesResponse.add(new ManageRoomTypeResponse(p.toAggregate()));
        }
        return new PaginatedResponse(manageRoomTypesResponse, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
