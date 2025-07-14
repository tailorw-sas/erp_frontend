package com.kynsoft.finamer.insis.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageRatePlan.ManageRatePlanResponse;
import com.kynsoft.finamer.insis.domain.dto.ManageRatePlanDto;
import com.kynsoft.finamer.insis.domain.services.IManageRatePlanService;
import com.kynsoft.finamer.insis.infrastructure.model.ManageRatePlan;
import com.kynsoft.finamer.insis.infrastructure.repository.command.ManageRatePlanWriteDataJPARepository;
import com.kynsoft.finamer.insis.infrastructure.repository.query.ManageRatePlanReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManageRatePlanServiceImpl implements IManageRatePlanService {

    private final ManageRatePlanWriteDataJPARepository writeRepository;
    private final ManageRatePlanReadDataJPARepository readRepository;

    public ManageRatePlanServiceImpl(ManageRatePlanWriteDataJPARepository writeRepository, ManageRatePlanReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(ManageRatePlanDto dto) {
        ManageRatePlan manageRatePlan = new ManageRatePlan(dto);
        return writeRepository.save(manageRatePlan).getId();
    }

    @Override
    public List<ManageRatePlanDto> createMany(List<ManageRatePlanDto> ratePlanDtos) {
        List<ManageRatePlan> manageRatePlans = ratePlanDtos.stream()
                .map(ManageRatePlan::new)
                .toList();

        return writeRepository.saveAll(manageRatePlans).stream()
                .map(ManageRatePlan::toAggregate)
                .toList();
    }

    @Override
    public void update(ManageRatePlanDto dto) {
        ManageRatePlan manageRatePlan = new ManageRatePlan(dto);
        manageRatePlan.setUpdatedAt(LocalDateTime.now());
        writeRepository.save(manageRatePlan);
    }

    @Override
    public void delete(ManageRatePlanDto dto) {
        try{
            writeRepository.deleteById(dto.getId());
        }catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageRatePlanDto findById(UUID id) {
        Optional<ManageRatePlan> manageRatePlanOptional = readRepository.findById(id);
        if(manageRatePlanOptional.isPresent()){
            return manageRatePlanOptional.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "Rate plan not found.")));
    }

    @Override
    public ManageRatePlanDto findByCode(String code) {
        Optional<ManageRatePlan> ratePlan = readRepository.findByCode(code);
        if(ratePlan.isPresent()){
            return  ratePlan.get().toAggregate();
        }
        return null;
    }

    @Override
    public ManageRatePlanDto findByCodeAndHotel(String code, UUID hotel) {
        Optional<ManageRatePlan> ratePlan = readRepository.findByCodeAndHotel_Id(code, hotel);
        if(ratePlan.isPresent()){
            return ratePlan.get().toAggregate();
        }
        return null;
    }

    @Override
    public List<ManageRatePlanDto> findAll() {
        return readRepository.findAll().stream()
                .map(ManageRatePlan::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, UUID> findIdsByCodes(UUID hotel, List<String> codes) {
        List<Object[]> ratePlans = readRepository.findRatePlanIdsByCodesAndHotel(codes, hotel);

        return ratePlans
                .stream()
                .collect(Collectors.toMap(
                        row -> (String)row[0],
                        row -> (UUID)row[1]
                ));
    }

    @Override
    public List<ManageRatePlanDto> findAllByCodesAndHotel(List<String> codes, UUID hotelId) {
        if(Objects.nonNull(hotelId)){
            return readRepository.findByCodeInAndHotel_Id(codes, hotelId).stream()
                    .map(ManageRatePlan::toAggregate)
                    .toList();
        }
        throw new IllegalArgumentException("Hotel Id must not be null");
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageRatePlan> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageRatePlan> data = readRepository.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageRatePlan> data) {
        List<ManageRatePlanResponse> manageRatePlansResponse = new ArrayList<>();
        for (ManageRatePlan p : data.getContent()) {
            manageRatePlansResponse.add(new ManageRatePlanResponse(p.toAggregate()));
        }
        return new PaginatedResponse(manageRatePlansResponse, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
