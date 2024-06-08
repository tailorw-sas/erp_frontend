package com.kynsof.share.core.domain.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactInfoKafka implements Serializable {
    private UUID idPatient;
    private String email;
    private String telephone;
    private String address;
    private String birthdayDate;
}
