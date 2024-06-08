package com.kynsof.share.core.domain.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class PatientKafka implements Serializable {

    private String id;
    private String identification;
    private String name;
    private String lastName;
    private String gender;
    private String status;
    private String logo;
    private String birthdayDate;

    public PatientKafka(String id, String identification, String name, String lastName, String gender, String status, String logo) {
        this.id = id;
        this.identification = identification;
        this.name = name;
        this.lastName = lastName;
        this.gender = gender;
        this.status = status;
        this.logo = logo;
    }

}
