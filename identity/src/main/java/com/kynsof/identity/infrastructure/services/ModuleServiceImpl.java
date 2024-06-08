package com.kynsof.identity.infrastructure.services;

import com.kynsof.identity.application.query.module.getbyid.ModuleResponse;
import com.kynsof.identity.application.query.module.search.ModuleListResponse;
import com.kynsof.identity.domain.dto.ModuleDto;
import com.kynsof.identity.domain.dto.moduleDto.ModuleDataDto;
import com.kynsof.identity.domain.dto.moduleDto.ModuleNodeDto;
import com.kynsof.identity.domain.interfaces.service.IModuleService;
import com.kynsof.identity.infrastructure.identity.ModuleSystem;
import com.kynsof.identity.infrastructure.repository.command.ModuleWriteDataJPARepository;
import com.kynsof.identity.infrastructure.repository.query.ModuleReadDataJPARepository;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ModuleServiceImpl implements IModuleService {

    @Autowired
    private ModuleWriteDataJPARepository commandRepository;

    @Autowired
    private ModuleReadDataJPARepository queryRepository;

    @Override
    public void create(ModuleDto object) {
        this.commandRepository.save(new ModuleSystem(object));
    }

    @Override
    public void update(ModuleDto object) {
        this.commandRepository.save(new ModuleSystem(object));
    }

    @Override
    public void delete(ModuleDto delete) {
        ModuleSystem moduleSystem = new ModuleSystem(delete);

        moduleSystem.setDeleted(true);
        moduleSystem.setName(delete.getName() + "-" + UUID.randomUUID());

        this.commandRepository.save(moduleSystem);
    }

    @Override
    public void deleteAll(List<UUID> modules) {
        List<ModuleSystem> delete = new ArrayList<>();
        for (UUID id : modules) {
            try {
                ModuleDto user = this.findById(id);
                ModuleSystem d = new ModuleSystem(user);
                d.setDeleted(Boolean.TRUE);
                d.setName(d.getName() + "-" + UUID.randomUUID());

                delete.add(d);
            } catch (Exception e) {
                System.err.println("Module not found!!!");
            }
        }
        this.commandRepository.saveAll(delete);
    }

    @Override
    public ModuleDto findById(UUID id) {
        Optional<ModuleSystem> object = this.queryRepository.findById(id);
        if (object.isPresent()) {
            return object.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.MODULE_NOT_FOUND, new ErrorField("id", "Module not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<ModuleResponse> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<ModuleSystem> data = this.queryRepository.findAll(specifications, pageable);
        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<ModuleSystem> data) {
        List<ModuleListResponse> moduleListResponses = new ArrayList<>();
        for (ModuleSystem o : data.getContent()) {
            moduleListResponses.add(new ModuleListResponse(o.toAggregate()));
        }
        return new PaginatedResponse(moduleListResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    public List<ModuleNodeDto> buildStructure() {
        List<ModuleSystem> modules = queryRepository.findAll();
        List<ModuleNodeDto> root = new ArrayList<>();

        for (ModuleSystem module : modules) {
            ModuleNodeDto moduleNode = new ModuleNodeDto();
            moduleNode.setKey(module.getId().toString());
            moduleNode.setData(new ModuleDataDto(module.getName(), "Module", module.getName()));

            List<ModuleNodeDto> permissionsNodes = module.getPermissions().stream().map(permission -> {
                ModuleNodeDto permissionNode = new ModuleNodeDto();
                permissionNode.setKey(permission.getId().toString());
                ModuleDataDto permissionData = new ModuleDataDto(permission.getDescription(), "Permission", permission.getCode());
                permissionNode.setData(permissionData);
                return permissionNode;
            }).collect(Collectors.toList());

            moduleNode.setChildren(permissionsNodes);

            root.add(moduleNode);
        }

        return root;
    }

    @Override
    public Long countByNameAndNotId(String name, UUID id) {
        return this.queryRepository.countByNameAndNotId(name, id);
    }

    public void updateDelete() {
        List<ModuleSystem> modules = this.queryRepository.findAll();
        for (ModuleSystem module : modules) {
            if (module.getDeleted() == null || !module.getDeleted().equals(Boolean.TRUE)) {
                module.setDeleted(Boolean.FALSE);
            }
            this.commandRepository.save(module);
        }
    }
}
