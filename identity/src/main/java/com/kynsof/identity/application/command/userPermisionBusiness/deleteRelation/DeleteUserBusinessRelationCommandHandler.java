package com.kynsof.identity.application.command.userPermisionBusiness.deleteRelation;

import com.kynsof.identity.domain.dto.UserPermissionBusinessDto;
import com.kynsof.identity.domain.interfaces.service.IUserPermissionBusinessService;
import com.kynsof.identity.infrastructure.services.kafka.producer.userBusiness.ProducerDeleteUserBusinessRelationEventService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeleteUserBusinessRelationCommandHandler implements ICommandHandler<DeleteUserBusinessRelationCommand> {

    private final IUserPermissionBusinessService service;
    private final ProducerDeleteUserBusinessRelationEventService deleteUserBusinessRelationEventService;

    public DeleteUserBusinessRelationCommandHandler(IUserPermissionBusinessService service,
            ProducerDeleteUserBusinessRelationEventService deleteUserBusinessRelationEventService) {
        this.service = service;
        this.deleteUserBusinessRelationEventService = deleteUserBusinessRelationEventService;
    }

    @Override
    public void handle(DeleteUserBusinessRelationCommand command) {
        // Recuperar el estado actual
        List<UserPermissionBusinessDto> currentPermissions = service.findByUserAndBusiness(command.getUserId(),
                command.getBusinessId())
                .stream()
                .toList();
        if (!currentPermissions.isEmpty()) {
            this.service.delete(currentPermissions);
            this.deleteUserBusinessRelationEventService.delete(currentPermissions.get(0).getUser().getId(), currentPermissions.get(0).getBusiness().getId());
        }
    }
}
