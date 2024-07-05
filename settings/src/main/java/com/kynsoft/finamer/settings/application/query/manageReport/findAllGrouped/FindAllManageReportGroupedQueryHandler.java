package com.kynsoft.finamer.settings.application.query.manageReport.findAllGrouped;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.settings.application.query.objectResponse.ManageReportResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageReportGroup.ManageReportGroupedResponse;
import com.kynsoft.finamer.settings.application.query.objectResponse.manageReportGroup.ManageReportModuleResponse;
import com.kynsoft.finamer.settings.domain.dto.ManageReportDto;
import com.kynsoft.finamer.settings.domain.services.IManageReportService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FindAllManageReportGroupedQueryHandler implements IQueryHandler<FindAllManageReportGroupedQuery, ManageReportGroupedResponse> {

    private final IManageReportService service;

    public FindAllManageReportGroupedQueryHandler(IManageReportService service) {
        this.service = service;
    }

    @Override
    public ManageReportGroupedResponse handle(FindAllManageReportGroupedQuery query) {
        List<ManageReportDto> dtoList = this.service.findAll();

        Map<String, List<ManageReportDto>> groupedInstances = dtoList.stream()
                .collect(Collectors.groupingBy(
                        instance -> instance.getModuleName()
                ));

        List<ManageReportModuleResponse> moduleResponses = groupedInstances.entrySet().stream()
                .map(countryEntry -> new ManageReportModuleResponse(
                        countryEntry.getKey(),
                        countryEntry.getValue().stream().map(
                                ManageReportResponse::new
                        ).collect(Collectors.toList())
                )).collect(Collectors.toList());

        return new ManageReportGroupedResponse(moduleResponses);
    }
}
