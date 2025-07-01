package com.kynsof.share.core.domain.http.entity.income.attachment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateAntiToIncomeAttachmentRequest {

    private String file;
    private String employee;
    private UUID employeeId;
}
