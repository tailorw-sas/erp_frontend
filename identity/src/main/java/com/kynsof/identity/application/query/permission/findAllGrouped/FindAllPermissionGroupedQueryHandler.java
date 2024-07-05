package com.kynsof.identity.application.query.permission.findAllGrouped;

import com.kynsof.identity.domain.dto.PermissionDto;
import com.kynsof.identity.domain.interfaces.service.IPermissionService;
import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FindAllPermissionGroupedQueryHandler implements IQueryHandler<FindAllPermissionGroupedQuery, PermissionGroupedResponse> {

    private final IPermissionService service;

    public FindAllPermissionGroupedQueryHandler(IPermissionService service) {
        this.service = service;
    }

    @Override
    public PermissionGroupedResponse handle(FindAllPermissionGroupedQuery query) {
        List<PermissionDto> dtoList = this.service.findAll();

        Map<String, List<PermissionDto>> groupedInstances = dtoList.stream()
                .collect(Collectors.groupingBy(
                        instance -> instance.getModule().getName()
                ));

        List<PermissionModuleResponse> moduleResponses = groupedInstances.entrySet().stream()
                .map(moduleEntry -> new PermissionModuleResponse(
                        moduleEntry.getKey(),
                        moduleEntry.getValue().stream().map(
                                PermissionBasicResponse::new
                        ).collect(Collectors.toList())
                )).collect(Collectors.toList());

        return new PermissionGroupedResponse(moduleResponses);
    }
}
