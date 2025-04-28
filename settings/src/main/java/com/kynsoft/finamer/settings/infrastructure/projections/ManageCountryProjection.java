package com.kynsoft.finamer.settings.infrastructure.projections;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageAgency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManageCountryProjection {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private String dialCode;
    private String iso3;
    private Boolean isDefault;
    //private ManagerLanguage managerLanguage;
    private Status status;
    private LocalDateTime deleteAt;
    private List<ManageAgency> agencies;
}
