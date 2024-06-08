package com.kynsof.share.core.domain.kafka.entity;

import com.kynsof.share.core.domain.EUserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSystemKafka {
    private UUID id;
    private String userName;
    private String email;
    private String name;
    private String lastName;
    private String idImage;
    private EUserType userType;
}
