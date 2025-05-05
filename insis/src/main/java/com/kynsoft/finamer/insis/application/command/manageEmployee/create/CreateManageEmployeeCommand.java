package com.kynsoft.finamer.insis.application.command.manageEmployee.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateManageEmployeeCommand implements ICommand {

    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime updatedAt;
    private List<UUID> hotels;
    private List<UUID> agencies;

    public CreateManageEmployeeCommand(UUID id, String firstName, String lastName, String email, List<UUID> hotels, List<UUID> agencies){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.updatedAt = LocalDateTime.now();
        this.hotels = hotels;
        this.agencies = agencies;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateManageEmployeeMessage(id);
    }
}
