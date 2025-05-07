package com.kynsoft.finamer.insis.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageHotel.ManageHotelResponse;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.services.IManageHotelService;
import com.kynsoft.finamer.insis.infrastructure.model.ManageHotel;
import com.kynsoft.finamer.insis.infrastructure.repository.command.ManageHotelWriteDataJPARepository;
import com.kynsoft.finamer.insis.infrastructure.repository.query.ManageHotelReadDataJPARepository;
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

    private final ManageHotelWriteDataJPARepository writeRepository;
    private final ManageHotelReadDataJPARepository readRepository;

    public ManageHotelServiceImpl(ManageHotelWriteDataJPARepository writeRepository, ManageHotelReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(ManageHotelDto dto) {
        ManageHotel hotel = new ManageHotel(dto);
        return writeRepository.save(hotel).getId();
    }

    @Override
    public void update(ManageHotelDto dto) {
        ManageHotel hotel = new ManageHotel(dto);
        hotel.setUpdatedAt(LocalDateTime.now());
        writeRepository.save(hotel);
    }

    @Override
    public void delete(ManageHotelDto dto) {
        try{
            writeRepository.deleteById(dto.getId());
        }catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageHotelDto findById(UUID id) {
        Optional<ManageHotel> manageHotel = readRepository.findById(id);
        if(manageHotel.isPresent()){
            return manageHotel.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "Hotel not found.")));
    }

    @Override
    public ManageHotelDto findByCode(String code) {
        Optional<ManageHotel> hotel = readRepository.findByCode(code);
        return hotel.map(ManageHotel::toAggregate).orElse(null);

    }

    @Override
    public ManageHotelDto findByTradingCompany(UUID id) {
        Optional<ManageHotel> hotel = readRepository.findByManageTradingCompany_id(id);
        return hotel.map(ManageHotel::toAggregate).orElse(null);
    }

    @Override
    public List<ManageHotelDto> findAll() {
        return readRepository.findAll().stream()
                .map(ManageHotel::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ManageHotelDto> findByIds(List<UUID> ids) {
        return readRepository.findByIdIn(ids).stream()
                .map(ManageHotel::toAggregate)
                .toList();
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageHotel> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageHotel> data = readRepository.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageHotel> data) {
        List<ManageHotelResponse> manageHotelResponse = new ArrayList<>();
        for (ManageHotel p : data.getContent()) {
            manageHotelResponse.add(new ManageHotelResponse(p.toAggregate()));
        }
        return new PaginatedResponse(manageHotelResponse, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
