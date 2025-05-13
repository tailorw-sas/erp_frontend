package com.kynsoft.finamer.invoicing.application.command.manageBooking.importInnsistBooking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportInnsistRequest {
    private UUID importInnsitProcessId;
    private String employee;
    private List<ImportInnsistBookingRequest> importList;
}
