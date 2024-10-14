package com.kynsoft.report.applications.command.jasperReportParameter.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UpdateJasperReportParameterCommand implements ICommand {

    private UUID id;
    private final String paramName;
    private final String type;
    private final String module;
    private final String service;
    private final String label;
    private final UUID reportId;
    private final String componentType;

    public static UpdateJasperReportParameterCommand fromRequest(UpdateJasperReportParameterRequest request, UUID id) {
        return new UpdateJasperReportParameterCommand(
                id, request.getParamName(), request.getType(), request.getModule(),
                request.getService(), request.getLabel(), request.getReportId(), request.getComponentType()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateJasperReportParameterMessage(id);
    }
}
