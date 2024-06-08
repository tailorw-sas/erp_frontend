package com.kynsof.identity.application.command.userPermisionBusiness.update;

import com.kynsof.identity.domain.dto.BusinessDto;
import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.identity.domain.dto.UserPermissionBusinessDto;
import com.kynsof.identity.domain.dto.UserSystemDto;
import com.kynsof.identity.domain.interfaces.service.IBusinessService;
import com.kynsof.identity.domain.interfaces.service.IPermissionService;
import com.kynsof.identity.domain.interfaces.service.IUserPermissionBusinessService;
import com.kynsof.identity.domain.interfaces.service.IUserSystemService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UpdateUserPermissionBusinessCommandHandler implements ICommandHandler<UpdateUserPermissionBusinessCommand> {

    private final IUserPermissionBusinessService service;

    private final IPermissionService permissionService;

    private final IBusinessService businessService;

    private final IUserSystemService userSystemService;

    public UpdateUserPermissionBusinessCommandHandler(IUserPermissionBusinessService service,
                                                      IPermissionService permissionService,
                                                      IBusinessService businessService,
                                                      IUserSystemService userSystemService) {
        this.service = service;
        this.permissionService = permissionService;
        this.businessService = businessService;
        this.userSystemService = userSystemService;
    }

    @Override
    public void handle(UpdateUserPermissionBusinessCommand command) {
        UserRoleBusinessUpdateRequest updateRequest = command.getPayload();
        // Validar y recuperar las entidades involucradas
        UserSystemDto userSystemDto = userSystemService.findById(updateRequest.getUserId());
        BusinessDto businessDto = businessService.findById(updateRequest.getBusinessId());

        // Recuperar el estado actual
        List<UserPermissionBusinessDto> currentPermissions = service.findByUserAndBusiness(userSystemDto.getId(),
                        businessDto.getId())
                .stream()
                .toList();

        Set<UUID> newPermissionIds = new HashSet<>(updateRequest.getPermissionIds());
        Set<UUID> currentPermissionIds = currentPermissions.stream()
                .map(permission -> permission.getPermission().getId())
                .collect(Collectors.toSet());

        // Determinar cambios necesarios
        Set<UUID> permissionsToAdd = new HashSet<>(newPermissionIds);
        permissionsToAdd.removeAll(currentPermissionIds);

        Set<UUID> permissionsToRemove = new HashSet<>(currentPermissionIds);
        permissionsToRemove.removeAll(newPermissionIds);
        List<UserPermissionBusinessDto> addPermissionUserBusinessList = new ArrayList<>();
        List<UserPermissionBusinessDto> deletePermissionUserBusinessList = new ArrayList<>();
        // Ejecutar cambios
        for (UUID permissionIdToAdd : permissionsToAdd) {
            PermissionDto permissionDto = permissionService.findById(permissionIdToAdd);
            UserPermissionBusinessDto newUserPermissionBusiness = new UserPermissionBusinessDto();
            newUserPermissionBusiness.setId(UUID.randomUUID());
            newUserPermissionBusiness.setUser(userSystemDto);
            newUserPermissionBusiness.setPermission(permissionDto);
            newUserPermissionBusiness.setBusiness(businessDto);
            addPermissionUserBusinessList.add(newUserPermissionBusiness);
        }

        for (UUID permissionIdToRemove : permissionsToRemove) {
            currentPermissions.stream()
                    .filter(p -> p.getPermission().getId().equals(permissionIdToRemove))
                    .findFirst().ifPresent(deletePermissionUserBusinessList::add);
        }

        service.create(addPermissionUserBusinessList);
        service.delete(deletePermissionUserBusinessList);

    }


    private boolean validate(UserPermissionBusinessDto payloadUpdate, UserPermissionBusinessDto toUpdate) {
        return !(payloadUpdate.getBusiness().getId().equals(toUpdate.getBusiness().getId()) &&
                payloadUpdate.getPermission().getId().equals(toUpdate.getPermission().getId()) &&
                payloadUpdate.getUser().getId().equals(toUpdate.getUser().getId()));
    }
}