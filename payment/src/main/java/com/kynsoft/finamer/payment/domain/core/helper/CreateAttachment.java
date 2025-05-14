package com.kynsoft.finamer.payment.domain.core.helper;

import com.kynsoft.finamer.payment.domain.dto.AttachmentTypeDto;
import com.kynsoft.finamer.payment.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateAttachment {

    private Status status;
    private ResourceTypeDto manageResourceTypeDto;
    private AttachmentTypeDto manageAttachmentTypeDto;
    private String fileName;
    private String fileWeight;
    private String path;
    private String remark;
    private boolean support;
}
