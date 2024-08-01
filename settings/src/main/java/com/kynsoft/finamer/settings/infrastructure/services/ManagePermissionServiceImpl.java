package com.kynsoft.finamer.settings.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManagePermissionResponse;
import com.kynsoft.finamer.settings.domain.dto.PermissionDto;
import com.kynsoft.finamer.settings.domain.services.IManagePermissionService;
import com.kynsoft.finamer.settings.infrastructure.identity.ManagePermission;
import com.kynsoft.finamer.settings.infrastructure.repository.command.ManagePermissionWriteDataJPARepository;
import com.kynsoft.finamer.settings.infrastructure.repository.query.ManagePermissionReadDataJPARepository;
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
public class ManagePermissionServiceImpl implements IManagePermissionService {

    @Autowired
    private final ManagePermissionWriteDataJPARepository repositoryCommand;

    @Autowired
    private final ManagePermissionReadDataJPARepository repositoryQuery;

    public ManagePermissionServiceImpl(ManagePermissionWriteDataJPARepository repositoryCommand, ManagePermissionReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }

    @Override
    public UUID create(PermissionDto dto) {
        ManagePermission entity = new ManagePermission(dto);
        return repositoryCommand.save(entity).getId();
    }

    @Override
    public void update(PermissionDto dto) {
        ManagePermission entity = new ManagePermission(dto);


        repositoryCommand.save(entity);
    }

    @Override
    public void delete(PermissionDto dto) {
        try {
            this.repositoryCommand.deleteById(dto.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public PermissionDto findById(UUID id) {
        Optional<ManagePermission> optionalEntity = repositoryQuery.findById(id);

        if(optionalEntity.isPresent()){
            return optionalEntity.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND, new ErrorField("id", "Manage Permission not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ManagePermission> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ManagePermission> data = repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return repositoryQuery.countByCodeAndNotId(code, id);
    }

    @Override
    public List<PermissionDto> findByIds(List<UUID> ids) {
        return repositoryQuery.findAllById(ids).stream().map(ManagePermission::toAggregate).toList();
    }

    private PaginatedResponse getPaginatedResponse(Page<ManagePermission> data) {
        List<ManagePermissionResponse> responseList = new ArrayList<>();
        for (ManagePermission entity : data.getContent()) {
            responseList.add(new ManagePermissionResponse(entity.toAggregate()));
        }
        return new PaginatedResponse(responseList, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
