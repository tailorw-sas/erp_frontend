package com.kynsof.share.core.domain.kafka.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserKafka implements Serializable {

    private String id;
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private String identification;
    private String phone;
    private String gender;
    private String status;
    private String birthdayDate;

    public UserKafka(String id, String username, String email, String firstname, String lastname, String identification, String phone, String gender, String status) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.identification = identification;
        this.phone = phone;
        this.gender = gender;
        this.status = status;
    }

}
