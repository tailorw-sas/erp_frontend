package com.kynsoft.finamer.settings.application.query.objectResponse.manageReportGroup;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageReportGroupedResponse implements IResponse {
    private List<ManageReportModuleResponse> response;
}
