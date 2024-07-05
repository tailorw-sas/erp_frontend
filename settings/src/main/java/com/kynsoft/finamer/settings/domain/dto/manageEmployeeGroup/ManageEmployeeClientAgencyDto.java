package com.kynsoft.finamer.settings.domain.dto.manageEmployeeGroup;

import com.kynsoft.finamer.settings.domain.dto.ManageAgencyDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageEmployeeClientAgencyDto {

    private String clientName;
    private List<ManageAgencyDto> agencies;
}
