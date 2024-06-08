package com.kynsof.identity.application.command.userPermisionBusiness.delete;

import com.kynsof.identity.domain.dto.UserPermissionBusinessDto;
import com.kynsof.identity.domain.interfaces.service.IUserPermissionBusinessService;
import com.kynsof.identity.infrastructure.services.kafka.producer.userBusiness.ProducerDeleteUserBusinessRelationEventService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class DeleteUserRolBusinessCommandHandler implements ICommandHandler<DeleteUserRolBusinessCommand> {

    private final IUserPermissionBusinessService serviceImpl;
    private final ProducerDeleteUserBusinessRelationEventService deleteUserBusinessRelationEventService;

    public DeleteUserRolBusinessCommandHandler(IUserPermissionBusinessService serviceImpl,
                                               ProducerDeleteUserBusinessRelationEventService deleteUserBusinessRelationEventService) {
        this.serviceImpl = serviceImpl;
        this.deleteUserBusinessRelationEventService = deleteUserBusinessRelationEventService;
    }

    @Override
    public void handle(DeleteUserRolBusinessCommand command) {

        UserPermissionBusinessDto delete = this.serviceImpl.findById(command.getId());
        delete.setDeleted(true);
        serviceImpl.delete(delete);
        if (this.serviceImpl.countByUserAndBusinessNotDeleted(delete.getUser().getId(), delete.getBusiness().getId()) == 0) {
            this.deleteUserBusinessRelationEventService.delete(delete.getUser().getId(), delete.getBusiness().getId());
        }
    }

}
