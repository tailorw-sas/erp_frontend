package com.kynsof.identity.application.command.businessModule.deleteall;

import com.kynsof.identity.domain.dto.BusinessModuleDto;
import com.kynsof.identity.domain.interfaces.service.IBusinessModuleService;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeleteAllBusinessModuleCommandHandler implements ICommandHandler<DeleteAllBusinessModuleCommand> {

    private final IBusinessModuleService service;

    public DeleteAllBusinessModuleCommandHandler(IBusinessModuleService service) {
        this.service = service;
    }

    @Override
    public void handle(DeleteAllBusinessModuleCommand command) {
        List<BusinessModuleDto> modulesToDelete = command.getBusinessModules()
           .stream() // Convertimos la colección a Stream para poder usar métodos de flujo
           .map(service::findById) // Usamos map para transformar cada UUID a BusinessModuleDto
           .collect(Collectors.toList()); // Recogemos los resultados en una nueva lista
        service.delete(modulesToDelete);
    }

}
