package com.kynsof.identity.application.command.customer.deleteAll;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DeleteAllCustomerRequest {
    private List<UUID> customers;
}
