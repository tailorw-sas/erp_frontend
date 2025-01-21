package com.kynsoft.finamer.insis.application.command.manageAgency.createMany;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.insis.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.insis.domain.services.IManageAgencyService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateManyManageAgencyCommandHandler implements ICommandHandler<CreateManyManageAgencyCommand> {

    private final IManageAgencyService service;

    public CreateManyManageAgencyCommandHandler(IManageAgencyService service){
        this.service = service;
    }

    @Override
    public void handle(CreateManyManageAgencyCommand command) {

        List<ManageAgencyDto> agencyDtos = command.getNewAgencies().stream()
                .map(agencyCommand -> new ManageAgencyDto(
                        agencyCommand.getId(),
                        agencyCommand.getCode(),
                        agencyCommand.getName(),
                        agencyCommand.getAgencyAlias(),
                        agencyCommand.getStatus(),
                        false,
                        null
                ))
                .toList();

        service.createMany(agencyDtos);
    }
}
