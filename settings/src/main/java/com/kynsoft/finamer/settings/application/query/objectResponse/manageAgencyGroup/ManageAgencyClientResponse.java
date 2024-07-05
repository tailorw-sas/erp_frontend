package com.kynsoft.finamer.settings.application.query.objectResponse.manageAgencyGroup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManageAgencyClientResponse {

    private String clientName;
    private List<ManageAgencyBasicResponse> agencies;
}
