package com.tailorw.tcaInnsist.infrastructure.service;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.tailorw.tcaInnsist.domain.dto.ManageHotelDto;
import com.tailorw.tcaInnsist.domain.services.IManageHotelService;
import com.tailorw.tcaInnsist.infrastructure.model.ManageHotel;
import com.tailorw.tcaInnsist.infrastructure.repository.command.ManageHotelWriteDataJPARepository;
import com.tailorw.tcaInnsist.infrastructure.repository.query.ManageHotelReadDataJPARepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ManageHotelServiceImpl implements IManageHotelService {

    private final ManageHotelWriteDataJPARepository writeRepository;
    private final ManageHotelReadDataJPARepository readRepository;

    @Override
    public UUID create(ManageHotelDto manageHotelDto) {
        ManageHotel manageHotel = new ManageHotel(manageHotelDto);
        return writeRepository.save(manageHotel).toAggregate().getId();
    }

    @Override
    public void update(ManageHotelDto manageHotelDto) {
        ManageHotel hotel = new ManageHotel(manageHotelDto);
        writeRepository.save(hotel);
    }

    @Override
    public void delete(UUID id) {
        try{
            writeRepository.deleteById(id);
        }catch (Exception ex){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("ManageHotel_id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageHotelDto getByCode(String code) {
        return readRepository.findByCode(code).map(ManageHotel::toAggregate)
                .orElse(null);
    }

    @Override
    public List<ManageHotelDto> getAll() {
        List<ManageHotelDto> result = new ArrayList<>();
        for(ManageHotel hotel : readRepository.findAll()){
            result.add(hotel.toAggregate());
        }
        return result;
    }

    @Override
    public boolean existsHotels() {
        return readRepository.count() > 0;
    }

    @Override
    public void createMany(List<ManageHotelDto> hotels) {
        try{
            writeRepository.deleteAll();
            List<ManageHotel> manageHotelList = hotels.stream()
                    .map(ManageHotel::new)
                    .toList();

            writeRepository.saveAll(manageHotelList);
        }catch (Exception ex){
            Logger.getLogger(ManageHotelServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public ManageHotelDto getById(UUID id) {
        return readRepository.findById(id)
                .map(ManageHotel::toAggregate)
                .orElse(null);
    }
}
