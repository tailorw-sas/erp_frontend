package com.kynsoft.finamer.insis.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.insis.application.query.objectResponse.manageB2BPartnerType.ManageB2BPartnerTypeResponse;
import com.kynsoft.finamer.insis.domain.dto.ManageB2BPartnerTypeDto;
import com.kynsoft.finamer.insis.domain.services.IManageB2BPartnerTypeService;
import com.kynsoft.finamer.insis.infrastructure.model.ManageB2BPartnerType;
import com.kynsoft.finamer.insis.infrastructure.repository.command.ManageB2BPartnerTypeWriteDataJPARepository;
import com.kynsoft.finamer.insis.infrastructure.repository.query.ManageB2BPartnerTypeReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ManageB2BPartnerTypeServiceImpl implements IManageB2BPartnerTypeService {

    private final ManageB2BPartnerTypeWriteDataJPARepository writeRepository;
    private final ManageB2BPartnerTypeReadDataJPARepository readRepository;

    public ManageB2BPartnerTypeServiceImpl(ManageB2BPartnerTypeWriteDataJPARepository writeRepository, ManageB2BPartnerTypeReadDataJPARepository readRepository){
        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    public UUID create(ManageB2BPartnerTypeDto dto) {
        ManageB2BPartnerType manageB2BPartnerType = new ManageB2BPartnerType(dto);
        return writeRepository.save(manageB2BPartnerType).getId();
    }

    @Override
    public void update(ManageB2BPartnerTypeDto dto) {
        ManageB2BPartnerType manageB2BPartnerType = new ManageB2BPartnerType(dto);
        writeRepository.save(manageB2BPartnerType);
    }

    @Override
    public void delete(ManageB2BPartnerTypeDto dto) {
        try{
            writeRepository.deleteById(dto.getId());
        }catch (Exception e){
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageB2BPartnerTypeDto findById(UUID id) {
        Optional<ManageB2BPartnerType> manageB2BPartnerTypeOptional = readRepository.findById(id);
        if(manageB2BPartnerTypeOptional.isPresent()){
            return manageB2BPartnerTypeOptional.get().toAgrregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGER_B2BPARTNER_NOT_FOUND, new ErrorField("id", "Manage B2BPartner not found.")));
    }

    @Override
    public List<ManageB2BPartnerTypeDto> findAll() {
        return readRepository.findAll().stream()
                .map(ManageB2BPartnerType::toAgrregate)
                .collect(Collectors.toList());
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageB2BPartnerType> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageB2BPartnerType> data = readRepository.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageB2BPartnerType> data) {
        List<ManageB2BPartnerTypeResponse> manageB2BPartnerTypesResponse = new ArrayList<>();
        for (ManageB2BPartnerType p : data.getContent()) {
            manageB2BPartnerTypesResponse.add(new ManageB2BPartnerTypeResponse(p.toAgrregate()));
        }
        return new PaginatedResponse(manageB2BPartnerTypesResponse, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
