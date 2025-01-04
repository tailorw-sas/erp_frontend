package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.invoicing.application.query.objectResponse.ManageRegionResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRegionDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageRegionService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageRegion;
import com.kynsoft.finamer.invoicing.infrastructure.repository.command.ManageRegionWriteDataJPARepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.query.ManageRegionReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ManageRegionServiceImpl implements IManageRegionService {

    @Autowired
    private final ManageRegionWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManageRegionReadDataJPARepository repositoryQuery;

    public ManageRegionServiceImpl(ManageRegionWriteDataJPARepository repositoryCommand, ManageRegionReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(ManageRegionDto dto) {
        ManageRegion entity = new ManageRegion(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(ManageRegionDto dto) {
        ManageRegion entity = new ManageRegion(dto);

        entity.setUpdatedAt(LocalDateTime.now());

        repositoryCommand.save(entity);
    }

    @Override
    public void delete(ManageRegionDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public ManageRegionDto findById(UUID id) {
        Optional<ManageRegion> optionalEntity = repositoryQuery.findById(id);

        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MANAGE_REGION_NOT_FOUND, new ErrorField("id", "The source not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManageRegion> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManageRegion> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    private PaginatedResponse getPaginatedResponse(Page<ManageRegion> data) {
        List<ManageRegionResponse> responseList = new ArrayList<>();
        for (ManageRegion entity : data.getContent()) {
            responseList.add(new ManageRegionResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
