package com.kynsoft.finamer.invoicing.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDto {
    private UUID id;
    private String templateCode;
    private String name;
    private String languageCode;
    private String type;
}