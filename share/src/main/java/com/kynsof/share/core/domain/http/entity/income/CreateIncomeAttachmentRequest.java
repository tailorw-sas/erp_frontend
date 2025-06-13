package com.kynsof.share.core.domain.http.entity.income;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateIncomeAttachmentRequest {

    private String filename;
    private String file;
    private String remark;
    private UUID type;
    private String employee;
    private UUID employeeId;
    private UUID paymentResourceType;
}
