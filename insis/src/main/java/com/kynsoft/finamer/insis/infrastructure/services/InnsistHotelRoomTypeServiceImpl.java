package com.kynsoft.finamer.insis.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.insis.application.query.objectResponse.innsistHotelRoomType.InnsistHotelRoomTypeResponse;
import com.kynsoft.finamer.insis.domain.dto.InnsistHotelRoomTypeDto;
import com.kynsoft.finamer.insis.domain.services.IInnsistHotelRoomTypeService;
import com.kynsoft.finamer.insis.infrastructure.model.InnsistHotelRoomType;
import com.kynsoft.finamer.insis.infrastructure.repository.command.InnsistTradingCompanyHotelWriteDataJPARepository;
import com.kynsoft.finamer.insis.infrastructure.repository.query.InnsistHotelRoomTypeReadDataJPARepository;
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
public class InnsistHotelRoomTypeServiceImpl implements IInnsistHotelRoomTypeService {

    private final InnsistTradingCompanyHotelWriteDataJPARepository writeRepository;
    private final InnsistHotelRoomTypeReadDataJPARepository readRepository;

    public InnsistHotelRoomTypeServiceImpl(InnsistTradingCompanyHotelWriteDataJPARepository writeRepository, InnsistHotelRoomTypeReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(InnsistHotelRoomTypeDto dto) {
        InnsistHotelRoomType innsistTradingCompanyHotel = new InnsistHotelRoomType(dto);
        return writeRepository.save(innsistTradingCompanyHotel).getId();
    }

    @Override
    public void update(InnsistHotelRoomTypeDto dto) {
        InnsistHotelRoomType innsistTradingCompanyHotel = new InnsistHotelRoomType(dto);
        innsistTradingCompanyHotel.setUpdatedAt(LocalDateTime.now());
        writeRepository.save(innsistTradingCompanyHotel);
    }

    @Override
    public void delete(InnsistHotelRoomTypeDto dto) {
        try {
            writeRepository.deleteById(dto.getId());
        }catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }

    }

    @Override
    public InnsistHotelRoomTypeDto findById(UUID id) {
        Optional<InnsistHotelRoomType> innsistTradingCompanyHotel = readRepository.findById(id);
        if(innsistTradingCompanyHotel.isPresent()){
            return innsistTradingCompanyHotel.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_RATE_PLAN_NOT_FOUND, new ErrorField("id", "Manage Rate Plan not found.")));
    }

    @Override
    public InnsistHotelRoomTypeDto findByHotelAndActive(UUID hotelId, String status) {
        Optional<InnsistHotelRoomType> innsistTradingCompanyHotel = readRepository.findByHotel_idAndStatus(hotelId, status);
        return innsistTradingCompanyHotel.map(InnsistHotelRoomType::toAggregate).orElse(null);
    }

    @Override
    public long countByRoomTypePrefixAndTradingCompanyId(UUID tradingCompanyId, String roomTypePrefix) {
        return readRepository.countByRoomTypePrefixAndTradingCompanyId(roomTypePrefix, tradingCompanyId);
    }

    @Override
    public List<InnsistHotelRoomTypeDto> findAll() {
        return readRepository.findAll().stream()
                .map(InnsistHotelRoomType::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<InnsistHotelRoomType> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<InnsistHotelRoomType> data = readRepository.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<InnsistHotelRoomType> data) {
        List<InnsistHotelRoomTypeResponse> userSystemsResponses = new ArrayList<>();
        for (InnsistHotelRoomType p : data.getContent()) {
            userSystemsResponses.add(new InnsistHotelRoomTypeResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
