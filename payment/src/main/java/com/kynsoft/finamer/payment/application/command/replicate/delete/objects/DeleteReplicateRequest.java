package com.kynsoft.finamer.payment.application.command.replicate.delete.objects;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteReplicateRequest {
    private List<DeleteObjectEnum> objects;
}
