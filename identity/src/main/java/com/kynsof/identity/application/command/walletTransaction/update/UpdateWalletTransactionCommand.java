package com.kynsof.identity.application.command.walletTransaction.update;

import com.kynsof.identity.domain.dto.enumType.EBusinessStatus;
import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateWalletTransactionCommand implements ICommand {

    private UUID id;
    private String name;
    private String latitude;
    private String longitude;
    private String description;
    private byte [] logo;
    private String ruc;
    private EBusinessStatus status;
    private UUID geographicLocation;
    private String address;

    public UpdateWalletTransactionCommand(UUID id, String name, String latitude, String longitude, String description, byte [] logo, String ruc, EBusinessStatus status, UUID geographicLocation, String address) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.logo = logo;
        this.ruc = ruc;
        this.status = status;
        this.geographicLocation = geographicLocation;
        this.address = address;
    }

    public static UpdateWalletTransactionCommand fromRequest(UpdateWalletTransactionRequest request, UUID id) {
        return new UpdateWalletTransactionCommand(
                id,
                request.getName(), 
                request.getLatitude(),
                request.getLongitude(), 
                request.getDescription(), 
                request.getImage(),
                request.getRuc(), 
                request.getStatus(), 
                request.getGeographicLocation(),
                request.getAddress()
                );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateWalletTransactionMessage(id);
    }
}
