package com.kynsoft.finamer.settings.infrastructure.projections;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageCountry;
import com.kynsoft.finamer.settings.infrastructure.identity.ManagerTimeZone;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManageCityStateProjection {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private ManageCountryProjection country;
    //private ManagerTimeZone timeZone;
    private Status status;
}
