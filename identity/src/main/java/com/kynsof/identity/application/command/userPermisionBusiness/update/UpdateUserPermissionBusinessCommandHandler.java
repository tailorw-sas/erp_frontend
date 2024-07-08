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

import java.util.List;
import java.util.UUID;

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

        UserSystemDto userSystemDto = userSystemService.findById(updateRequest.getUserId());
        BusinessDto businessDto = businessService.findById(updateRequest.getBusinessId());
        List<UserPermissionBusinessDto> addPermissionUserBusinessList = new java.util.ArrayList<>(List.of());
        for (UUID permissionIdToAdd : command.getPayload().getPermissionIds()) {
            PermissionDto permissionDto = permissionService.findById(permissionIdToAdd);
            UserPermissionBusinessDto newUserPermissionBusiness = new UserPermissionBusinessDto();
            newUserPermissionBusiness.setId(UUID.randomUUID());
            newUserPermissionBusiness.setUser(userSystemDto);
            newUserPermissionBusiness.setPermission(permissionDto);
            newUserPermissionBusiness.setBusiness(businessDto);
            addPermissionUserBusinessList.add(newUserPermissionBusiness);
        }

        service.delete(updateRequest.getBusinessId(), updateRequest.getUserId());

        service.create(addPermissionUserBusinessList);

    }
}