package com.kynsof.identity.application.query.customer.getbyid;

import com.kynsof.identity.domain.dto.CustomerDto;
import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CustomerResponse implements IResponse {

    private UUID id;
    private String name;
    private String lastName;
    private String email;
    private LocalDate createdAt;

    public CustomerResponse(CustomerDto customerDto) {
        this.id = customerDto.getId();
        this.name = customerDto.getFirstName();
        this.lastName = customerDto.getLastName();
        this.email = customerDto.getEmail();
        this.createdAt = customerDto.getCreatedAt().toLocalDate();
    }

}