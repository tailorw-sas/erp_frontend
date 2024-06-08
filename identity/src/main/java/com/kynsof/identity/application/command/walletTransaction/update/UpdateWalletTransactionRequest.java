package com.kynsof.identity.application.command.walletTransaction.update;

import com.kynsof.identity.domain.dto.enumType.EBusinessStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateWalletTransactionRequest {
    private String name;    
    private String latitude;
    private String longitude;
    private String description;
    private byte [] image;
    private String ruc;
    private EBusinessStatus status;
    private UUID geographicLocation;
    private String address;
}