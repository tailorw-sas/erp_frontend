package com.kynsoft.finamer.invoicing.application.command.manageInvoice.send;

import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.util.List;

// Clase auxiliar que encapsula el PDF y la información relevante para el envío
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedInvoice {
    private ByteArrayOutputStream pdfStream;
    private String nameFile;
    private String ip;
    private String userName;
    private String password;
    private List<ManageInvoiceDto> invoices; // Lista de facturas generadas en el PDF
}