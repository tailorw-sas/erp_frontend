package com.kynsoft.finamer.settings.application.query.objectResponse.manageReportGroup;

import com.kynsoft.finamer.settings.application.query.objectResponse.ManageReportResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageReportModuleResponse {

    private String module;
    private List<ManageReportResponse> reports;
}
