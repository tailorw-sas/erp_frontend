package com.kynsof.identity.application.command.geographiclocations.delete;

import com.kynsof.identity.domain.dto.GeographicLocationDto;
import com.kynsof.identity.domain.interfaces.service.IGeographicLocationService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

@Component
public class DeleteGeographicLocationsCommandHandler implements ICommandHandler<DeleteGeographicLocationsCommand> {

    private final IGeographicLocationService service;

    public DeleteGeographicLocationsCommandHandler(IGeographicLocationService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteGeographicLocationsCommand command) {
        GeographicLocationDto delete = this.service.findById(command.getId());

        service.delete(delete);
    }

}
