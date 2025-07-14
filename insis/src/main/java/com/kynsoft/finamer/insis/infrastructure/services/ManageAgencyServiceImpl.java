package com.kynsoft.finamer.insis.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageAgency.ManageAgencyResponse;
import com.kynsoft.finamer.insis.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.insis.domain.services.IManageAgencyService;
import com.kynsoft.finamer.insis.infrastructure.model.ManageAgency;
import com.kynsoft.finamer.insis.infrastructure.repository.command.ManageAgencyWriteDataJPARepository;
import com.kynsoft.finamer.insis.infrastructure.repository.query.ManageAgencyReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ManageAgencyServiceImpl implements IManageAgencyService {

    private final ManageAgencyWriteDataJPARepository writeRepository;
    private final ManageAgencyReadDataJPARepository readRepository;

    public ManageAgencyServiceImpl(ManageAgencyWriteDataJPARepository writeRepository, ManageAgencyReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(ManageAgencyDto dto) {
        ManageAgency agency = new ManageAgency(dto);
        return writeRepository.save(agency).getId();
    }

    @Override
    public void createMany(List<ManageAgencyDto> agencyDtos) {
       List<ManageAgency> agencies = agencyDtos.stream()
                       .map(ManageAgency::new)
                               .collect(Collectors.toList());
       writeRepository.saveAll(agencies);
    }

    @Override
    public void update(ManageAgencyDto dto) {
        ManageAgency agency = new ManageAgency(dto);
        writeRepository.save(agency);
    }

    @Override
    public void delete(ManageAgencyDto dto) {
        try{
            writeRepository.deleteById(dto.getId());
        }catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageAgencyDto findById(UUID id) {
        Optional<ManageAgency> agencyOptional = readRepository.findById(id);
        if(agencyOptional.isPresent()){
            return agencyOptional.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_AGENCY_NOT_FOUND, new ErrorField("id", "Manage Agency not found.")));
    }

    @Override
    public ManageAgencyDto findByCode(String code) {
        Optional<ManageAgency> agency = readRepository.findByCode(code);
        if(agency.isPresent()){
            return agency.get().toAggregate();
        }
        return null;
    }

    @Override
    public List<ManageAgencyDto> findAll() {
        return readRepository.findAll().stream()
                .map(ManageAgency::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ManageAgencyDto> findByIds(List<UUID> ids) {
        return readRepository.findByIdIn(ids).stream()
                .map(ManageAgency::toAggregate)
                .toList();
    }

    @Override
    public Map<String, UUID> findIdsByCodes(List<String> codes) {
        List<Object[]> result = readRepository.findAgencyIdsByCodes(codes);
        return result.stream()
                .collect(Collectors.toMap(
                        row -> (String)row[0],
                        row -> (UUID)row[1]
                ));
    }

    @Override
    public List<ManageAgencyDto> findByCodes(List<String> codes) {
        if(Objects.nonNull(codes)){
            return readRepository.findByCodeIn(codes).stream()
                    .map(ManageAgency::toAggregate)
                    .toList();
        }
        throw new IllegalArgumentException("Codes list must not be null");
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageAgency> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageAgency> data = readRepository.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageAgency> data) {
        List<ManageAgencyResponse> agenciesResponses = new ArrayList<>();
        for (ManageAgency p : data.getContent()) {
            agenciesResponses.add(new ManageAgencyResponse(p.toAggregate()));
        }
        return new PaginatedResponse(agenciesResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
