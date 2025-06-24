package com.kynsoft.finamer.insis.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsoft.finamer.insis.domain.dto.ManageRoomCategoryDto;
import com.kynsoft.finamer.insis.domain.services.IManageRoomCategoryService;
import com.kynsoft.finamer.insis.infrastructure.model.ManageRoomCategory;
import com.kynsoft.finamer.insis.infrastructure.repository.command.ManageRoomCategoryWriteDataJPARepository;
import com.kynsoft.finamer.insis.infrastructure.repository.query.ManageRoomCategoryReadDataJPARepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManageRoomCategoryServiceImpl implements IManageRoomCategoryService {

    private final ManageRoomCategoryWriteDataJPARepository writeRepository;
    private final ManageRoomCategoryReadDataJPARepository readRepository;

    public ManageRoomCategoryServiceImpl(ManageRoomCategoryWriteDataJPARepository writeRepository,
                                         ManageRoomCategoryReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(ManageRoomCategoryDto dto) {
        ManageRoomCategory roomCategory = new ManageRoomCategory(dto);
        return writeRepository.save(roomCategory).getId();
    }

    @Override
    public List<ManageRoomCategoryDto> createMany(List<ManageRoomCategoryDto> roomCategoryDtos) {
        List<ManageRoomCategory> roomCategories = roomCategoryDtos.stream()
                .map(ManageRoomCategory::new)
                .toList();

        return writeRepository.saveAll(roomCategories).stream()
                .map(ManageRoomCategory::toAggregate)
                .toList();
    }

    @Override
    public void update(ManageRoomCategoryDto dto) {
        ManageRoomCategory roomCategory = new ManageRoomCategory(dto);
        writeRepository.save(roomCategory);
    }

    @Override
    public void delete(UUID id) {
        try{
            writeRepository.deleteById(id);
        }catch (Exception ex){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }

    }

    @Override
    public ManageRoomCategoryDto findById(UUID id) {
        Optional<ManageRoomCategory> roomCategory = readRepository.findById(id);
        if(roomCategory.isPresent()){
            return roomCategory.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "Room type not found.")));
    }

    @Override
    public ManageRoomCategoryDto findByCode(String code) {
        Optional<ManageRoomCategory> roomCategory = readRepository.findByCode(code);
        if(roomCategory.isPresent()){
            return roomCategory.get().toAggregate();
        }

        return null;
    }

    @Override
    public List<ManageRoomCategoryDto> findAll() {
        return readRepository.findAll().stream()
                .map(ManageRoomCategory::toAggregate)
                .toList();
    }

    @Override
    public Map<String, UUID> findIdsByCodes(List<String> codes) {
        return readRepository.findIdsByCodes(codes).stream()
                .collect(Collectors.toMap(
                        row -> (String)row[0],
                        row -> (UUID)row[1]
                ));
    }

    @Override
    public List<ManageRoomCategoryDto> findAllByCodes(List<String> codes) {
        if(Objects.nonNull(codes)){
            return readRepository.findByCodeIn(codes).stream()
                    .map(ManageRoomCategory::toAggregate)
                    .toList();
        }
        throw new IllegalArgumentException("Room Category codes must not be must");
    }
}
