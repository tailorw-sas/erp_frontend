package com.kynsof.identity.application.command.geographiclocations.create;

import com.kynsof.identity.domain.dto.GeographicLocationDto;
import com.kynsof.identity.domain.interfaces.service.IGeographicLocationService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateGeographicLocationCommandHandler implements ICommandHandler<CreateGeographicLocationCommand> {

    private final IGeographicLocationService geographicLocationService;

    @Autowired
    public CreateGeographicLocationCommandHandler(IGeographicLocationService geographicLocationService) {
        this.geographicLocationService = geographicLocationService;
    }

    @Override
    public void handle(CreateGeographicLocationCommand command) {
        GeographicLocationDto parent = null;
        if (command.getParent() != null)
            parent = this.geographicLocationService.findById(command.getParent());

        GeographicLocationDto geographicLocationDto = new GeographicLocationDto(
                command.getId(),
                command.getName(),
                command.getType(),
                parent
        );
        this.geographicLocationService.create(geographicLocationDto);

    }
}
