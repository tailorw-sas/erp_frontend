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
    private AttachmentTypeDto attachmentTypeDto;
    private ResourceTypeDto resourceTypeDto;
    private String fileName;
    private String fileWeight;
    private String path;
    private String remark;
    private boolean support;
}
